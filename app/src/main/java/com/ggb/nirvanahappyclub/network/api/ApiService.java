package com.ggb.nirvanahappyclub.network.api;

import com.ggb.nirvanahappyclub.bean.ArticleContentBean;
import com.ggb.nirvanahappyclub.bean.DevelopJokesListBean;
import com.ggb.nirvanahappyclub.bean.IndexArticleInfoBean;
import com.ggb.nirvanahappyclub.bean.IndexTagBean;
import com.ggb.nirvanahappyclub.bean.SimpleUserInfo;
import com.ggb.nirvanahappyclub.bean.VersionBean;
import com.ggb.nirvanahappyclub.network.result.HttpResult;
import com.ggb.nirvanahappyclub.network.result.JokerAndroidHttpResult;
import com.ggb.nirvanahappyclub.network.result.WanAndroidHttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
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

    /**
     *牛蛙吶原生 ----自帶接口地址
     */
    @GET("v2/api/android/apk/latest/")
    Observable<HttpResult<VersionBean>> getVersion();

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

    //用户登录
    @POST("/v2/api/user/login")
    Observable<HttpResult<String>> login(@Body Map<String, Object> map);

    //用户登出
    @POST("/v2/api/user/logout")
    Observable<HttpResult<String>> loginOut(@Body Map<String, Object> map);

    //用户基础信息
    @GET("/v2/api/user/myinfo")
    Observable<HttpResult<SimpleUserInfo>> getUserInfo();




    /**
     * 玩安卓----接口
     */

    //获取主页的推荐关注数据
    @GET("article/list/{pager}/json")
    Observable<WanAndroidHttpResult<String>> getCommunityAndroid(@Path("pager") int pager,@Query("page_size") int page_size);

    @GET("user_article/list/{pager}/json")
    Observable<WanAndroidHttpResult<String>> getCommunitySquare(@Path("pager") int pager,@Query("page_size") int page_size);


    /**
     * 段子乐----接口
     */
    //获取随机纯文字
    @POST("home/text")
    Observable<JokerAndroidHttpResult<List<DevelopJokesListBean>>> getCommunityTextJokerAndroid();

    //获取随机图文
    @POST("home/pic")
    Observable<JokerAndroidHttpResult<List<DevelopJokesListBean>>> getCommunityPictureJokerAndroid();
}
