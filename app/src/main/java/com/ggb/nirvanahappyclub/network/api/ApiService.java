package com.ggb.nirvanahappyclub.network.api;

import com.ggb.nirvanahappyclub.bean.ArticleContentBean;
import com.ggb.nirvanahappyclub.bean.IndexArticleInfoBean;
import com.ggb.nirvanahappyclub.bean.IndexTagBean;
import com.ggb.nirvanahappyclub.bean.VersionBean;
import com.ggb.nirvanahappyclub.network.result.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @GET("hydra-mobile-terminal/api/v1/common/version/last")
    Observable<HttpResult<VersionBean>> getVersion(@Query("type") int type);

    @Streaming
    @GET
    @Headers({"Accept-Encoding:identity", "BigFile:ture"})
    Observable<Response<ResponseBody>> download(@Header("token") String token, @Url String fileUrl);


    //获取用户标签,已改为获取前20热度标签列表
    @GET("v2/api/tag/recommand/list")
    Observable<HttpResult<List<IndexTagBean>>> searchIndexTag();

    //分页查询某个标签的文章
    @GET("v2/api/tag/blog/list")
    Observable<HttpResult<List<IndexArticleInfoBean>>> searchArticleByTag(@QueryMap Map<String, Object> map);

    //获取文章的内容
    @GET("/v2/api/blog/details/{id}")
    Observable<HttpResult<ArticleContentBean>> getArticleContentById(@Path("id") String id);

}
