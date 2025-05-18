package com.example.splashscore;

public class Scorer {
    private String name;
    private int goals;
    private String pid;
    private String team;

    public Scorer(String name, int goals, String pid, String team) {
        this.name = name;
        this.goals = goals;
        this.pid = pid;
        this.team = team;
    }
    public String getName() { return name; }
    public int getGoals()   { return goals; }
    public String getPid()  { return pid; }
    public String getTeam() { return team; }
}
