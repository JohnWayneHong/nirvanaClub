package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.StockOutEntity;
import com.jgw.delingha.sql.entity.StockOutEntity_;

import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * author : xsw
 * data : 2020/2/12
 * description : 出库数据库操作类
 */
public class StockOutOperator extends BaseWaitUploadCodeOperator<StockOutEntity> {

    @Override
    public StockOutEntity queryEntityByCode(String code) {
        return queryEntityByString(StockOutEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(StockOutEntity_.code, code);
    }

    @Nullable
    public List<StockOutEntity> querySuccessDataByConfigId(long configId) {
        return queryListByStatus(StockOutEntity_.configEntityId, configId
                , StockOutEntity_.codeStatus, CodeBean.STATUS_CODE_SUCCESS);
    }

    public long queryErrorDataByConfigId(long configId) {
        Query<StockOutEntity> query = box.query()
                .equal(StockOutEntity_.configEntityId, configId)
                .notEqual(StockOutEntity_.codeStatus, CodeBean.STATUS_CODE_SUCCESS)
                .build();
        return queryCountByQB(query);
    }

    @Nullable
    public List<StockOutEntity> queryDataByConfigId(long configId, long currentId, int limit) {
        return queryDataByConfigId(configId, currentId, limit, 0);
    }

    @Nullable
    public List<StockOutEntity> queryDataByConfigId(long configId, long currentId, int limit, int filterStatus) {
        QueryBuilder<StockOutEntity> qb = box.query()
                .equal(StockOutEntity_.configEntityId, configId)
                .greaterOrEqual(StockOutEntity_.id, currentId);
        if (filterStatus != 0) {
            qb.equal(StockOutEntity_.codeStatus, filterStatus);
        }
        Query<StockOutEntity> query = qb.build();
        return queryListByLimit(query, 0, limit);
    }

    @Override
    public List<StockOutEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(StockOutEntity_.configEntityId, configId
                , StockOutEntity_.id, page, pageSize);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(StockOutEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(StockOutEntity_.configEntityId, configId);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(StockOutEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(StockOutEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
