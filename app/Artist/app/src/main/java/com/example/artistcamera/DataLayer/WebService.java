package com.example.artistcamera.DataLayer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {
    @GET("movie/top250")
    Call<BeanTest> getTop250(@Query("start") int start, @Query("count")  int count);

    @GET("/users/{user}")
    Call<BeanTest> repo(@Path("user") String user);
}
