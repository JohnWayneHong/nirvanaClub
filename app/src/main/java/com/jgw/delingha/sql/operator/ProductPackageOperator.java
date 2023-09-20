package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.ProductPackageInfoEntity;
import com.jgw.delingha.sql.entity.ProductPackageInfoEntity_;


/**
 * author : xsw
 * data : 2020/3/3
 * description : 商品信息操作类
 */
public class ProductPackageOperator extends BaseOperator<ProductPackageInfoEntity> {

    public ProductPackageInfoEntity queryDataByProductId(String id) {
        return queryEntityByString(ProductPackageInfoEntity_.productId, id);
    }

    /**
     * @param id 商品时候有包装限制  默认有
     * @return true 限制包装规格 false不限制包装规格
     */
    public static boolean checkPackageRestrictProductId(String id) {
        ProductPackageOperator operator = new ProductPackageOperator();
        ProductPackageInfoEntity entity =operator.queryDataByProductId(id);
        if (entity == null) {
            return true;
        }
        return entity.getPackageRestricted() == 1;
    }
}
