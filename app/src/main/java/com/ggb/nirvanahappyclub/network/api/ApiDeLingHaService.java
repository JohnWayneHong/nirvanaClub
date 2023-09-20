package com.ggb.nirvanahappyclub.network.api;

import com.ggb.nirvanahappyclub.network.result.HttpResult;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiDeLingHaService {
    /**
     * 获取猪只品种列表
     *
     * @return
     */
//    @GET("hydra-breed-pig/api/pig/variety/series/get-variety")
    @GET("hydra-base-data/api/v1/product/enable/next-sort-list")
    Observable<HttpResult<String>> getPigVarietyList(@QueryMap Map<String, String> map);

    /**
     * 获取猪只品系
     *
     * @return
     */
    @GET("hydra-breed-pig/api/pig/variety/series/get-series")
    Observable<HttpResult<String>> getPigSeriesList(@Query("pigVarietyId") String pigVarietyId);

    /**
     * 获取养殖基础常量数据
     */
    @GET("hydra-breed-pig/api/base-enumerate/list")
    Observable<HttpResult<String>> getCommonSelectList(@QueryMap Map<String, String> map);

    /**
     * 往来客户列表(供应商,处理方)
     *
     * @return
     */
    @GET("hydra-base-data/api/v1/supper/select/list")
    Observable<HttpResult<String>> getSupplierList(@QueryMap Map<String, String> map);

    /**
     * 肉猪进场上传
     */
    @POST("hydra-breed-pig/pda/porker-in-factory/add")
    Observable<HttpResult<String>> postPorkerIn(@Body Map<String, Object> map);

    /**
     * 猪猪离场
     */
    @POST("hydra-breed-pig/pda/leave-factory/add")
    Observable<HttpResult<String>> postPigLeave(@Body Map<String, Object> map);


    /**
     * 肉猪进场上传
     */
    @POST("hydra-breed-pig/pda/innocent/add")
    Observable<HttpResult<String>> postWuHaiHua(@Body Map<String, Object> map);


    @Multipart
    @POST("hydra-open-thirdpart-service/hydra-open-third-party/api/v1/file/open/upload")
    Observable<HttpResult<String>> uploadFiles(
            @Part List<MultipartBody.Part> partList,
            @Query("name") String name);


    /**
     * 屠宰，获取进场列表
     */
    @GET("hydra-butcher/api/in-factory/list")
    Observable<HttpResult<String>> slaughterFactoryInList(@QueryMap Map<String, String> map);


    /**
     * 屠宰-无害化-添加无害化信息
     */
    @POST("hydra-butcher/pda/innocent/add")
    Observable<HttpResult<String>> postSlaughterWuHaiHua(@Body Map<String, Object> map);

    /**
     * 屠宰，屠宰管理，获取屠宰批次
     */
    @GET("hydra-base-data/api/v1/product-batch/list")
    Observable<HttpResult<String>> slaughterFactoryLeaveList(@QueryMap Map<String, String> map);

    /**
     * 屠宰，屠宰管理，新增屠宰管理
     */
    @POST("hydra-butcher/pda/butcher/add")
    Observable<HttpResult<String>> postSlaughterPigLeave(@Body Map<String, Object> map);

    /**
     * 养殖进场列表
     */
    @GET("hydra-breed-pig/pda/breed/in/list")
    Observable<HttpResult<String>> getBreedInList(@QueryMap Map<String, Object> map);

    @GET("hydra-breed-pig/pda/breed/in/ne/list")
    Observable<HttpResult<String>> getBreedInBatchList(@QueryMap Map<String, Object> map);

    /**
     * 养殖进场
     */
    @POST("hydra-breed-pig/pda/breed/in/add")
    Observable<HttpResult<String>> postBreedIn(@Body Map<String, Object> map);


    /**
     * 养殖进场 耳号关联表记录列表
     */
    @GET("hydra-breed-pig/pda/identification/association/list")
    Observable<HttpResult<String>> getEarCodeByBatchList(@QueryMap Map<String, Object> map);

    /**
     * 养殖进场 添加耳号关联记录
     */
    @POST("hydra-breed-pig/pda/identification/association/add/in")
    Observable<HttpResult<String>> postAddEarCodeAssociation(@Body Map<String, Object> map);

    /**
     * 养殖进场 删除耳号关联记录
     */
    @POST("hydra-breed-pig/pda/identification/association/remove/in")
    Observable<HttpResult<String>> postRemoveEarCodeAssociation(@Body Map<String, Object> map);

    /**
     * 删除养殖进场记录
     */
    @DELETE("hydra-breed-pig/pda/breed/in/delete")
    Observable<HttpResult<String>> deleteBreedIn(@Query("breedInRecId") String breedInRecId);

    /**
     * 入栏信息记录列表
     */
    @GET("hydra-breed-pig/pda/in/fence/list")
    Observable<HttpResult<String>> getEnterFenceList(@QueryMap Map<String, Object> map);


    /**
     * 根据栏舍获取入场批次号及在栏数量记录列表
     */
    @GET("hydra-breed-pig/pda/breed/in/count/pending/list")
    Observable<HttpResult<String>> getBreedInBatchByFenceList(@QueryMap Map<String, Object> map);

    /**
     * 删除入栏记录
     */
    @DELETE("hydra-breed-pig/pda/in/fence/delete")
    Observable<HttpResult<String>> deleteEnterFence(@Query("inFenceId") String inFenceId);

    /**
     * 添加入栏信息
     */
    @POST("hydra-breed-pig/pda/in/fence/add")
    Observable<HttpResult<String>> postEnterFence(@Body Map<String, Object> map);

    /**
     * 批次代入栏信息
     */
    @GET("hydra-breed-pig/pda/breed/in/ne/pending/list")
    Observable<HttpResult<String>> getEnterFenceBatchList(@QueryMap Map<String, Object> map);


    /**
     * 养殖离场列表
     */
    @GET("hydra-breed-pig/pda/breed/out/rec/list")
    Observable<HttpResult<String>> getBreedOutList(@QueryMap Map<String, Object> map);

    @GET("hydra-breed-pig/pda/breed/out/batch/ne/list")
    Observable<HttpResult<String>> getBreedOutBatchList(@QueryMap Map<String, Object> map);

    /**
     * 养殖离场
     */
    @POST("hydra-breed-pig/pda/breed/out/rec/add")
    Observable<HttpResult<String>> postBreedOut(@Body Map<String, Object> map);


    /**
     * 养殖离场 添加耳号关联记录
     */
    @POST("hydra-breed-pig/pda/identification/association/add/out")
    Observable<HttpResult<String>> postOutAddEarCodeAssociation(@Body Map<String, Object> map);

    /**
     * 养殖离场 删除耳号关联记录
     */
    @POST("hydra-breed-pig/pda/identification/association/remove/out")
    Observable<HttpResult<String>> postOutRemoveEarCodeAssociation(@Body Map<String, Object> map);

    /**
     * 删除养殖离场记录
     */
    @DELETE("hydra-breed-pig/pda/breed/out/rec/delete")
    Observable<HttpResult<String>> deleteBreedOut(@Query("breedOutRecId") String breedInRecId);

    /**
     * 耳号重置 重置耳号记录
     */
    @POST("hydra-breed-pig/pda/identification/association/reset")
    Observable<HttpResult<String>> postResetEarCodeAssociation(@Body Map<String, Object> map);


    /**
     * 屠宰进场列表
     */
    @GET("hydra-butcher/pda/in-factory/dlh/list")
    Observable<HttpResult<String>> getSlaughterInList(@QueryMap Map<String, Object> map);

    @GET("hydra-butcher/api/incoming/stock/dlh/enable-stock")
    Observable<HttpResult<String>> getSlaughterInBatchList(@QueryMap Map<String, Object> map);

    /**
     * 屠宰进场
     */
    @POST("hydra-butcher/pda/in-factory/dlh/add")
    Observable<HttpResult<String>> postSlaughterIn(@Body Map<String, Object> map);

    /**
     * 删除屠宰进场记录
     */
    @POST("hydra-butcher/pda/in-factory/dlh/del")
    Observable<HttpResult<String>> deleteSlaughterIn(@Body Map<String, Object> map);

    /**
     * 屠宰进场记录 上传合格证图片
     */
    @POST("hydra-butcher/pda/in-factory/dlh/update-certificate-image")
    Observable<HttpResult<String>> getUpdateImageSlaughterIn(@Body Map<String, Object> map);


    /**
     *  称重记录列表
     */
    @GET("hydra-butcher/pda/weigh/dlh/list")
    Observable<HttpResult<String>> getSlaughterWeighingList(@QueryMap Map<String, Object> map);


    /**
     * 删除称重记录
     */
    @POST("hydra-butcher/pda/weigh/dlh/del")
    Observable<HttpResult<String>> deleteSlaughterWeighing(@Body Map<String, Object> map);

    /**
     * 添加称重
     */
    @POST("hydra-butcher/pda/weigh/dlh/add")
    Observable<HttpResult<String>> postSlaughterWeighing(@Body Map<String, Object> map);

    /**
     * 加工进场列表
     */
//    @GET("hydra-butcher/pda/processing-approach/record/list")
    @GET("hydra-butcher/pda/processing-approach/list")
    Observable<HttpResult<String>> getProcessingApproachInList(@QueryMap Map<String, Object> map);

    /**
     * 加工进场
     */
    @POST("hydra-butcher/pda/processing-approach/add")
    Observable<HttpResult<String>> postProcessingApproachIn(@Body Map<String, Object> map);

    /**
     * 删除加工进场记录
     */
    @DELETE("hydra-butcher/pda/processing-approach/delete")
    Observable<HttpResult<String>> deleteProcessingApproachIn(@Query("processingId") String processingId);


    /**
     * 加工进场列表
     */
    @GET("hydra-user/api/v1/employee/enable/list")
    Observable<HttpResult<String>> getEmployeeList(@QueryMap Map<String, Object> map);

    /**
     * 饲喂管理
     */
    @GET("hydra-breed-pig/pda/dlh/feed/list")
    Observable<HttpResult<String>> getFeedingList(@QueryMap Map<String, Object> map);

    /**
     * 删除饲喂管理记录
     */
    @DELETE("hydra-breed-pig/pda/dlh/feed/del")
    Observable<HttpResult<String>> deleteFeeding(@Query("feedId") String processingRecordId);


    /**
     * 饲喂管理新增记录
     */
    @POST("hydra-breed-pig/pda/dlh/feed/add")
    Observable<HttpResult<String>> postFeeding(@Body Map<String, Object> map);

    /**
     * 获取栏列表
     */
    @GET("hydra-culture-baseinfo/api/v1/culture/mass/type/enable/list")
    Observable<HttpResult<String>> getPigstyList(@QueryMap Map<String, Object> map);

    /**
     * 获取栏列表 养殖离场
     */
    @GET("hydra-breed-pig/pda/in/fence/sum/list")
    Observable<HttpResult<String>> getPigstyOutList(@QueryMap Map<String, Object> map);

    /**
     * 称重管理列表
     */
    @GET("hydra-breed-pig/pda/dlh/weight/list")
    Observable<HttpResult<String>> getWeightList(@QueryMap Map<String, Object> map);

    /**
     * 删除称重管理记录
     */
    @DELETE("hydra-breed-pig/pda/dlh/weight/del")
    Observable<HttpResult<String>> deleteWeight(@Query("weightId") String processingRecordId);

    /**
     * 称重管理新增记录
     */
    @POST("hydra-breed-pig/pda/dlh/weight/add")
    Observable<HttpResult<String>> postWeight(@Body Map<String, Object> map);

    /**
     * 能耗管理列表
     */
    @GET("hydra-breed-pig/pda/dlh/energy/list")
    Observable<HttpResult<String>> getEnergyList(@QueryMap Map<String, Object> map);

    /**
     * 删除能耗管理记录
     */
    @DELETE("hydra-breed-pig/pda/dlh/energy/del")
    Observable<HttpResult<String>> deleteEnergy(@Query("energyId") String processingRecordId);


    /**
     * 能耗管理新增记录
     */
    @POST("hydra-breed-pig/pda/dlh/energy/add")
    Observable<HttpResult<String>> postEnergy(@Body Map<String, Object> map);

    /**
     * 转栏管理列表
     */
    @GET("hydra-breed-pig/pda/dlh/rotation-rail/list")
    Observable<HttpResult<String>> getExchangePigstyList(@QueryMap Map<String, Object> map);

    /**
     * 能耗管理新增记录
     */
    @POST("hydra-breed-pig/pda/dlh/rotation-rail/add")
    Observable<HttpResult<String>> postExchangePigsty(@Body Map<String, Object> map);

    /**
     * 保健管理记录列表（分页）
     */
    @GET("hydra-breed-pig/pda/dlh/health/care/list")
    Observable<HttpResult<String>> getHealthCareList(@QueryMap Map<String, Object> map);

    /**
     * 删除保健管理
     */
    @GET("hydra-breed-pig/pda/dlh/health/care/delete")
    Observable<HttpResult<String>> deleteHealthCare(@Query("healthCareId") String healthCareId);

    /**
     * 添加保健管理
     */
    @POST("hydra-breed-pig/pda/dlh/health/care/add")
    Observable<HttpResult<String>> postHealthCare(@Body Map<String, Object> map);

    /**
     * 免疫管理记录列表（分页）
     */
    @GET("hydra-breed-pig/pda/dlh/immuno/list")
    Observable<HttpResult<String>> getImmunityList(@QueryMap Map<String, Object> map);

    /**
     * 删除免疫管理
     */
    @GET("hydra-breed-pig/pda/dlh/immuno/delete")
    Observable<HttpResult<String>> deleteImmunity(@Query("immunoId") String immunoId);

    /**
     * 添加免疫管理
     */
    @POST("hydra-breed-pig/pda/dlh/immuno/add")
    Observable<HttpResult<String>> postImmunity(@Body Map<String, Object> map);

    /**
     * 诊疗管理记录列表（分页）
     */
    @GET("hydra-breed-pig/pda/dlh/treat/list")
    Observable<HttpResult<String>> getTreatmentList(@QueryMap Map<String, Object> map);

    /**
     * 删除诊疗管理
     */
    @DELETE("hydra-breed-pig/pda/dlh/treat/delete")
    Observable<HttpResult<String>> deleteTreatment(@Query("treatId") String treatId);

    /**
     * 添加诊疗管理
     */
    @POST("hydra-breed-pig/pda/dlh/treat/add")
    Observable<HttpResult<String>> postTreatment(@Body Map<String, Object> map);

    /**
     * 消毒管理记录列表（分页）
     */
    @GET("hydra-breed-pig/pda/dlh/disinfect/list")
    Observable<HttpResult<String>> getDisinfectList(@QueryMap Map<String, Object> map);

    /**
     * 删除消毒管理
     */
    @GET("hydra-breed-pig/pda/dlh/disinfect/delete")
    Observable<HttpResult<String>> deleteDisinfect(@Query("disinfectId") String disinfectId);

    /**
     * 添加消毒管理
     */
    @POST("hydra-breed-pig/pda/dlh/disinfect/add")
    Observable<HttpResult<String>> postDisinfect(@Body Map<String, Object> map);

    /**
     * 无害化管理记录列表（分页）
     */
    @GET("hydra-breed-pig/pda/dlh/innocent/list")
    Observable<HttpResult<String>> getHarmlessList(@QueryMap Map<String, Object> map);

    /**
     * 删除无害化
     */
    @GET("hydra-breed-pig/pda/dlh/innocent/delete")
    Observable<HttpResult<String>> deleteHarmless(@Query("innocentId") String innocentId);

    /**
     * 添加无害化
     */
    @POST("hydra-breed-pig/pda/dlh/innocent/add")
    Observable<HttpResult<String>> postHarmless(@Body Map<String, Object> map);

    /**
     * 标准养殖方案明细任务记录列表（分页）
     */
    @GET("hydra-breed-pig/pda/standard/breed/plan/detail/task/list")
    Observable<HttpResult<String>> getBreedTaskList(@QueryMap Map<String, Object> map);

    /**
     * 养殖任务 执行任务
     */
    @POST("hydra-breed-pig/pda/standard/breed/plan/detail/task/do")
    Observable<HttpResult<String>> doBreedTask(@Body Map<String, Object> map);
}
