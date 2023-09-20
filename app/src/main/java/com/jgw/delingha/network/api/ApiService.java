package com.jgw.delingha.network.api;

import com.jgw.delingha.bean.BatchManagementBean;
import com.jgw.delingha.bean.BatchManagementListBean;
import com.jgw.delingha.bean.CodeRelationInfoResultBean;
import com.jgw.delingha.bean.NFCTaskBean;
import com.jgw.delingha.bean.OrgAndSysBean;
import com.jgw.delingha.bean.OrganizationBean;
import com.jgw.delingha.bean.ProductWareHouseBean;
import com.jgw.delingha.bean.RegisterBean;
import com.jgw.delingha.bean.SystemBean;
import com.jgw.delingha.bean.UserBean;
import com.jgw.delingha.bean.VersionBean;
import com.jgw.delingha.bean.WareHouseBean;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.converter_bean.AllBatchInfoBean;
import com.jgw.delingha.sql.converter_bean.LogisticsCompanyBean;
import com.jgw.delingha.sql.converter_bean.MyCustomerBean;
import com.jgw.delingha.sql.converter_bean.ProductBean;
import com.jgw.delingha.sql.entity.StorePlaceEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    /**
     * 版本
     */
    @GET("hydra-org/api/v1/org/app-version/download-version")
    Observable<HttpResult<String>> getVersionType(@Query("organizationId") String organizationId
            , @Query("appVersion") int appVersion);

    /**
     * 登录
     * isCodeToken: 0-不进行人机验证码校验;1-密码错误达到次数启用人机验证码校验 -> 默认为1
     * body参数-> tokenTimeout: token超时时间(秒), 不传默认两小时
     */
    @POST("hydra-user/api/v1/user/web/login")
    @Headers("isCodeToken:0")
    Observable<HttpResult<UserBean>> login(@Body Map<String, Object> map);

    @GET("hydra-user/api/v1/user/web/logout")
    Observable<HttpResult<String>> logout();

    /**
     * 检查设备的合法性(新)
     */
    @GET("hydra-mobile-terminal/api/v1/device/mobile/query")
    Observable<HttpResult<String>> getCheckDevice(@Query("secretKey") String secretKey);

    @GET("hydra-mobile-terminal/api/v1/mobile/device/pda/query")
    Observable<HttpResult<String>> getOldCheckDevice(@Query("secretKey") String secretKey);

    /**
     * PDA自动注册
     */
    @POST("hydra-mobile-terminal/api/v1/device/mobile/autoRegister")
    Observable<HttpResult<String>> autoRegister(@Body Map<String, Object> map);

    /**
     * pda注册
     */
    @POST("hydra-mobile-terminal/api/v1/device/mobile/register")
    Observable<HttpResult<String>> pdaRegister(@Body RegisterBean bean);

    /**
     * 企业列表
     */
    @GET("hydra-user/api/v1/org/user/id")
    Observable<HttpResult<OrganizationBean>> getOrganizationList(@Header("super-token") String token);

    /**
     * 企业列表(不需要传token)
     */
    @GET("hydra-user/api/v1/org/list/platform")
    Observable<HttpResult<OrganizationBean>> getOrganizationListWithoutToken(@QueryMap Map<String, Object> map);

    /**
     * 系统列表
     */
    @GET("hydra-user/api/v1/sys/org/auth/list")
    Observable<HttpResult<SystemBean>> getSystemList(@Header("super-token") String token, @Query("organizationId") String organizationId);

    /**
     * 企业系统登录
     */
    @POST("hydra-user/api/v1/user/set/org-sys")
    @FormUrlEncoded
    @Headers("isAdmin:false")
    Observable<HttpResult<OrgAndSysBean>> submitOrgSys(@Header("super-token") String token,@FieldMap Map<String, Object> map);

    /**
     * 产品列表 带包装信息
     */
