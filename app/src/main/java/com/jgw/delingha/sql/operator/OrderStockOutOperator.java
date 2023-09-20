package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.OrderStockOutEntity;
import com.jgw.delingha.sql.entity.OrderStockOutEntity_;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;

import java.util.List;

import io.objectbox.relation.ToMany;

public class OrderStockOutOperator extends BaseOrderOperator<OrderStockOutEntity> {

    @Override
    public void deleteAllCodeByOrderCode(String orderCode) {
        OrderStockOutEntity entity = queryEntityByCode(orderCode);
        if (entity == null) {
            return;
        }
        deleteAllCodeByOrderEntity(entity);
    }
    @Override
    public void deleteAllCodeByOrderEntity(OrderStockOutEntity entity) {
        List<OrderStockOutProductInfoEntity> productList = entity.getProductList();
        if (productList != null && !productList.isEmpty()) {
            OrderStockOutScanCodeOperator codeOperator = new OrderStockOutScanCodeOperator();
            for (OrderStockOutProductInfoEntity e : productList) {
                //noinspection rawtypes
                List codeList = e.getScanCodeList();
                if (codeList != null && !codeList.isEmpty()) {
                    //noinspection unchecked
                    codeOperator.removeData(codeList);
                }
            }
            OrderStockOutProductOperator productOperator = new OrderStockOutProductOperator();
            productOperator.removeData(productList);
        }
        removeData(entity);
    }

    /**
     * 根据订单号获取订单信息
     *
     * @param orderCode 订单号
     * @return 订单信息(可能非当前用户)
     */
    @Override
    public OrderStockOutEntity queryEntityByCode(String orderCode) {
        return queryEntityByString(OrderStockOutEntity_.orderCode, orderCode);
    }


    @Override
    public long queryCount() {
        return queryCountByCurrentUser(OrderStockOutEntity_.userEntityId);
    }

    @Override
    public List<OrderStockOutEntity> queryAll() {
        return queryListByCurrentUser(OrderStockOutEntity_.userEntityId);
    }

    public void clearEmptyOrder() {
        List<OrderStockOutEntity> orderList = queryAll();
        for (OrderStockOutEntity o:orderList){
            ToMany<OrderStockOutProductInfoEntity> productList = o.getProductList();
            if (productList.isEmpty()){
                removeData(o);
                continue;
            }
            boolean isEmpty=true;
            for (OrderStockOutProductInfoEntity p:productList){
                if (!p.getCodeList().isEmpty()){
                    isEmpty=false;
                    break;
                }
            }
            if (isEmpty){
                removeData(o);
            }
        }
    }
}
