package com.example.artistcamera.DataLayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.artistcamera.DataLayer.Bean.PoemBean;
import com.example.artistcamera.Util.UriPhotoHelp;
import com.example.artistcamera.Util.WebHelp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.artistcamera.Util.WebHelp.generateRequestBody;
import static com.example.artistcamera.Util.WebHelp.imageToBase64;

public class PoemGetHelp {

    private static final String BASE_URL ="https://poem.msxiaobing.com/api/";
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private WebService webService = retrofit.create(WebService.class);

    public PoemGetHelp() {
    }

    public String poemReturnShortest(Context context, Bitmap bitmap){
        return poemReturnShortest(context, WebHelp.bitmapToBase64(bitmap));
    }
    public String poemReturnShortest(Context context, Uri photoUri){
        return poemReturnShortest(context,imageToBase64(UriPhotoHelp.getRealPathFromUri(context,photoUri)));
    }

    public String poemReturnShortest(Context context,String imageBodyV){
//        File file = new File(UriPhotoHelp.getRealPathFromUri(context,photoUri));//filePath 图片地址
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),"this_is_username");
        String imageBody = imageBodyV;
        String text="";
        String userid="345cf72a-d665-4aed-a5c2-99d26f86a078";
        String guid="cafd6d08-7e4e-4143-aa12-dbe4fa49bc77";
        Map<String, String> requestDataMap=new HashMap<>();
        requestDataMap.put("image",imageBody);
        requestDataMap.put("text",text);
        requestDataMap.put("userid",userid);
        requestDataMap.put("guid",guid);

//        Call<PoemBean> call= webService.poemOfImage(imageBody,text,userid,guid);
        Call<PoemBean> call= webService.poemOfImage(generateRequestBody(requestDataMap));
        PoemBean bean=null;
        try {
            bean = call.execute().body();
        } catch (IOException e) {
            Log.d("PoemGetHelp ","execute error");
        }
        if (bean==null){
            return "词穷了...";
        }
        return bean.getOpenPoems().get(0).getPoemContent();
    }



}
