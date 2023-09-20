package com.jgw.delingha.sql.operator;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.WanWeiStockOutEntity;
import com.jgw.delingha.sql.entity.WanWeiStockOutEntity_;

import java.util.List;

/**
 * author : xsw
 * data : 2020/2/12
 * description : 出库数据库操作类(万维)
 */
public class WanWeiStockOutOperator extends BaseWaitUploadCodeOperator<WanWeiStockOutEntity> {

    @Override
    public WanWeiStockOutEntity queryEntityByCode(String code) {
        return queryEntityByString(WanWeiStockOutEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(WanWeiStockOutEntity_.code, code);
    }

    public List<WanWeiStockOutEntity> queryGroupDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(WanWeiStockOutEntity_.configEntityId, configId
                , WanWeiStockOutEntity_.id, currentId, limit);
    }

    public long queryVerifyingDataConfigId(long configId) {
        return queryCountByStatus(WanWeiStockOutEntity_.configEntityId, configId
                , WanWeiStockOutEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public List<WanWeiStockOutEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(WanWeiStockOutEntity_.configEntityId, configId
                , WanWeiStockOutEntity_.id, page, pageSize);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(WanWeiStockOutEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(WanWeiStockOutEntity_.configEntityId, configId);
    }

    public long querySingleNumberByConfigId(long configId) {
        return querySingleNumberCountByConfigId(WanWeiStockOutEntity_.configEntityId, configId, WanWeiStockOutEntity_.singleNumber);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(WanWeiStockOutEntity_.configEntity);
    }

    public long queryCount() {
        return queryCountByCurrentUser(WanWeiStockOutEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
