package com.ggb.nirvanahappyclub.network.api;

import com.ggb.nirvanahappyclub.bean.VersionBean;
import com.ggb.nirvanahappyclub.network.result.HttpResult;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @GET("hydra-mobile-terminal/api/v1/common/version/last")
    Observable<HttpResult<VersionBean>> getVersion(@Query("type") int type);

    @Streaming
    @GET
    @Headers({"Accept-Encoding:identity", "BigFile:ture"})
    Observable<Response<ResponseBody>> download(@Header("token") String token, @Url String fileUrl);

}
