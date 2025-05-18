package com.example.splashscore;

public class PlayerStat {
    private final String pid;
    private final String name;
    private final String teamName;
    private final int goals;
    private final int age;         // új mező
    private final int weight;

    public PlayerStat(String pid, String name, String teamName, int goals, int age, int weight) {
        this.pid   = pid;
        this.name  = name;
        this.teamName = teamName;
        this.goals = goals;
        this.age   = age;
        this.weight = weight;
    }

    public String getPid()   { return pid;   }
    public String getName()  { return name;  }
    public String getTeamName()  { return teamName;  }
    public int    getGoals() { return goals; }
    public int    getAge()   { return age;   }  // getter
    public int getWeight() { return weight; }
}
