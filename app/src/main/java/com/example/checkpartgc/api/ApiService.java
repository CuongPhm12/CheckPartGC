package com.example.checkpartgc.api;

import com.example.checkpartgc.model.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    //172.28.10.17:5003/Usap/CheckPartExist?wh_code=BR11&part_no=YG1002090&issue_no=0091716016

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://172.28.10.17:5003/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("Usap/CheckPartExist")
    Call<ApiResponse> checkPartExist(@Query("wh_code") String wh_code,
                                     @Query("part_no") String part_no,
                                     @Query("issue_no") String issue_no);
}
