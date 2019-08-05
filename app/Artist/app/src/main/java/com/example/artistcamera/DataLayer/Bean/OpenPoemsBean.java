package com.example.artistcamera.DataLayer.Bean;

public class OpenPoemsBean {
    private String OpenPoemID;
    private String PoemContent;
    private String PoemType;
    private Double Score;
    private Boolean Optimum;
    private String TimeStamp;

    public String getOpenPoemID() {
        return OpenPoemID;
    }

    public void setOpenPoemID(String openPoemID) {
        OpenPoemID = openPoemID;
    }

    public String getPoemContent() {
        return PoemContent;
    }

    public void setPoemContent(String poemContent) {
        PoemContent = poemContent;
    }

    public String getPoemType() {
        return PoemType;
    }

    public void setPoemType(String poemType) {
        PoemType = poemType;
    }

    public Double getScore() {
        return Score;
    }

    public void setScore(Double score) {
        Score = score;
    }

    public Boolean getOptimum() {
        return Optimum;
    }

    public void setOptimum(Boolean optimum) {
        Optimum = optimum;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
