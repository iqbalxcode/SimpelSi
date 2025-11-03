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
import id.polije.simpelsi.api.ApiClient;

// ❗️ Pastikan 'Laporan' di sini adalah model class yang benar (Laporan.java atau LaporanModel.java)
//    yang sudah memiliki getter getStatus_laporan()
public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {
    private final Context context;
    private final List<Laporan> laporanList;
    private final List<Laporan> laporanListFull;

    public LaporanAdapter(Context context, List<Laporan> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
        this.laporanListFull = new ArrayList<>(laporanList);
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

        // --- ⬇️ PERBAIKAN DI SINI (LOGIKA STATUS) ⬇️ ---
        // Kita hanya panggil satu getter yang benar, sesuai model data
        String status = laporan.getStatus_laporan();

        if (status == null || status.isEmpty()) {
            status = "Diproses"; // Default
        }

        holder.tvStatus.setText(status);

        // Warna label status (kode ini sudah benar)
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
        // --- ⬆️ AKHIR PERBAIKAN STATUS ⬆️ ---


        // --- FOTO (Kode Anda di sini sudah benar) ---
        String namaFileFoto = laporan.getFoto();

        if (namaFileFoto != null && !namaFileFoto.trim().isEmpty()) {

            // Buat URL PROXY ke get_image.php
            String urlProxy = ApiClient.BASE_URL + "get_image.php?file=" + namaFileFoto;
            Log.d("LaporanAdapter", "Memuat URL Proxy: " + urlProxy);

            Glide.with(context)
                    .load(urlProxy)
                    .centerCrop()
                    .placeholder(R.drawable.img_desa_bagor)
                    .error(R.drawable.img_desa_bagor)
                    .into(holder.imgLaporan);
        } else {
            Log.w("LaporanAdapter", "Nama file foto kosong/null.");
            holder.imgLaporan.setImageResource(R.drawable.img_desa_bagor);
        }
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