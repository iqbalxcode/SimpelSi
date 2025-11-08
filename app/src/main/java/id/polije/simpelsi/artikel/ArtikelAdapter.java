package id.polije.simpelsi.artikel;

import android.content.Context;
import android.content.Intent; // ❗️ Import Intent
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.io.Serializable; // ❗️ Import Serializable
import java.util.List;
import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.ViewHolder> {

    private final Context context;
    private final List<Artikel> artikelList;

    public ArtikelAdapter(Context context, List<Artikel> artikelList) {
        this.context = context;
        this.artikelList = artikelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artikel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artikel artikel = artikelList.get(position);
        if (artikel == null) return;

        holder.tvJudul.setText(artikel.getJudul());
        holder.tvTanggal.setText(artikel.getTanggal()); // Asumsi format tanggal sudah benar

        // --- (Logika Foto Anda sudah benar) ---
        String namaFileFoto = artikel.getFoto();
        if (namaFileFoto != null && !namaFileFoto.trim().isEmpty()) {
            String urlProxy = ApiClient.BASE_URL + "get_image.php?file=" + namaFileFoto + "&tipe=artikel";
            Log.d("ArtikelAdapter", "Memuat URL Proxy: " + urlProxy);
            Glide.with(context)
                    .load(urlProxy)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(holder.imgArtikel);
        } else {
            Log.w("ArtikelAdapter", "Nama file foto kosong/null.");
            holder.imgArtikel.setImageResource(R.drawable.loading);
        }

        // --- ⬇️ PERBAIKAN: TAMBAHKAN OnClickListener DI SINI ⬇️ ---
        holder.itemView.setOnClickListener(v -> {
            // Ambil posisi yang aman (anti-crash saat item dihapus)
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            // Ambil data artikel yang diklik
            Artikel artikelDiklik = artikelList.get(currentPosition);

            // Buat Intent untuk pindah ke ArtikelDetailActivity
            Intent intent = new Intent(context, ArtikelDetailActivity.class);

            // Kirim seluruh objek Artikel
            // ❗️ PENTING: Pastikan class Artikel Anda sudah "implements Serializable"
            intent.putExtra("ARTIKEL_DATA", (Serializable) artikelDiklik);

            // Mulai Activity baru
            context.startActivity(intent);
        });
        // --- ⬆️ AKHIR PERBAIKAN ⬆️ ---
    }

    @Override
    public int getItemCount() {
        return artikelList.size();
    }

    // Method untuk update data
    public void updateData(List<Artikel> newData) {
        artikelList.clear();
        artikelList.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArtikel;
        TextView tvJudul, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArtikel = itemView.findViewById(R.id.imgArtikel);
            tvJudul = itemView.findViewById(R.id.tvJudulArtikel);
            tvTanggal = itemView.findViewById(R.id.tvTanggalArtikel);
        }
    }
}