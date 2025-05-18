package com.example.splashscore;

public class HirekModel {
    private final String id;
    private final String title;
    private final String summary;
    private final String date;
    private final String uploaderEmail;   // ← új mező

    public HirekModel(String id, String title, String summary, String date, String uploaderEmail) {
        this.id            = id;
        this.title         = title;
        this.summary       = summary;
        this.date          = date;
        this.uploaderEmail = uploaderEmail;
    }

    // getterek
    public String getId()             { return id; }
    public String getTitle()          { return title; }
    public String getSummary()        { return summary; }
    public String getDate()           { return date; }
    public String getUploaderEmail()  { return uploaderEmail; }  // ← getter
}

