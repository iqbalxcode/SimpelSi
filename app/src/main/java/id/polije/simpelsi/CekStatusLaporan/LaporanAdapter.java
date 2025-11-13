package id.polije.simpelsi.CekStatusLaporan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;
import id.polije.simpelsi.api.ApiInterface;
import id.polije.simpelsi.fitur.EditLaporanActivity;
import id.polije.simpelsi.model.HapusRequest;
import id.polije.simpelsi.model.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {

    private final Context context;
    private final List<Laporan> laporanList;
    private final List<Laporan> laporanListFull;
    private final String idMasyarakat;
    private final ApiInterface apiInterface;

    public LaporanAdapter(Context context, List<Laporan> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
        this.laporanListFull = new ArrayList<>(laporanList);

        SharedPreferences prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        this.idMasyarakat = prefs.getString("id_masyarakat", null);

        this.apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laporan laporan = laporanList.get(position);
        if (laporan == null) return;

        holder.tvNama.setText("Nama : " + safeText(laporan.getNama()));
        holder.tvLokasi.setText("Lokasi : " + safeText(laporan.getLokasi()));
        holder.tvKeterangan.setText("Keterangan : " + safeText(laporan.getKeterangan()));
        holder.tvTanggal.setText("Tanggal : " + safeText(laporan.getTanggal()));

        // === Status ===
        String status = laporan.getStatusLaporan() != null ? laporan.getStatusLaporan() : "Diproses";
        holder.tvStatus.setText(status);

        switch (status.toLowerCase()) {
            case "diterima":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_diterima);
                break;
            case "ditolak":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_ditolak);
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_diproses);
                break;
        }

        // === Gambar ===
        String foto = laporan.getFoto();
        if (foto != null && !foto.trim().isEmpty()) {
            String url = ApiClient.BASE_URL + "get_image.php?file=" + foto;
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(holder.imgLaporan);
        } else {
            holder.imgLaporan.setImageResource(R.drawable.loading);
        }

        // === Atur visibilitas tombol berdasarkan status ===
        switch (status.toLowerCase()) {
            case "diproses":
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnHapus.setVisibility(View.VISIBLE);
                break;
            case "ditolak":
                holder.btnEdit.setVisibility(View.GONE);   // tidak bisa edit
                holder.btnHapus.setVisibility(View.VISIBLE); // bisa hapus
                break;
            case "diterima":
            default:
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnHapus.setVisibility(View.GONE);
                break;
        }

        // === Tombol Edit ===
        holder.btnEdit.setOnClickListener(v -> {
            if (isWithinOneHour(laporan.getCreated_at())) {
                Intent intent = new Intent(context, EditLaporanActivity.class);
                intent.putExtra("LAPORAN_DATA", laporan);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Sudah lewat 1 jam, laporan tidak bisa diedit.", Toast.LENGTH_SHORT).show();
            }
        });

        // === Tombol Hapus ===
        holder.btnHapus.setOnClickListener(v -> {
            if (isWithinOneHour(laporan.getCreated_at())) {
                new AlertDialog.Builder(context)
                        .setTitle("Tarik Laporan")
                        .setMessage("Apakah Anda yakin ingin menghapus laporan ini?")
                        .setPositiveButton("Hapus", (dialog, which) ->
                                tarikLaporan(laporan.getIdLaporan(), position))
                        .setNegativeButton("Batal", null)
                        .show();
            } else {
                Toast.makeText(context, "Sudah lewat 1 jam, laporan tidak bisa dihapus.", Toast.LENGTH_SHORT).show();
            }
        });

        // === Klik item buka Detail ===
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailTanggapanActivity.class);
            intent.putExtra("id_laporan", laporan.getIdLaporan());
            context.startActivity(intent);
        });
    }


    private boolean isWithinOneHour(String createdAtTimestamp) {
        if (createdAtTimestamp == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date tanggal = sdf.parse(createdAtTimestamp);
            long diff = System.currentTimeMillis() - tanggal.getTime();
            return diff < 3600 * 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void tarikLaporan(String idLaporan, int position) {
        if (idMasyarakat == null) {
            Toast.makeText(context, "ID Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Menarik laporan...");
        pd.show();

        HapusRequest request = new HapusRequest(idLaporan, idMasyarakat);
        apiInterface.tarikLaporan(request).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    laporanList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    Toast.makeText(context, "Laporan berhasil ditarik", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Gagal menarik laporan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public void updateData(List<Laporan> newData) {
        laporanList.clear();
        laporanList.addAll(newData);
        laporanListFull.clear();
        laporanListFull.addAll(newData);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        laporanList.clear();
        if (text == null || text.isEmpty()) {
            laporanList.addAll(laporanListFull);
        } else {
            text = text.toLowerCase();
            for (Laporan item : laporanListFull) {
                if (item.getNama() != null && item.getNama().toLowerCase().contains(text)) {
                    laporanList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    private String safeText(String text) {
        return text != null && !text.isEmpty() ? text : "-";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLaporan;
        TextView tvNama, tvLokasi, tvKeterangan, tvTanggal, tvStatus;
        Button btnEdit, btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLaporan = itemView.findViewById(R.id.imgLaporan);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvKeterangan = itemView.findViewById(R.id.tvKeterangan);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }
}
