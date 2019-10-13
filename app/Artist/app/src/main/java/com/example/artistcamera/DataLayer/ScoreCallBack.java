package com.example.artistcamera.DataLayer;

import com.example.artistcamera.PresentationLayer.ViewLib.DirectSuggest.SUGGEST_DIRECT;

public interface ScoreCallBack {
    /*
     * 获取分数
     */
    void getScore(String scoreText,SUGGEST_DIRECT suggest_direct);
}
