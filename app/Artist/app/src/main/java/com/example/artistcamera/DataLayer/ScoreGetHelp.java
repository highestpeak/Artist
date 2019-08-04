package com.example.artistcamera.DataLayer;

import android.util.Log;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScoreGetHelp {
    private static final String BASE_URL ="https://api.github.com";
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private WebService moviceService = retrofit.create(WebService.class);

    public ScoreGetHelp() {
//        Call<BeanTest> call= moviceService.repo("highestpeak");
//        call.enqueue(new Callback<BeanTest>() {
//            @Override
//            public void onResponse(Call<BeanTest> call, Response<BeanTest> response) {
//                 Log.e("Test", response.body().getAvatar_url());
//            }
//            @Override
//            public void onFailure(Call<BeanTest> call, Throwable t) {
//                System.out.print(t.getMessage());
//            }
//        });

    }

    private Random random = new Random(1000);//指定种子数字
    public String scoreReturn(){
        return "xxx score:"+random.nextInt(100);
    }
}
