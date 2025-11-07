package id.polije.simpelsi.Tps; // ⚠️ Sesuaikan package Anda

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.polije.simpelsi.R;

public class TpsAdapter extends RecyclerView.Adapter<TpsAdapter.ViewHolder> {

    private final Context context;
    private final List<Tps> tpsList;
    private final List<Tps> tpsListFull;

    public TpsAdapter(Context context, List<Tps> tpsList) {
        this.context = context;
        this.tpsList = tpsList;
        this.tpsListFull = new ArrayList<>(tpsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ❗️ Pastikan Anda memiliki layout item_tps.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_tps, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tps tps = tpsList.get(position);
        if (tps == null) return;

        holder.tvNama.setText(tps.getNamaTps());
        holder.tvLokasi.setText(tps.getAlamat()); // Tampilkan alamat lengkap di list

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailTPSActivity.class);
            // Kirim seluruh objek Tps
            intent.putExtra("TPS_DATA", tps);
            context.startActivity(intent);
            // Hapus overridePendingTransition dari adapter
        });
    }

    @Override
    public int getItemCount() {
        return tpsList.size();
    }

    public void updateData(List<Tps> newData) {
        tpsList.clear();
        tpsList.addAll(newData);
        tpsListFull.clear();
        tpsListFull.addAll(newData);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        tpsList.clear();
        if (query.isEmpty()) {
            tpsList.addAll(tpsListFull);
        } else {
            query = query.toLowerCase().trim();
            for (Tps tps : tpsListFull) {
                // Filter berdasarkan nama, lokasi, atau alamat
                if (tps.getNamaTps().toLowerCase().contains(query) ||
                        tps.getLokasi().toLowerCase().contains(query) ||
                        tps.getAlamat().toLowerCase().contains(query)) {
                    tpsList.add(tps);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvLokasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // ❗️ Pastikan ID ini ada di item_tps.xml
            tvNama = itemView.findViewById(R.id.tvNamaTPS);
            tvLokasi = itemView.findViewById(R.id.tvLokasiTPS);
        }
    }
}