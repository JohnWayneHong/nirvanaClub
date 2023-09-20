package com.jgw.delingha.sql.operator;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity_;

import java.util.List;

import io.objectbox.query.QueryBuilder;

/**
 * author : xsw
 * data : 2020/3/3
 * description : 商品信息操作类
 */
public class ProductBatchOperator extends BaseOperator<ProductBatchEntity> {

    @Nullable
    public List<ProductBatchEntity> queryDataBySearchStr(@NonNull String productId, String searchStr, int page, int page_size) {
        QueryBuilder<ProductBatchEntity> query = box.query();
        if (!TextUtils.isEmpty(searchStr)) {
            query.contains(ProductBatchEntity_.batchCode, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(ProductBatchEntity_.batchName, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        query.equal(ProductBatchEntity_.productId, productId, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        return queryListByPage(query.build(), page, page_size);
    }
}
