package id.polije.simpelsi.CekStatusLaporan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
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
    private String idMasyarakat;
    private ApiInterface apiInterface; // ❗️ Tambahkan ApiInterface

    public LaporanAdapter(Context context, List<Laporan> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
        this.laporanListFull = new ArrayList<>(laporanList);

        SharedPreferences prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        this.idMasyarakat = prefs.getString("id_masyarakat", null);

        // ❗️ Inisialisasi ApiInterface di sini
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
        // Ambil data HANYA untuk tampilan teks
        Laporan laporan = laporanList.get(position);
        if (laporan == null) return;

        holder.tvNama.setText("Nama : " + safeText(laporan.getNama()));
        holder.tvLokasi.setText("Lokasi : " + safeText(laporan.getLokasi()));
        holder.tvKeterangan.setText("Keterangan : " + safeText(laporan.getKeterangan()));
        holder.tvTanggal.setText("Tanggal : " + safeText(laporan.getTanggal()));

        // --- 1. LOGIKA STATUS ---
        String status = laporan.getStatusLaporan();
        if (status == null || status.isEmpty()) {
            status = "Diproses";
        }
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

        // --- 2. LOGIKA FOTO ---
        String namaFileFoto = laporan.getFoto();
        if (namaFileFoto != null && !namaFileFoto.trim().isEmpty()) {
            String urlProxy = ApiClient.BASE_URL + "get_image.php?file=" + namaFileFoto;
            Log.d("LaporanAdapter", "Memuat URL Proxy: " + urlProxy);
            Glide.with(context).load(urlProxy).centerCrop().placeholder(R.drawable.loading).error(R.drawable.loading).into(holder.imgLaporan);
        } else {
            Log.w("LaporanAdapter", "Nama file foto kosong/null.");
            holder.imgLaporan.setImageResource(R.drawable.loading);
        }

        // --- 3. LOGIKA TOMBOL (Hanya aktifkan visual) ---
        // Pengecekan waktu akan dilakukan SAAT DIKLIK
        holder.btnEdit.setEnabled(true);
        holder.btnHapus.setEnabled(true);
        holder.btnEdit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey_text));
        holder.btnHapus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));


        // --- ⬇️ PERBAIKAN LISTENER UNTUK ERROR LAMBDA ⬇️ ---

        holder.btnEdit.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Dapatkan posisi saat diklik
            if (currentPosition == RecyclerView.NO_POSITION) return;
            Laporan laporanSaatDiklik = laporanList.get(currentPosition);

            // ❗️ Logika Pengecekan Waktu DIPINDAHKAN KE SINI
            if (isWithinOneHour(laporanSaatDiklik.getCreated_at())) {
                Intent intent = new Intent(context, EditLaporanActivity.class);
                // Pastikan Laporan.java implements Serializable
                intent.putExtra("LAPORAN_DATA", (Serializable) laporanSaatDiklik);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Sudah lewat 1 jam, laporan tidak bisa diedit.", Toast.LENGTH_SHORT).show();
                // (Opsional) Nonaktifkan tombol secara visual jika sudah diklik sekali
                holder.btnEdit.setEnabled(false);
                holder.btnEdit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey_text));
            }
        });

        holder.btnHapus.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;
            Laporan laporanSaatDiklik = laporanList.get(currentPosition);

            // ❗️ Logika Pengecekan Waktu DIPINDAHKAN KE SINI
            if (isWithinOneHour(laporanSaatDiklik.getCreated_at())) {
                new AlertDialog.Builder(context)
                        .setTitle("Tarik Laporan") // ❗️ Ganti Judul
                        .setMessage("Apakah Anda yakin ingin menarik laporan ini?") // ❗️ Ganti Pesan
                        .setPositiveButton("Tarik", (dialog, which) -> { // ❗️ Ganti Teks Tombol
                            // Panggil API hapus/tarik dengan ID dan posisi yang benar
                            tarikLaporan(laporanSaatDiklik.getIdLaporan(), currentPosition); // ❗️ Ganti nama method
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            } else {
                Toast.makeText(context, "Sudah lewat 1 jam, laporan tidak bisa ditarik.", Toast.LENGTH_SHORT).show();
                // (Opsional) Nonaktifkan tombol secara visual jika sudah diklik sekali
                holder.btnHapus.setEnabled(false);
                holder.btnHapus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey_text));
            }
        });
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---
    }

    /**
     * Method Helper BARU untuk mengecek batas waktu 1 jam
     */
    private boolean isWithinOneHour(String createdAtTimestamp) {
        if (createdAtTimestamp == null) return false;

        try {
            // Format timestamp dari database (yyyy-MM-dd HH:mm:ss)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date tanggalLaporan = sdf.parse(createdAtTimestamp);
            long waktuLaporan = tanggalLaporan.getTime();
            long waktuSekarang = System.currentTimeMillis();

            long selisihWaktu = waktuSekarang - waktuLaporan; // Selisih dalam milidetik
            long satuJam = 3600 * 1000; // 1 jam dalam milidetik

            return selisihWaktu < satuJam; // Kembalikan true jika masih di bawah 1 jam

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TimeParseError", "Gagal parse timestamp: " + createdAtTimestamp);
            return false; // Anggap gagal jika format waktu salah
        }
    }

    /**
     * ❗️ PERBAIKAN: Method diganti namanya menjadi "tarikLaporan"
     */
    private void tarikLaporan(String idLaporan, int position) {
        if (idMasyarakat == null) {
            Toast.makeText(context, "ID Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Menarik laporan..."); // ❗️ Pesan diubah
        pd.show();

        HapusRequest request = new HapusRequest(idLaporan, idMasyarakat);

        // ❗️ Panggil "tarikLaporan" dari ApiInterface
        Call<ResponseModel> call = apiInterface.tarikLaporan(request);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if ("success".equals(response.body().getStatus())) {
                        // Hapus item dari list (di tampilan HP)
                        Laporan laporanDihapus = laporanList.get(position);
                        laporanList.remove(position);
                        laporanListFull.remove(laporanDihapus);

                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount()); // ❗️ Perbaikan: Gunakan getItemCount()
                    }
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
        return laporanList != null ? laporanList.size() : 0;
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