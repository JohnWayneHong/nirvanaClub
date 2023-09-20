package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.WanWeiStockReturnEntity;
import com.jgw.delingha.sql.entity.WanWeiStockReturnEntity_;

import java.util.List;

/**
 * author : Cxz
 * data : 2020/1/19
 * description :
 */
public class WanWeiStockReturnOperator extends BaseWaitUploadCodeOperator<WanWeiStockReturnEntity> {

    @Override
    public WanWeiStockReturnEntity queryEntityByCode(String code) {
        return queryEntityByString(WanWeiStockReturnEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(WanWeiStockReturnEntity_.code, code);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(WanWeiStockReturnEntity_.configEntityId, configId);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(WanWeiStockReturnEntity_.configEntityId, configId);
    }

    public long querySingleNumberByConfigId(long configId) {
        return querySingleNumberCountByConfigId(WanWeiStockReturnEntity_.configEntityId
                , configId
                , WanWeiStockReturnEntity_.singleNumber);
    }

    @Nullable
    public List<WanWeiStockReturnEntity> queryGroupDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(WanWeiStockReturnEntity_.configEntityId, configId
                , WanWeiStockReturnEntity_.id, currentId, limit);
    }

    public long queryVerifyingDataByConfigId(long configId) {
        return queryCountByStatus(WanWeiStockReturnEntity_.configEntityId, configId
                , WanWeiStockReturnEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Nullable
    @Override
    public List<WanWeiStockReturnEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(WanWeiStockReturnEntity_.configEntityId, configId
                , WanWeiStockReturnEntity_.id, page, pageSize);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(WanWeiStockReturnEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(WanWeiStockReturnEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
