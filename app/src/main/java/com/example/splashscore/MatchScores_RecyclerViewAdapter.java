package com.example.splashscore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchScores_RecyclerViewAdapter extends RecyclerView.Adapter<MatchScores_RecyclerViewAdapter.MyViewHolder> {
    ArrayList<MatchScoreModel> matchScoreModels;

    public MatchScores_RecyclerViewAdapter(ArrayList<MatchScoreModel> matchScoreModels){
        this.matchScoreModels = matchScoreModels;
    }

    @NonNull
    @Override
    public MatchScores_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.matchscoreitem, parent, false);

        return new MatchScores_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchScores_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tvHomeTN.setText(matchScoreModels.get(position).gethTeamName());
        holder.tvAwayTN.setText(matchScoreModels.get(position).getaTeamName());
        holder.tvQuarters.setText(matchScoreModels.get(position).getQuarters());
        holder.tvScore.setText(matchScoreModels.get(position).getScore());
    }

    @Override
    public int getItemCount() {
        return matchScoreModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvHomeTN, tvAwayTN, tvQuarters, tvScore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHomeTN = itemView.findViewById(R.id.homeTeamName);
            tvAwayTN = itemView.findViewById(R.id.awayTeamName);
            tvQuarters = itemView.findViewById(R.id.quarterScores);
            tvScore = itemView.findViewById(R.id.matchScore);
        }
    }
}
