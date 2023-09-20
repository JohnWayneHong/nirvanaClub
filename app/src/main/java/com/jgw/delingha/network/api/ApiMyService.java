package com.jgw.delingha.network.api;

import com.jgw.delingha.network.result.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiMyService {
    @Multipart
    @POST("file/upload")
    @Headers("BigFile:ture")
    Observable<HttpResult<String>> uploadFiles(@Part List<MultipartBody.Part> partList
            , @Query("code") String code
            , @Query("data") String phone);
}
