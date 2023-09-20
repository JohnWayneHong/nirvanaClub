package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.OrderStockOutScanCodeEntity;
import com.jgw.delingha.sql.entity.OrderStockOutScanCodeEntity_;

import java.util.List;

import io.objectbox.query.Query;

public class OrderStockOutScanCodeOperator extends BaseOrderScanCodeOperator<OrderStockOutScanCodeEntity> {

    @Override
    public void removeEntityByCode(String code) {
        removeEntityByString(OrderStockOutScanCodeEntity_.outerCode, code);
    }

    @Override
    public void deleteByProductId(Long productId) {
        deleteByProductId(OrderStockOutScanCodeEntity_.orderStockOutProductInfoEntityId, productId);
    }

    @Override
    public void deleteVerifyingCodesByProductId(Long productId) {
        deleteVerifyingCodesByProductId(OrderStockOutScanCodeEntity_.orderStockOutProductInfoEntityId, productId,
                OrderStockOutScanCodeEntity_.codeStatus);

    }

    @Override
    public OrderStockOutScanCodeEntity queryEntityByCode(String code) {
        return queryEntityByString(OrderStockOutScanCodeEntity_.outerCode, code);
    }

    @Override
    public List<OrderStockOutScanCodeEntity> queryDataByPage(long productId, int page, int pageSize) {
        return queryDataByPageOrderDesc(OrderStockOutScanCodeEntity_.orderStockOutProductInfoEntityId,
                productId, page, pageSize, OrderStockOutScanCodeEntity_.id);
    }
    public List<OrderStockOutScanCodeEntity> queryAllDataByProductId(long productId) {
        Query<OrderStockOutScanCodeEntity> query = box.query()
                .equal(OrderStockOutScanCodeEntity_.orderStockOutProductInfoEntityId, productId)
                .build();
        return queryListByPage(query, 0, 0);
    }

    @Override
    public long queryCountByProductId(long productId) {
        return queryCountByProductId(OrderStockOutScanCodeEntity_.orderStockOutProductInfoEntityId, productId);
    }
}

