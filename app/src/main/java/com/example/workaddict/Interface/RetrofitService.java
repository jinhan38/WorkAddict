package com.example.workaddict.Interface;

import com.example.workaddict.DataClass.CategoryResult;
import com.example.workaddict.DataClass.MapPressResult;
import com.example.workaddict.Utility.Util;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RetrofitService {

    //현재 위치의 x,y 같이 radius랑 보내야한다.
    @GET("/v2/local/search/keyword.json")
    Call<CategoryResult> getAddress(
            @Query("query") String query,
            @Query("x") double x,
            @Query("y") double y,
//            @Query("radius") Integer radius,
            @Query("sort") String sort
    );

    @GET("/v2/local/search/keyword.json")
    Call<CategoryResult> getAddress(
            @Query("query") String query
    );

    @GET("/v2/local/geo/coord2address.json")
    Call<MapPressResult> getAddressByLatLon(
            //클릭 위치의 가까운데 기준으로
            //반경도 정하기
//            @Header("Authorization") String header,
            @Query("x") double x,
            @Query("y") double y
    );



    @GET("/v2/local/search/address.json")
    Call<CategoryResult> getAddr(
            @Header("Authorization") String header,
            @Query("query") String query
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Util.addressURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitService service = retrofit.create(RetrofitService.class);
}
