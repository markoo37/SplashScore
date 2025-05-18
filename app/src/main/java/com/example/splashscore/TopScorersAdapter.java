package com.example.splashscore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TopScorersAdapter extends RecyclerView.Adapter<TopScorersAdapter.Holder> {
    private final List<PlayerStat> items;
    public TopScorersAdapter(List<PlayerStat> items) { this.items = items; }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_scorer, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        PlayerStat p = items.get(pos);
        h.rankText.setText(String.valueOf(pos + 1));
        h.nameText.setText(p.getName());
        h.teamText.setText(p.getTeamName());
        h.ageGoalsText.setText(p.getAge() + " év • " + p.getGoals() + " gól");
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView rankText, nameText, teamText, ageGoalsText;
        Holder(@NonNull View v) {
            super(v);
            rankText     = v.findViewById(R.id.rankText);
            nameText     = v.findViewById(R.id.playerNameText);
            teamText     = v.findViewById(R.id.teamNameText);
            ageGoalsText = v.findViewById(R.id.ageGoalsText);
        }
    }
}

