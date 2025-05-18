package com.example.splashscore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScorerAdapter
        extends RecyclerView.Adapter<ScorerAdapter.VH> {

    private final List<Scorer> scorers;

    public ScorerAdapter(List<Scorer> scorers) {
        this.scorers = scorers;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // most használjunk egy kétsoros list itemt:
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        Scorer s = scorers.get(pos);
        holder.name.setText(s.getName());
        holder.goals.setText(String.valueOf(s.getGoals()));
    }

    @Override
    public int getItemCount() {
        return scorers.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, goals;
        VH(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(android.R.id.text1);
            goals = itemView.findViewById(android.R.id.text2);
        }
    }
}

