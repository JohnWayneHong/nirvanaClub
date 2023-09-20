package com.jgw.delingha.network.api;

import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.bean.PackageStatisticsHeaderBean;
import com.jgw.delingha.bean.ProducePackageCodeInfoBean;
import com.jgw.delingha.network.result.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiPackageService {
    /**
     * 今天昨天的包装统计数据
     */
    @GET("hydra-production/api/v1/package/statistics/two-day")
    Observable<HttpResult<PackageStatisticsHeaderBean>> getPackageTwoDayData();

    /**
     * 产品维度包装统计
     */
    @GET("hydra-production/api/v1/package/statistics/product")
    Observable<HttpResult<List<PackageStatisticsBean>>> getPackageStatisticsByProduct(@QueryMap Map<String, Object> map);

    /**
     * 时间维度包装统计
     */
    @GET("hydra-production/api/v1/package/statistics/time")
    Observable<HttpResult<List<PackageStatisticsBean>>> getPackageStatisticsByTime(@QueryMap Map<String, Object> map);


    /**
     * 生产补码入箱上传
     */
    @POST("hydra-production/api/v1/replenish/package/replenish")
    Observable<HttpResult<String>> postProduceSupplementToBox(@Body ProducePackageCodeInfoBean bean);

}
