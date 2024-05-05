package com.ggb.nirvanahappyclub.sql.operator;

import com.ggb.common_library.utils.LogUtils;
import com.ggb.nirvanahappyclub.sql.BaseOperator;
import com.ggb.nirvanahappyclub.sql.entity.UserEntity;
import com.ggb.nirvanahappyclub.sql.entity.UserEntity_;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;


/**
 * author : Cxz
 * data : 2020/1/15
 * description : 用户数据库操作类
 */
public class UserOperator extends BaseOperator<UserEntity> {

    public long getUserIdByInfo(UserEntity entity) {
        Query<UserEntity> query = box.query()
                .equal(UserEntity_.companyId, entity.getCompanyId(), QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .equal(UserEntity_.phone, entity.getPhone(), QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .build();
        UserEntity userEntity = queryUniqueDataByQB(query);
        LogUtils.xswShowLog("传入的数据是==》" + entity.getCompanyId() +"--" + entity.getPhone());
        return userEntity != null ? userEntity.getId() : putData(entity);
    }

    public UserEntity getUserIdByInfo(Long userId) {
        return  queryDataById(userId);
    }
}
