package com.example.artistcamera.PresentationLayer.Presenter;

import android.graphics.Bitmap;

import com.example.artistcamera.DataLayer.ScoreGetHelp;
import com.example.artistcamera.DataLayer.ScoreCallBack;
import com.example.artistcamera.PresentationLayer.ViewLib.DirectSuggest.SUGGEST_DIRECT;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessWithThreadPool {
    private static final String TAG = "ThreadPool";
    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue;
    private ThreadPoolExecutor mThreadPool;

    public ProcessWithThreadPool() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maximumPoolSize = corePoolSize * 2;
        workQueue = new LinkedBlockingQueue<>();
        mThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TIME_UNIT, workQueue);
    }

    public synchronized void post(final Bitmap frameData) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                processFrame(frameData);
            }
        });
    }

    private ScoreGetHelp scoreGetHelp=new ScoreGetHelp();
    private void processFrame(Bitmap frameData) {
        //上传服务器
//        Log.i(TAG, "test");
        Map<String,Object> rs=  scoreGetHelp.scoreReturn(frameData);
        String score=(String) rs.get("score");
        SUGGEST_DIRECT suggest_direct= (SUGGEST_DIRECT)rs.get("suggest_direct");
        scoreCallBack.getScore(score,suggest_direct);
    }

    private ScoreCallBack scoreCallBack;

    public void setScoreCallBack(ScoreCallBack scoreCallBack) {
        this.scoreCallBack = scoreCallBack;
    }
}
