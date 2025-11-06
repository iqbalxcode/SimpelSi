package id.polije.simpelsi.CekStatusLaporan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import id.polije.simpelsi.R;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {
    private final Context context;
    private final List<Laporan> laporanList;
    private final List<Laporan> laporanListFull;

    public LaporanAdapter(Context context, List<Laporan> laporanList) {
        this.context = context;
        this.laporanList = laporanList != null ? laporanList : new ArrayList<>();
        this.laporanListFull = new ArrayList<>(this.laporanList);
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

        Log.d("ADAPTER_BIND", "Menampilkan laporan ke-" + position + ": " + laporan.getNama());

        holder.tvNama.setText("Nama : " + safeText(laporan.getNama()));
        holder.tvLokasi.setText("Lokasi : " + safeText(laporan.getLokasi()));
        holder.tvKeterangan.setText("Keterangan : " + safeText(laporan.getKeterangan()));
        holder.tvTanggal.setText("Tanggal : " + safeText(laporan.getTanggal()));

        // Status
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

        // FOTO URL LANGSUNG dari API
        String fotoUrl = laporan.getFotoUrl();
        if (fotoUrl != null && !fotoUrl.trim().isEmpty()) {
            Glide.with(context)
                    .load(fotoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(holder.imgLaporan);
        } else {
            holder.imgLaporan.setImageResource(R.drawable.loading);
        }
    }

    @Override
    public int getItemCount() {
        int count = laporanList != null ? laporanList.size() : 0;
        Log.d("ADAPTER_COUNT", "Jumlah item di adapter: " + count);
        return count;
    }

    // ✅ Memperbarui data adapter
    public void updateData(List<Laporan> newData) {
        if (newData == null) {
            Log.e("ADAPTER", "updateData: data baru null!");
            laporanList.clear();
            laporanListFull.clear();
            notifyDataSetChanged();
            return;
        }

        Log.d("ADAPTER", "updateData dipanggil, jumlah data: " + newData.size());
        laporanList.clear();
        laporanList.addAll(newData);
        laporanListFull.clear();
        laporanListFull.addAll(newData);
        notifyDataSetChanged();
    }

    // ✅ Menambahkan fitur filter/pencarian
    public void filter(String text) {
        laporanList.clear();
        if (text == null || text.isEmpty()) {
            laporanList.addAll(laporanListFull);
        } else {
            text = text.toLowerCase();
            for (Laporan item : laporanListFull) {
                if ((item.getNama() != null && item.getNama().toLowerCase().contains(text)) ||
                        (item.getLokasi() != null && item.getLokasi().toLowerCase().contains(text)) ||
                        (item.getKeterangan() != null && item.getKeterangan().toLowerCase().contains(text))) {
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
