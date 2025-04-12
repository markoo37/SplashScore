package com.example.splashscore;

public class MatchScoreModel {
    String hTeamName;
    String aTeamName;
    String quarters;
    String score;

    public MatchScoreModel(String hTeamName, String aTeamName, String quarters, String score) {
        this.hTeamName = hTeamName;
        this.aTeamName = aTeamName;
        this.quarters = quarters;
        this.score = score;
    }

    public String gethTeamName() {
        return hTeamName;
    }

    public String getaTeamName() {
        return aTeamName;
    }

    public String getQuarters() {
        return quarters;
    }

    public String getScore() {
        return score;
    }
}
