package com.jgw.delingha.module.order_task;

import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.OrderStockOutScanCodeEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;

public class TaskModel {

    /**
     * 创建任务
     *
     * @param houseList 单据号
     * @param taskType  {@link com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel}
     * @return 任务id
     */
    public Observable<String> createTask(String houseList, int taskType) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("houseList", houseList);
        map.put("taskType", taskType);
        return HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postCreateTaskId(map)
                .compose(HttpUtils.applyIOSchedulers());
    }

    /**
     * 获取已校验码信息
     *
     * @param productRecordId 产品记录Id
     */
    public Observable<String> getOrderProductCurrentCodeInfo(String productRecordId) {
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("productRecordId", productRecordId);
        map1.put("status", 0);//0-已校验未执行，1-成功，2-失败 ，3- 未知
        return HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getOrderProductCurrentCodeInfo(map1)
                .compose(HttpUtils.applyIOSchedulers());
    }
    /**
     * 编辑手输数量
     *
     * @param enterNumber     手输数量
     * @param productRecordId 产品记录Id
     * @param taskId          任务id
     */
    public Observable<String> editInputNumber(int enterNumber, String productRecordId, String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("enterNumber", enterNumber);
        map.put("productRecordId", productRecordId);
        map.put("taskId", taskId);
        return HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postInputNumber(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers());
    }

    /**
     * 获取当前已上传的码列表
     *
     * @param taskId          任务id
     * @param productRecordId 产品记录Id
     * @return 当前上传成功的全部码列表
     */
    public Observable<List<OrderStockOutScanCodeEntity>> getOrderProductAllCodeList(String taskId, String productRecordId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("current", 1);
        map.put("pageSize", 10 * 10000);
        map.put("productRecordId", productRecordId);
        map.put("taskId", taskId);
        //0-未完成，1-成功，2-失败 ，3- 未知
        map.put("status", 0);
        return HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getOrderProductCodeList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(orderStockScanBeans -> {
                    ArrayList<OrderStockOutScanCodeEntity> list = new ArrayList<>();
                    OrderStockOutScanCodeEntity e;
                    for (OrderStockScanBean o : orderStockScanBeans) {
                        e = new OrderStockOutScanCodeEntity();
                        e.setCode(o.outerCodeId);
                        e.setCodeStatus(BaseCodeEntity.STATUS_CODE_SUCCESS);
                        e.setSingleNumber(o.singleNumber);
                        e.setCodeLevel(o.currentLevel);
                        list.add(e);
                    }
                    return list;
                });
    }

    /**
     * 删除指定的已上传的码
     *
     * @param code   码
     * @param taskId 任务id
     */
    public Observable<String> deleteOrderProductCode(String code, String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("outerCodeId",code);
        map.put("taskId",taskId);
        return HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postDeleteOrderProductCode(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers());
    }
    /**
     * 删除全部的已上传的码
     *
     * @param taskId 任务id
     */
    public Observable<String> deleteAllOrderProductCode(String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("taskId",taskId);
        return HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postDeleteAllOrderProductCode(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers());
    }

    /**
     * 执行任务
     *
     * @param taskId      任务id
     * @param taskType    任务类型(列表返回) 旧任务需要使用
     *                    {@link com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel}
     * @param taskVersion 任务版本 0 旧任务，1 新任务
     * @return
     */
    public Observable<String> executeTask(String taskId, int taskType, int taskVersion) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("taskId", taskId);
        map.put("taskType", taskType);
        map.put("taskVersion", taskVersion);
        return HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postExecuteTask(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers());
    }

}
