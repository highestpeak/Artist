package com.example.artistcamera.DataLayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import com.example.artistcamera.DataLayer.Bean.EvaluateJsonBean;
import com.example.artistcamera.DataLayer.Bean.PoemBean;
import com.example.artistcamera.PresentationLayer.ViewLib.DirectSuggest;
import com.example.artistcamera.Util.UriPhotoHelp;
import com.example.artistcamera.Util.WebHelp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.artistcamera.Util.WebHelp.generateRequestBody;
import static com.example.artistcamera.Util.WebHelp.imageToBase64;

public class ScoreGetHelp {
    private static final String BASE_URL ="http://120.25.227.237:80/";
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private WebService webService = retrofit.create(WebService.class);

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
    public Map<String,Object> scoreReturn(Bitmap frameData){
        return scoreReturn(frameData,0);
    }
    public Map<String,Object> scoreReturn(Bitmap frameData,final int pattern){
        Map<String,Object> rs=new HashMap<>();

        //TODO: 进行数据传输
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),"this_is_username");
        String imageBody = WebHelp.bitmapToBase64(frameData);
        Map<String, String> requestDataMap=new HashMap<>();
        requestDataMap.put("photo",imageBody);
        if(pattern==0){//上传的是预览的连续的数据
            requestDataMap.put("pattern","Multiple");
        }else if (pattern==1){//上传的是一张图片
            //pattern=1 //上传的是一张图片
            requestDataMap.put("pattern","Single");
        }

        Call<EvaluateJsonBean> call= webService.evaluate(generateRequestBody(requestDataMap));
        EvaluateJsonBean bean=null;
        try {
            bean = call.execute().body();
        } catch (IOException e) {
            Log.d("PoemGetHelp ","execute error");
        }
        if (bean==null){
            rs.put("score","classify: loading"+"\n"+
                    "line score: loading"+"\n"+
                    "score: loading"+"\n"
            );
            rs.put("suggest_direct", DirectSuggest.SUGGEST_DIRECT.CENTER);
        }else {
            rs.put("score","classify: "+bean.getClassify()+"\n"+
                    "line score: "+bean.getLine_score()+"\n"+
                    "score: "+bean.getScore()+"\n"
            );
            rs.put("suggest_direct", DirectSuggest.SUGGEST_DIRECT.values()[Integer.parseInt(bean.getBest())]);
        }
        //TODO end

        return rs;
    }
}
