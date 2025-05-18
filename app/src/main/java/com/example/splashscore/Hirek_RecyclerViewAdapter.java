package com.example.splashscore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter a hírekhez, amely:
 * - Két listát tart karban: allNews (összes adat) és filteredNews (szűrt adat)
 * - Megvalósítja a Filterable interfészt a SearchView számára
 */
public class Hirek_RecyclerViewAdapter
        extends RecyclerView.Adapter<Hirek_RecyclerViewAdapter.MyViewHolder>
        implements Filterable {

    private final Hirek_RecyclerViewAdapter.OnDeleteListener onDeleteListener;

    interface OnDeleteListener {
        void onDelete(String docId);
    }

    private final List<HirekModel> allNews;      // az összes betöltött hír
    private final List<HirekModel> filteredNews; // a jelenleg megjelenített lista
    private final String currentEmail;

    public Hirek_RecyclerViewAdapter(ArrayList<HirekModel> hirekModels, String currentEmail, OnDeleteListener deleteListener) {
        // Másolatokat készítünk, hogy ne módosuljon a külső lista
        this.allNews = new ArrayList<>(hirekModels);
        this.filteredNews = new ArrayList<>(hirekModels);
        this.currentEmail = currentEmail;
        this.onDeleteListener = deleteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hirekitem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HirekModel item = filteredNews.get(position);
        holder.tvNewsTitle.setText(item.getTitle());
        holder.tvNewsSummary.setText(item.getSummary());
        holder.tvNewsDate.setText(item.getDate());
        holder.uploaderText.setText("Közzétette: " + item.getUploaderEmail());
        // ha a feltöltő egyezik, mutatjuk a törlés gombot
        if (item.getUploaderEmail().equals(currentEmail)) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v ->
                    onDeleteListener.onDelete(item.getId()));
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredNews.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? ""
                        : constraint.toString().toLowerCase().trim();
                List<HirekModel> temp = new ArrayList<>();
                if (query.isEmpty()) {
                    temp.addAll(allNews);
                } else {
                    for (HirekModel hm : allNews) {
                        if (hm.getTitle().toLowerCase().contains(query)
                                || hm.getSummary().toLowerCase().contains(query)) {
                            temp.add(hm);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = temp;
                return results;
            }
            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNews.clear();
                filteredNews.addAll((List<HirekModel>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Új adatok beállítása (pl. új Snapshot érkezett).
     * Ez felülírja az allNews listát, és újra lefuttatja a jelenlegi szűrést.
     */
    public void updateData(List<HirekModel> newList) {
        allNews.clear();
        allNews.addAll(newList);
        // újra szűrünk üres kéréssel, hogy visszaálljon a teljes lista, ha nincs query
        getFilter().filter("");
    }

    /** ViewHolder osztály */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNewsDate, tvNewsTitle, tvNewsSummary, uploaderText;
        ImageButton deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNewsDate    = itemView.findViewById(R.id.newsDateText);
            tvNewsTitle   = itemView.findViewById(R.id.newsTitleText);
            tvNewsSummary = itemView.findViewById(R.id.newsSummaryText);
            uploaderText  = itemView.findViewById(R.id.uploaderText);
            deleteButton  = itemView.findViewById(R.id.deleteButton);
        }
    }
}
