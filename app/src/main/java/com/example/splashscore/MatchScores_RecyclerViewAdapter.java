package com.example.splashscore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchScores_RecyclerViewAdapter
        extends RecyclerView.Adapter<MatchScores_RecyclerViewAdapter.MyViewHolder> {

    /** Listener interfész, hogy a Fragment-be vissza tudd jelezni a click-et */
    public interface OnItemClickListener {
        void onItemClick(MatchScoreModel match);
    }

    private ArrayList<MatchScoreModel> matchScoreModels;
    private OnItemClickListener listener;

    public MatchScores_RecyclerViewAdapter(ArrayList<MatchScoreModel> matchScoreModels) {
        this.matchScoreModels = matchScoreModels;
    }

    /** Ezt hívd meg a Fragment-ből, hogy regisztráld a click eseményt */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.matchscoreitem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MatchScoreModel m = matchScoreModels.get(position);

        holder.tvHomeTN.setText(m.gethTeamName());
        holder.tvAwayTN.setText(m.getaTeamName());
        holder.tvQuarters.setText(m.getQuarters());
        holder.tvScore.setText(m.getScore());

        // Itt adjuk hozzá a click-et:
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(m);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchScoreModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeTN, tvAwayTN, tvQuarters, tvScore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeTN   = itemView.findViewById(R.id.homeTeamName);
            tvAwayTN   = itemView.findViewById(R.id.awayTeamName);
            tvQuarters = itemView.findViewById(R.id.quarterScores);
            tvScore    = itemView.findViewById(R.id.matchScore);
        }
    }
}
