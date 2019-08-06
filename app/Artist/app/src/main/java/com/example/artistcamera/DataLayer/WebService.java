package com.example.artistcamera.DataLayer;

import com.example.artistcamera.DataLayer.Bean.EvaluateJsonBean;
import com.example.artistcamera.DataLayer.Bean.PoemBean;
import com.example.artistcamera.DataLayer.Bean.StyleJsonBean;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface WebService {
    /**
     * ai写诗
     */
    @Multipart
    @POST("upload")
    Call<PoemBean> poemOfImage(@PartMap Map<String, RequestBody> requestBodyMap);

    /**
     * 风格迁移
     * TODO: 风格迁移url
     */
    @Multipart
    @POST("style_transfer")
    Call<StyleJsonBean> styleMigration(@PartMap Map<String, RequestBody> requestBodyMap);

    /*
    * 评分
    * TODO: 评分url
     */
    @Multipart
    @POST("up_photo")
    Call<EvaluateJsonBean> evaluate(@PartMap Map<String, RequestBody> requestBodyMap);
}
