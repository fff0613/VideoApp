package com.example.videoapp;

import java.util.List;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    //https://beiyou.bytedance.com/api/invoke/video/invoke/video
   @GET("api/invoke/video/invoke/video")
    Call<List<VideoResponse>> getArticles();

}
