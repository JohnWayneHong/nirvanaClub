package com.jgw.delingha.sql.operator;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.CustomerEntity_;

import java.util.List;

import io.objectbox.query.QueryBuilder;

/**
 * author : xsw
 * data : 2020/3/3
 * description : 客户信息操作类
 */
public class CustomerOperator extends BaseOperator<CustomerEntity> {

    /**
     * 根据上级ID和搜索值 获取下级客户
     *
     * @param searchStr  搜索值
     * @param customerId 上级客户id
     * @return 下级客户分页数据
     */
    @Nullable
    public List<CustomerEntity> queryCustomerListByUpperAndSearch(String searchStr, String customerId, int page, int page_size) {
        QueryBuilder<CustomerEntity> query = box.query();
        if (!TextUtils.isEmpty(searchStr) && !TextUtils.isEmpty(customerId)) {
            query.contains(CustomerEntity_.customerName, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(CustomerEntity_.customerCode, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .equal(CustomerEntity_.customerSuperior, customerId, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        } else if (!TextUtils.isEmpty(searchStr)) {
            query.contains(CustomerEntity_.customerName, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(CustomerEntity_.customerCode, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        } else if (!TextUtils.isEmpty(customerId)) {
            query.equal(CustomerEntity_.customerSuperior, customerId, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        return queryListByPage(query.build(), page, page_size);
    }
}
