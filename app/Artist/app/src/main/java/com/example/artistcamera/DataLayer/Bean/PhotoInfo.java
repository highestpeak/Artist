package com.example.artistcamera.DataLayer.Bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class PhotoInfo extends LitePalSupport {
    @Column(nullable = false)
    private String uri;

    private String score;

    private String poem;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPoem() {
        return poem;
    }

    public void setPoem(String poem) {
        this.poem = poem;
    }
}
