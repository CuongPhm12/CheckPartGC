package com.example.checkpartgc.api;

import com.example.checkpartgc.model.ApiResponse;
import com.example.checkpartgc.model.MI_Master;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {


    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    //API for Usap CheckPartExist
    //172.28.10.17:5003/Usap/CheckPartExist?wh_code=BR11&part_no=YG1002090&issue_no=0091716016
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://172.28.10.17:5003/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("Usap/CheckPartExist")
    Call<ApiResponse> checkPartExist(@Query("wh_code") String wh_code,
                                     @Query("part_no") String part_no,
                                     @Query("issue_no") String issue_no);

    //API for PDA_GA_Service
    //http://172.28.10.17:5005/Service/PDA_GA_Service.asmx/PdaGetPartGC?modelId=FM1-X946-000SS01&partNo=WB8-5234-000
    ApiService pdaService = new Retrofit.Builder()
            .baseUrl("http://172.28.10.17:5005/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("Service/PDA_GA_Service.asmx/PdaGetPartGC")
    Call<List<MI_Master>> pdaGetPartGC(@Query("modelId") String modelId,
                                       @Query("partNo") String partNo);

}