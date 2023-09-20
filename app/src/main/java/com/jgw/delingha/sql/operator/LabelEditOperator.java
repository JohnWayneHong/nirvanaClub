package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.LabelEditEntity;
import com.jgw.delingha.sql.entity.LabelEditEntity_;

import java.util.List;

/**
 * author : xsw
 * data : 2020/2/12
 * description : 出库数据库操作类
 */
public class LabelEditOperator extends BaseWaitUploadCodeOperator<LabelEditEntity> {

    @Override
    public LabelEditEntity queryEntityByCode(String code) {
        return queryEntityByString(LabelEditEntity_.code, code);
    }


    /**
     * 根据code删除某一条数据
     */
    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(LabelEditEntity_.code, code);
    }

    @Nullable
    public List<LabelEditEntity> queryDataByConfigIdV2(long configId, long currentId, int limit) {
        return queryGroupDataByConfigIdDesc(LabelEditEntity_.configEntityId, configId,
                LabelEditEntity_.id, currentId, limit);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(LabelEditEntity_.configEntityId, configId);
    }

    @Nullable
    @Override
    public List<LabelEditEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(LabelEditEntity_.configEntityId, configId, LabelEditEntity_.id, page, pageSize);
    }

    //获取配置id对应的记录条数
    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(LabelEditEntity_.configEntityId, configId);
    }

    /**
     * 批量删除数据
     */
    public void deleteData(List<LabelEditEntity> list) {
        removeData(list);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(LabelEditEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(LabelEditEntity_.configEntity,
                ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }

}
