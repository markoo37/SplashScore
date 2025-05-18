package com.example.splashscore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StandingsAdapter extends RecyclerView.Adapter<StandingsAdapter.Holder> {
    private final List<TeamStat> items;

    public StandingsAdapter(List<TeamStat> items) {
        this.items = items;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemstanding, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        TeamStat stat = items.get(pos);
        h.posText.setText(String.valueOf(pos + 1));
        h.teamNameText.setText(stat.getName());
        h.winsText.setText(String.valueOf(stat.getWins()));
        h.drawsText.setText(String.valueOf(stat.getDraws()));
        h.lossesText.setText(String.valueOf(stat.getLosses()));
        h.pointsText.setText(String.valueOf(stat.getPoints()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView posText, teamNameText, winsText, drawsText, lossesText, pointsText;
        Holder(@NonNull View v) {
            super(v);
            posText      = v.findViewById(R.id.posText);
            teamNameText = v.findViewById(R.id.teamNameText);
            winsText     = v.findViewById(R.id.winsText);
            drawsText    = v.findViewById(R.id.drawsText);
            lossesText   = v.findViewById(R.id.lossesText);
            pointsText   = v.findViewById(R.id.pointsText);
        }
    }
}
