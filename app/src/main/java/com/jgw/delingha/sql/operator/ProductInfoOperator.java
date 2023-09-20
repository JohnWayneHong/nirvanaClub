package com.jgw.delingha.sql.operator;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity_;

import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * author : xsw
 * data : 2020/3/3
 * description : 商品信息操作类
 */
public class ProductInfoOperator extends BaseOperator<ProductInfoEntity> {

    @Nullable
    public List<ProductInfoEntity> queryWareHouseProductBySearchStr(String searchStr, int page, int pageSize) {
        QueryBuilder<ProductInfoEntity> qb = box.query();
        if (!TextUtils.isEmpty(searchStr)){
            qb.contains(ProductInfoEntity_.productName,searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(ProductInfoEntity_.productCode,searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        qb.equal(ProductInfoEntity_.haveWarehouse,true);
        Query<ProductInfoEntity> query = qb.build();
        return queryListByPage(query,page,pageSize);
    }

    @Nullable
    public List<ProductInfoEntity> queryPackagedDataBySearchStr(String searchStr, int page, int pageSize) {
        QueryBuilder<ProductInfoEntity> qb = box.query();
        if (!TextUtils.isEmpty(searchStr)){
            qb.contains(ProductInfoEntity_.productName,searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(ProductInfoEntity_.productCode,searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        qb.equal(ProductInfoEntity_.havePackaged,true);
        Query<ProductInfoEntity> query = qb.build();
        return queryListByPage(query,page,pageSize);
    }

    @Nullable
    public List<ProductInfoEntity> queryPackagedAndStockInDataBySearchStr(String searchStr, int page, int pageSize) {
        QueryBuilder<ProductInfoEntity> qb = box.query();
        if (!TextUtils.isEmpty(searchStr)){
            qb.contains(ProductInfoEntity_.productName,searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(ProductInfoEntity_.productCode,searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        qb.equal(ProductInfoEntity_.havePackaged,true);
        qb.equal(ProductInfoEntity_.haveWarehouse,true);
        Query<ProductInfoEntity> query = qb.build();
        return queryListByPage(query,page,pageSize);
    }
}
