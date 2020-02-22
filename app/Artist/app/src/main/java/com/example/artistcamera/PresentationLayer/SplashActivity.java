package com.example.artistcamera.PresentationLayer;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.artistcamera.R;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_splash);

        //processThread是初始化网络操作、本地数据库读取、等等的操作
        Thread processThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        processThread.start();

        //使用handler而不使用new thread节省资源和调用堆栈
        //如果不使用handler直接调用thread.sleep则会出现白屏，并不会显示本activity的画面，
        //因为splashActivity的画面是在onCreate方法结束之后渲染完毕的
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                firstRun();
            }
        }, 1000);
    }

    private void firstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstRun",0);
        Boolean first_run = sharedPreferences.getBoolean("First",true);
        if (first_run){
            sharedPreferences.edit().putBoolean("First",false).commit();
            startActivity(new Intent(SplashActivity.this,GuideActivity.class));
            finish();// finish 并不会结束该firstRun函数
        }else {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }
    }

}
