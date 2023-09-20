package com.jgw.delingha.network.api;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.CommonOrderStockOutDetailsBean;
import com.jgw.delingha.bean.FailLogBean;
import com.jgw.delingha.bean.FlowQueryBean;
import com.jgw.delingha.bean.GBOrderExchangeWarehouseBean;
import com.jgw.delingha.bean.GBOrderExchangeWarehouseDetailsBean;
import com.jgw.delingha.bean.GBOrderStockInDetailsBean;
import com.jgw.delingha.bean.GBOrderStockInListBean;
import com.jgw.delingha.bean.GBOrderStockOutDetailsBean;
import com.jgw.delingha.bean.GBOrderStockOutListBean;
import com.jgw.delingha.bean.InventoryCodeBean;
import com.jgw.delingha.bean.InventoryDetailsBean;
import com.jgw.delingha.bean.InventoryListBean;
import com.jgw.delingha.bean.InventoryScanBean;
import com.jgw.delingha.bean.OrderExchangeGoodsBean;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.bean.OrderExchangeGoodsDetailsBean;
import com.jgw.delingha.bean.OrderExchangeGoodsResultBean;
import com.jgw.delingha.bean.OrderStockInDetailsBean;
import com.jgw.delingha.bean.OrderStockInListBean;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.bean.OrderStockUploadResultBean;
import com.jgw.delingha.bean.PackageCodeInfoBean;
import com.jgw.delingha.bean.PackageInCheckBean;
import com.jgw.delingha.bean.SplitCheckResultBean;
import com.jgw.delingha.network.result.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiLogisticsService {

    /**
     * V3 获取码信息
     */
    @GET("logistics/pda/sweep-out/house/code-product")
    Observable<HttpResult<CodeBean>> getCodeInfo(@Query("outerCodeId") String outerCodeId);

    /**
     * 直接出库校验码是否为空码
     */
    @GET("logistics/pda/code/check/direct")
    Observable<HttpResult<String>> getCheckStockOutCode(@Query("outerCodeId") String outerCodeId);


    /**
     * V3 扫码入库
     */
    @POST("logistics/pda/sweep/in/offline-task/v3020")
    Observable<HttpResult<String>> postStockInCodeV3List(@Body Map<String, Object> map);

    /**
     * V3 出库上传
     */
    @POST("logistics/pda/sweep/out/offline-task/v3020")
    Observable<HttpResult<String>> postStockOutCodeV3List(@Body Map<String, Object> map);

    /**
     * 直接出库上传
     */
    @POST("logistics/pda/sweep/out/product-direct-out/offline-task")
    Observable<HttpResult<String>> postStockOutFastCode(@Body Map<String, Object> map);

    /**
     * V3 退货码验证
     */
    @GET("logistics/pda/return/goods/check-customer-code")
    Observable<HttpResult<String>> checkStockReturnCode(@QueryMap Map<String, Object> map);

    /**
     * 退货码 校验  不校验库存信息
     */
    @GET("logistics/pda/return/goods/check-code-info")
    Observable<HttpResult<String>> checkStockReturnCodeV1(@QueryMap Map<String, Object> map);

    /**
     * V3 退货提交
     */
    @POST("logistics/pda/return/goods/offline-task/v3020")
    Observable<HttpResult<String>> postStockReturnCodeV3List(@Body Map<String, Object> map);

    /**
     * 退货提交  不校验库存
     */
    @POST("logistics/pda/return/goods/offline-task/not-check-stock")
    Observable<HttpResult<String>> postStockReturnCodeV3ListV1(@Body Map<String, Object> map);

    /**
     * V3 物流查询
     */
    @GET("logistics/pda/flow/query")
    Observable<HttpResult<FlowQueryBean>> getQueryFlow(@Query("outerCodeId") String outerCodeId);


    /**
     * 任务状态列表
     */
    @GET("logistics/pda/offline/task/list")
    Observable<HttpResult<String>> getTaskList(
            @Query("taskType") Integer taskType, //7-出库、6-入库、3-退货、2-调货、1-调仓、9-整组拆解、10-单个拆解、11-补码入箱
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("current") int currentPage,
            @Query("pageSize") int pageSize
    );

    /**
     * 失败日志
     */
    @GET("logistics/pda/fail/log")
    Observable<HttpResult<FailLogBean>> getFailLogList(@QueryMap Map<String, Object> map);

    /**
     * 包装入库检查父码
     */
    @GET("logistics/pda/package/sweep-in-house/check/parent/code")
    Observable<HttpResult<String>> checkPackageStockInParentCode(@QueryMap Map<String, Object> map);

    /**
     * 包装入库检查子码
     */
    @GET("logistics/pda/package/sweep-in-house/check/child/code")
    Observable<HttpResult<String>> checkPackageStockInSingleCode(@QueryMap Map<String, Object> map);

    /**
     * 整组拆解
     */
    @POST("logistics/pda/disassemblybox/group-split")
    Observable<HttpResult<String>> splitGroupCodeRelation(
            @QueryMap Map<String, Object> map
    );

    /**
     * 单个拆解
     */
    @POST("logistics/pda/disassemblybox/single-split")
    Observable<HttpResult<String>> splitSingleCodeRelation(
            @QueryMap Map<String, Object> map
    );

    /**
     * 整组拆解验证
     */
    @POST("logistics/pda/disassemblybox/group-split/check")
    Observable<HttpResult<SplitCheckResultBean>> checkSplitGroupCodeRelation(
            @QueryMap Map<String, String> map
    );

    /**
     * 单个拆解验证
     */
    @POST("logistics/pda/disassemblybox/single-split/check")
    Observable<HttpResult<SplitCheckResultBean>> checkSplitSingleCodeRelation(
            @QueryMap Map<String, String> map
    );

    /**
     * 根据码获取对应的产品和仓储信息(产品信息 包装规格)
     */
    @GET("logistics/pda/disassemblybox/get-product-ware")
    Observable<HttpResult<PackageCodeInfoBean>> getPackageCodeInfo(@Query("outerCodeId") String outerCodeIdList);

    /**
     * 补码入箱 校验子码
     */
    @POST("logistics/pda/disassemblybox/repair-code/check")
    Observable<HttpResult<String>> checkSubCodeStatus(@QueryMap Map<String, String> map);

    /**
     * 补码入箱上传
     */
    @POST("logistics/pda/disassemblybox/repair-code")
    Observable<HttpResult<String>> postSupplementToBox(@Body Map<String, Object> map);

    //包装入库
    @POST("logistics/pda/sweep/in/offline-task/package-sweep-in")
    Observable<HttpResult<String>> uploadPackageStockIn(@Body Map<String, Object> map);

    /**
     * 检查产品身份码是否在库存中或者属于客户
     */
    @POST("logistics/pda/purchasesale/checkOuterCodeId")
    Observable<HttpResult<String>> checkExchangeOuterCodeId(@Body Map<String, Object> map);

    /**
     * 上传调仓数据
     */
    @POST("logistics/pda/house/out/offline-task")
    Observable<HttpResult<String>> uploadExchangeWarehouse(@Body Map<String, Object> map);

    /**
     * 上传调货数据
     */
    @POST("logistics/pda/goods/out/offline-task")
    Observable<HttpResult<String>> uploadExchangeGoods(@Body Map<String, Object> map);

    /**
     * 检查码是否包装关联过并且是否入过库
     */
    @GET("logistics/pda/sweep/check-package-in")
    Observable<HttpResult<PackageInCheckBean>> checkPackageIn(@Query("outerCodeId") String outerCodeId);

    /**
     * 已关联的码进行扫码入库，检查包装关联信息入库 先装后入
     */
    @POST("logistics/pda/sweep/in/offline-task/sweep-package")
    Observable<HttpResult<String>> postStockInPackagedCodeList(@Body Map<String, Object> map);

    /**
     * 标签纠错
     */
    @POST("logistics/pda/current/code/correct")
    Observable<HttpResult<String>> postLabelEditCodeList(@Body Map<String, Object> map);


    //在库关联上传
    @POST("logistics/pda/current/code/package")
    Observable<HttpResult<String>> uploadInWareHousePackage(@Body Map<String, Object> map);

    //在库关联父码校验
    @GET("logistics/pda/code/check/package-parent")
    Observable<HttpResult<String>> checkInWareHouseParentCode(@QueryMap Map<String, Object> map);

    //在库关联子码校验
    @GET("logistics/pda/code/check/package-child")
    Observable<HttpResult<String>> checkInWareHouseChildCode(@QueryMap Map<String, Object> map);

    //获取任务id
    @GET("logistics/pda/offline/task/id")
    Observable<HttpResult<String>> getTaskId();

    //获取任务id
    @POST("logistics/pda/offline/task/redo")
    Observable<HttpResult<String>> postTaskTryAgain(@Body Map<String, Object> map);

    //获取盘点单据
    @GET("logistics/pda/inventory/task/list")
    Observable<HttpResult<InventoryListBean>> getInventoryList(@QueryMap Map<String, Object> map);

    //获取盘点任务详情
    @GET("logistics/pda/inventory/task")
    Observable<HttpResult<InventoryDetailsBean>> getInventoryDetails(@Query("houseList") String houseList);

    //盘点上传
    @POST("logistics/pda/inventory/task/single-inventory")
    Observable<HttpResult<InventoryScanBean>> postSingleInventory(@Body Map<String, Object> map);

    //获取已上传盘点记录
    @GET("logistics/pda/inventory/task/product-detail")
    Observable<HttpResult<InventoryCodeBean>> getInventoryFinishListByProduct(@QueryMap Map<String, Object> map);

    //获取单据入库单据列表
    @GET("logistics/pda/sweep/in/order-list")
    Observable<HttpResult<OrderStockInListBean>> getOrderStockInList(@QueryMap Map<String, Object> map);  //获取单据入库单据列表

    //获取订单入库详情
    @GET("logistics/pda/sweep/in")
    Observable<HttpResult<OrderStockInDetailsBean>> getOrderStockInDetails(@Query("inHouseList") String houseList);

    //获取订单入库码校验
    @GET("logistics/pda/code/check/top-level")
    Observable<HttpResult<OrderStockScanBean>> checkOrderStockInCode(@QueryMap Map<String, Object> map);

    //单据入库按商品上传
    @POST("logistics/pda/sweep/in/order-product")
    @Headers("BigFile:ture")
    Observable<HttpResult<OrderStockUploadResultBean>> postOrderStockInUploadByProduct(@Body Map<String, Object> map);


    //获取单据出库单据列表
    @GET("logistics/pda/sweep/out/order-list")
    Observable<HttpResult<OrderStockOutListBean>> getOrderStockOutList(@QueryMap Map<String, Object> map);  //获取单据入库单据列表

    //获取订单出库详情
    @GET("logistics/pda/sweep/out")
    Observable<HttpResult<OrderStockOutDetailsBean>> getOrderStockOutDetails(@Query("outHouseList") String houseList
            , @Query("wareHouseOutId") String wareHouseOutId);

    //获取订单出库码校验
    @GET("logistics/pda/code/check/out-current")
    Observable<HttpResult<OrderStockScanBean>> checkOrderStockOutCode(@QueryMap Map<String, Object> map);

    //单据入库按商品上传
    @POST("logistics/pda/sweep/out/order-product")
    @Headers("BigFile:ture")
    Observable<HttpResult<OrderStockUploadResultBean>> postOrderStockOutUploadByProduct(@Body Map<String, Object> map);

    /**
     * 可用调货单列表
     */
    @GET("logistics/pda/goods/out/order-detail")
    Observable<HttpResult<OrderExchangeGoodsDetailsBean>> getOrderExchangeGoodsDetails(@QueryMap Map<String, Object> map);

    /**
     * 订单调货上传
     */
    @POST("logistics/pda/goods/out/order-sweep/v2030")
    Observable<HttpResult<OrderExchangeGoodsResultBean>> postOrderExchangeGoodsList(@Body Map<String, Object> map);

    /**
     * 调货单详情
     */
    @GET("logistics/pda/goods/out/enable-order-list")
    Observable<HttpResult<OrderExchangeGoodsBean>> getOrderExchangeGoodsList(@QueryMap Map<String, Object> map);

    /**
     * 订单调货扫码校验
     */
    @GET("logistics/pda/goods/out/order-code-check/v2030")
    Observable<HttpResult<OrderExchangeGoodsCodeBean>> getOrderGoodsCodeInfo(@QueryMap Map<String, Object> map);

    /**
     * 万维扫码出库,调仓校验
     */
    @GET("logistics/pda/code/check/current-code")
    Observable<HttpResult<String>> getWanWeiCheckStockCode(@QueryMap Map<String, Object> map);

    /**
     * 万维扫码退货校验
     */
    @GET("logistics/pda/return/goods/check-customer-code")
    Observable<HttpResult<String>> checkWanWeiStockReturnCode(@QueryMap Map<String, Object> map);

    /**
     * V3 退货提交
     */
    @POST("logistics/pda/return/goods/offline-task/straight-back")
    Observable<HttpResult<String>> postWanWeiStockReturnCodeV3List(@Body Map<String, Object> map);


    /**
     * 入库统计
     */
    @POST("logistics/pda/analyze/statistics/in")
    Observable<HttpResult<String>> postStatisticsStockInList(@Body Map<String, Object> map);

    /**
     * 出库统计
     */
    @POST("logistics/pda/analyze/statistics/out")
    Observable<HttpResult<String>> postStatisticsStockOutList(@Body Map<String, Object> map);

    /**
     * 退货统计
     */
    @POST("logistics/pda/analyze/statistics/return-goods")
    Observable<HttpResult<String>> postStatisticsStockReturnList(@Body Map<String, Object> map);

    /**
     * 调仓统计
     */
    @POST("logistics/pda/analyze/statistics/relocate")
    Observable<HttpResult<String>> postStatisticsExchangeWarehouseList(@Body Map<String, Object> map);

    /**
     * 调货统计
     */
    @POST("logistics/pda/analyze/statistics/transfer-goods")
    Observable<HttpResult<String>> postStatisticsExchangeGoodsList(@Body Map<String, Object> map);

    /**
     * 最近入库码查询
     */
    @GET("logistics/pda/code/query/recent/in")
    Observable<HttpResult<String>> queryRecentIn(@Query("outerCodeId") String outerCodeId);

    /**
     * 最近出库码查询
     */
    @GET("logistics/pda/code/query/recent/out")
    Observable<HttpResult<String>> queryRecentOut(@Query("outerCodeId") String outerCodeId);

    /**
     * 最近调仓码查询
     */
    @GET("logistics/pda/code/query/recent/relocate")
    Observable<HttpResult<String>> queryRecentRelocate(@Query("outerCodeId") String outerCodeId);

    /**
     * 最近调货码查询
     */
    @GET("logistics/pda/code/query/recent/transfer")
    Observable<HttpResult<String>> queryRecentTransfer(@Query("outerCodeId") String outerCodeId);

    /**
     * 最近退货码查询
     */
    @GET("logistics/pda/code/query/recent/return")
    Observable<HttpResult<String>> queryRecentReturn(@Query("outerCodeId") String outerCodeId);


    /**
     * 标识替换 扫码校验  type 1 旧码    2 新码
     */
    @GET("logistics/pda/mark/check")
    Observable<HttpResult<String>> checkIdentificationReplaceCode(@Query("outerCodeId") String outerCodeId,
                                                                  @Query("type") int type);

    /**
     * 标识替换 上传
     */
    @POST("logistics/pda/mark/add")
    Observable<HttpResult<String>> uploadIdentificationReplace(@Body Map<String, String> map);

    //--------------订单出库

    /**
     * 订单出库订单列表
     */
    @GET("logistics/pda/wh-house-list/out/list")
    Observable<HttpResult<String>> getCommonOrderStockOutList(@QueryMap Map<String, Object> map);  //获取单据入库单据列表

    /**
     * 获取订单出库详情
     */
    @GET("logistics/pda/wh-house-list/out/detail")
    Observable<HttpResult<CommonOrderStockOutDetailsBean>> getOrderStockOutDetails(@Query("houseList") String houseList
            , @Query("id") String id, @Query("year") String year);


    /**
     * 获取订单出库码校验
     */
    @GET("logistics/pda/house-list/out/task/code-check")
    Observable<HttpResult<OrderStockScanBean>> checkCommonOrderStockOutCode(@QueryMap Map<String, Object> map);
    //--------------订单出库


    //-----------------通用订单任务------------

    /**
     * 获取任务id,同时创建任务
     * houseList 单据号(订单任务时传)
     * taskType 与任务列表同步
     */
    @POST("logistics/pda/house-list/task/id")
    Observable<HttpResult<String>> postCreateTaskId(@Body Map<String, Object> map);

    /**
     * 获取订单商品当前扫码信息
     */
    @GET("logistics/pda/house-list/task/sweep-code-info")
    Observable<HttpResult<String>> getOrderProductCurrentCodeInfo(@QueryMap Map<String, Object> map);

    /**
     * 单据产品手输数量录入
     */
    @POST("logistics/pda/house-list/task/input")
    Observable<HttpResult<String>> postInputNumber(@Body Map<String, Object> map);

    /**
     * 获取订单商品已扫码列表
     */
    @GET("logistics/pda/house-list/task/code-list")
    Observable<HttpResult<List<OrderStockScanBean>>> getOrderProductCodeList(@QueryMap Map<String, Object> map);


    /**
     * 删除指定的已上传的码
     *
     */
    @POST("logistics/pda/house-list/task/del-undo-code")
    Observable<HttpResult<String>> postDeleteOrderProductCode(@Body Map<String, Object> map);

    /**
     * 删除全部已上传的码
     *
     */
    @POST("logistics/pda/house-list/task/del-undo-code-all")
    Observable<HttpResult<String>> postDeleteAllOrderProductCode(@Body Map<String, Object> map);

    /**
     * 执行任务
     */
    @POST("logistics/pda/house-list/task/do")
    Observable<HttpResult<String>> postExecuteTask(@Body Map<String, Object> map);
    //-----------------通用订单任务------------

    //获取单据入库单据列表(德令哈)
    @GET("logistics/pda/ware-house-in/receipt/order-list")
    Observable<HttpResult<OrderStockInListBean>> getDLHOrderStockInList(@QueryMap Map<String, Object> map);

    //入库单上传(德令哈)
    @POST("logistics/pda/ware-house-in/receipt/confirm-warehousing")
    Observable<HttpResult<String>> postDLHOrderStockInUpload(@Body Map<String, Object> map);

    //获取单据出库单据列表(德令哈)
    @GET("logistics/pda/ware-house-out/receipt/order-list")
    Observable<HttpResult<OrderStockOutListBean>> getDLHOrderStockOutList(@QueryMap Map<String, Object> map);  //获取单据入库单据列表

    //获取订单出库详情(德令哈)
    @GET("logistics/pda/ware-house-out/receipt/detail")
    Observable<HttpResult<OrderStockOutDetailsBean>> getDLHOrderStockOutDetails(@Query("outHouseList") String houseList
            , @Query("wareHouseOutId") String wareHouseOutId);


    //获取订单出库码校验(德令哈)
    @GET("hydra-production/api/v1/query/code/package/query/getCodeInfoAndParentCodeVo")
    Observable<HttpResult<String>> checkDLHOrderStockOutCode(@QueryMap Map<String, Object> map);


    //点货(德令哈)
    @POST("logistics/pda/ware-house-out/receipt/tall-goods")
    Observable<HttpResult<String>> postDLHSave(@Body Map<String, Object> map);

    //单据入库按商品上传(德令哈)
    @POST("logistics/pda/ware-house-out/receipt/deliver-one")
    Observable<HttpResult<String>> postDLHOrderStockOutUploadByProduct(@Body Map<String, Object> map);

    //单据入库上传(德令哈)
    @POST("logistics/pda/ware-house-out/receipt/deliver-all")
    Observable<HttpResult<String>> postDLHOrderStockOutUpload(@Body Map<String, Object> map);

}