//    @GET("hydra-base-data/api/v1/product/enable/list")
    @GET("hydra-base-data/api/v1/product/nf-list")
    Observable<HttpResult<ProductBean>> getProductAndPackageInfoList(
            @Query("organizationId") String organizationId,
            @Query("status") int status,
            @Query("current") int currentPage,
            @Query("pageSize") int pageSize
    );

    /**
     * 产品仓储信息
     */
    @GET("hydra-base-data/api/v1/product-ware-house/page")
    Observable<HttpResult<ProductWareHouseBean>> getProductPackageInfoList(
            @Query("orgId") String organizationId,
            @Query("current") int currentPage,
            @Query("pageSize") int pageSize
    );

    /**
     * 获取菜单
     */
    @POST("hydra-user/api/v1/menu/pda/menu-final")
    Observable<HttpResult<String>> getAllMenu();


    /**
     * 仓库列表
     */
    @GET("hydra-base-data/api/v1/ware-house/enable/list")
    Observable<HttpResult<WareHouseBean>> getWareHouseList(
            @Query("search") String searchStr,
            @Query("current") int currentPage,
            @Query("pageSize") int pageSize
    );

    /**
     * 全部仓位列表
     */
    @GET("hydra-base-data/api/v1/pda/base/ware/house/all-store-list")
    Observable<HttpResult<List<StorePlaceEntity>>> getAllStorePlaceList(
            @Query("current") int currentPage,
            @Query("pageSize") int pageSize
    );

    /**
     * 客户列表
     */
    @GET("hydra-base-data/api/v1/pda/base/customer/enable/list")
    Observable<HttpResult<String>> getCustomerList(@QueryMap Map<String, Object> map);

    /**
     * 获取当前客户信息
     */
    @GET("hydra-base-data/api/v1/customer/cur")
    Observable<HttpResult<MyCustomerBean>> getMyCustomerInfo();

    /**
     * 获取所有批次数据
     */
    @GET("hydra-base-data/api/v1/product-batch/batchinfo/list/field")
    Observable<HttpResult<AllBatchInfoBean>> getAllProductBatchList(
            @Query("current") int page,
            @Query("pageSize") int pageSize
    );

    /**
     * 物流公司列表
     */
    @GET("hydra-base-data/api/v1/shipper/company/list")
    Observable<HttpResult<LogisticsCompanyBean>> getLogisticsCompanyList(@QueryMap Map<String, Object> map);

    /**
     * 检查包装相关父码
     * 包装场景类型 0- 是生产包装 1- 是仓储包装 2-混合包装 3-补码入箱
     */
    @GET("hydra-production/api/v1/code/package/check/package/parentCode")
    Observable<HttpResult<String>> checkPackagingParentCode(@QueryMap Map<String, Object> map);

    /**
     * 检查子码（通用补码入箱也可以使用）
     */
    @GET("hydra-production/api/v1/code/package/check/package/sonCode")
    Observable<HttpResult<String>> checkPackagingCode(@QueryMap Map<String, Object> map);

    /**
     * 根据码查询码的关联信息
     */
    @GET("hydra-production/api/v1/query/code/package/query/packageInfo")
    Observable<HttpResult<CodeRelationInfoResultBean>> searchCodeRelation(
            @Query("outerCode") String outerCode,
            @Query("codeTypeId") String codeTypeId
    );

    /**
     * 根据码查询码的关联信息(冈本)
     */
    @GET("hydra-production/api/v1/query/code/package/query/packageInfoWithSonLeafCount")
    Observable<HttpResult<CodeRelationInfoResultBean>> searchCodeRelationShowSingle(
            @Query("outerCode") String outerCode,
            @Query("codeTypeId") String codeTypeId
    );


    @POST("hydra-production/api/v1/package/offlineTask/pack")
    Observable<HttpResult<String>> uploadPackagingAssociation(@Body Map<String, Object> map);

    /**
     * 获取任务id
     *
     * @param taskType 任务类型 1-包装 , 2-补箱, 3-单拆, 4-整拆  17-NFC
     */
    @POST("/hydra-production/api/v1/package/offlineTask/get/task/number")
    Observable<HttpResult<String>> getPackageTaskId(@Query("taskType") String taskType);

    @POST("hydra-production/api/v1/package/offlineTask/nfc")
    Observable<HttpResult<String>> uploadNFCTask(@Body NFCTaskBean bean);

    @Streaming
    @GET
    @Headers({"Accept-Encoding:identity", "BigFile:ture"})
    Observable<Response<ResponseBody>> download(@Header("token") String token, @Url String fileUrl);

    @GET("hydra-mobile-terminal/api/v1/common/version/last")
    Observable<HttpResult<VersionBean>> getVersion(@Query("type") int type);

    /**
     * 检查包装关联的父码(毛戈平)
     */
    @GET("hydra-production/api/v1/mgp/package/check/parentCode")
    Observable<HttpResult<String>> checkPackagingParentCodeMGP(@QueryMap Map<String, Object> map);

    /**
     * 检查子码(毛戈平)
     */
    @GET("hydra-production/api/v1/mgp/package/check/sonCode")
    Observable<HttpResult<String>> checkPackagingCodeMGP(@QueryMap Map<String, Object> map);

    /**
     * 包装关联上传(毛戈平)
     */
    @POST("hydra-production/api/v1/mgp/package/create")
    Observable<HttpResult<String>> uploadPackagingAssociationMGP(@Body Map<String, Object> map);

    /**
     * 打散套标码校验
     */
    @GET("hydra-production/api/v1/split/code/tree/check/code")
    Observable<HttpResult<String>> checkDisassembleAllCode(@QueryMap Map<String, Object> map);

    /**
     * 打散套标
     */
    @POST("hydra-production/api/v1/split/code/tree/split")
    Observable<HttpResult<String>> uploadDisassembleAll(@Body Map<String, Object> map);

    /**
     * 批次管理列表
     */
    @GET("hydra-base-data/api/v1/product-batch/list")
    Observable<HttpResult<BatchManagementListBean>> getBatchManagementList(@QueryMap Map<String, Object> map);

    /**
     * 删除批次
     */
    @DELETE("hydra-base-data/api/v1/product-batch")
    Observable<HttpResult<BatchManagementListBean>> deleteBatch(@Query("id") int id);

    /**
     * 新增批次
     */
    @POST("hydra-base-data/api/v1/product-batch")
    Observable<HttpResult<String>> postAddBatch(@Body BatchManagementBean bean);

    /**
     * 编辑批次
     */
    @PUT("hydra-base-data/api/v1/product-batch")
    Observable<HttpResult<String>> putBatch(@Body BatchManagementBean bean);

    /**
     * 获取系统到期时间
     */
    @GET("hydra-user/api/v1/sys/org/time/validity")
    Observable<HttpResult<String>> getSystemExpireTime(@QueryMap Map<String, Object> map);

}
