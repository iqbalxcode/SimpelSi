package id.polije.simpelsi.artikel; // ⚠️ Sesuaikan package Anda

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.io.Serializable;
import java.util.List;
import id.polije.simpelsi.R;
import id.polije.simpelsi.api.ApiClient;

public class ArtikelTrendingAdapter extends RecyclerView.Adapter<ArtikelTrendingAdapter.ViewHolder> {

    private final Context context;
    private final List<Artikel> artikelList;

    public ArtikelTrendingAdapter(Context context, List<Artikel> artikelList) {
        this.context = context;
        this.artikelList = artikelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artikel_trending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artikel artikel = artikelList.get(position);
        if (artikel == null) return;

        holder.tvJudul.setText(artikel.getJudul());

        // Logika Foto (Gunakan Proxy)
        String namaFileFoto = artikel.getFoto();
        if (namaFileFoto != null && !namaFileFoto.trim().isEmpty()) {
            String urlProxy = ApiClient.BASE_URL + "get_image.php?file=" + namaFileFoto + "&tipe=artikel";
            Log.d("ArtikelTrendingAdapter", "Memuat URL: " + urlProxy);
            Glide.with(context)
                    .load(urlProxy)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(holder.imgArtikel);
        } else {
            holder.imgArtikel.setImageResource(R.drawable.loading);
        }

        // Click Listener untuk membuka Detail
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;
            
            Artikel artikelDiklik = artikelList.get(currentPosition);
            Intent intent = new Intent(context, ArtikelDetailActivity.class);
            intent.putExtra("ARTIKEL_DATA", (Serializable) artikelDiklik);
            context.startActivity(intent);
        });
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
        TextView tvJudul;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArtikel = itemView.findViewById(R.id.imgTrendingArtikel);
            tvJudul = itemView.findViewById(R.id.tvTrendingJudul);
        }
    }
}