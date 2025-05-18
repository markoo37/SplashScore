package com.example.splashscore;

public class TeamStat {
    private String teamId;      // pl. "team1"
    private String name;        // pl. "Szegedi VE"
    private int wins;
    private int draws;
    private int losses;
    private int points;

    public TeamStat(String teamId, String name, int wins, int draws, int losses, int points) {
        this.teamId   = teamId;
        this.name     = name;
        this.wins     = wins;
        this.draws    = draws;
        this.losses   = losses;
        this.points   = points;
    }

    // getterek
    public String getTeamId()   { return teamId; }
    public String getName()     { return name;     }
    public int    getWins()     { return wins;     }
    public int    getDraws()    { return draws;    }
    public int    getLosses()   { return losses;   }
    public int    getPoints()   { return points;   }
}
