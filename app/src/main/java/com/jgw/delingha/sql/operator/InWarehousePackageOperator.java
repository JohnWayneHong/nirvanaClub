package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.InWarehousePackageEntity;
import com.jgw.delingha.sql.entity.InWarehousePackageEntity_;
import com.jgw.delingha.sql.entity.PackageConfigEntity_;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.objectbox.query.Query;
import io.objectbox.relation.RelationInfo;

public class InWarehousePackageOperator extends BasePackageCodeOperator<InWarehousePackageEntity> {

    @Override
    public InWarehousePackageEntity queryEntityByCode(String code) {
        return queryEntityByString(InWarehousePackageEntity_.outerCode, code);
    }


    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(InWarehousePackageEntity_.outerCode, code);
    }

    @Override
    public void deleteAllSonByPrentCode(String code) {
        deleteAllSonByPrentCode(InWarehousePackageEntity_.parentOuterCodeId, code);
    }

    @Override
    public List<InWarehousePackageEntity> querySonListByParentCode(String code, int count) {
        return querySonListByParentCode(InWarehousePackageEntity_.parentOuterCodeId, code, InWarehousePackageEntity_.isBoxCode, count);
    }


    /**
     * 修改码的父码
     */
    @Override
    public void updateCodeParentCode(String boxCode, String codeTypeId, List<InWarehousePackageEntity> list) {
        ArrayList<InWarehousePackageEntity> tempList = new ArrayList<>();
        for (InWarehousePackageEntity bean : list) {
            InWarehousePackageEntity entity = queryEntityByCode(bean.getOuterCode());
            if (entity == null) {
                break;
            }
            entity.setParentOuterCodeId(boxCode);
            entity.setParentOuterCodeTypeId(codeTypeId);
            tempList.add(entity);
        }
        if (tempList.isEmpty()) {
            return;
        }
        putData(tempList);
    }

    @Override
    public List<InWarehousePackageEntity> querySonListByBoxCode(String code) {
        return querySonListByBoxCode(code, 0, 0);
    }

    public List<InWarehousePackageEntity> querySonListByBoxCode(String code, int offset, int count) {
        return querySonListByBoxCode(InWarehousePackageEntity_.parentOuterCodeId, code, InWarehousePackageEntity_.id, offset, count);
    }

    @Override
    public Map<String, Integer> queryBoxAndChildCountByConfig(long configId) {
        return queryBoxAndChildCountByConfig(InWarehousePackageEntity_.configEntityId, InWarehousePackageEntity_.isBoxCode, configId);
    }


    /**
     * 根据configId查询所有父码的信息
     */
    public List<InWarehousePackageEntity> queryBoxCodeListByConfigId(long configId) {
        Query<InWarehousePackageEntity> query = box.query()
                .equal(InWarehousePackageEntity_.configEntityId, configId)
                .equal(InWarehousePackageEntity_.isBoxCode, true)
                .build();
        return queryListByLimit(query, 0, 0);
    }

    @Override
    public List<InWarehousePackageEntity> queryBoxListByConfigurationId(long configurationId) {
        return queryBoxListByConfigurationId(configurationId, 0, 0);
    }

    @Override
    public List<InWarehousePackageEntity> queryBoxListByConfigurationId(long configurationId, long id, int pageSize) {
        return queryBoxListByConfigurationId(InWarehousePackageEntity_.configEntityId, configurationId,
                InWarehousePackageEntity_.id, id, InWarehousePackageEntity_.isBoxCode, pageSize);
    }

    @Override
    public void updateParentCodeIsFull(String code, boolean isFull) {
        InWarehousePackageEntity entity = queryEntityByCode(code);
        if (entity != null) {
            //更新子码时父码已被删除
            entity.setIsFull(isFull);
            putData(entity);
        }
    }

    @Override
    public int queryBoxCodeSize(Long configId) {
        return queryBoxCodeSize(InWarehousePackageEntity_.configEntityId, configId, InWarehousePackageEntity_.isBoxCode);
    }


    @Override
    public List<InWarehousePackageEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return null;
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(InWarehousePackageEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(InWarehousePackageEntity_.configEntityId, configId);
    }

    /**
     * 查询父码
     */
    public List<String> queryParentCodeList() {
        return queryParentCodeList(InWarehousePackageEntity_.isBoxCode, InWarehousePackageEntity_.outerCode);
    }

    @Override
    public List<String> queryRealParentCodeList() {
        return queryRealParentCodeList(InWarehousePackageEntity_.parentOuterCodeId);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(InWarehousePackageEntity_.configEntity
                , PackageConfigEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }

    /**
     * 获取箱
     */
    public InWarehousePackageEntity findBox(long configId, long boxId) {
        return findBox(InWarehousePackageEntity_.configEntityId, configId,
                InWarehousePackageEntity_.isBoxCode, InWarehousePackageEntity_.id,
                InWarehousePackageEntity_.id, boxId);
    }

    @Override
    public InWarehousePackageEntity findLastBox(long configId) {
        return findLastBox(InWarehousePackageEntity_.configEntityId, configId,
                InWarehousePackageEntity_.isBoxCode, InWarehousePackageEntity_.id);
    }


    public boolean isRepeatBoxCode(String code) {
        return isRepeatBoxCode(InWarehousePackageEntity_.outerCode, code, InWarehousePackageEntity_.isBoxCode);
    }

    public void deleteCodeAndCheckParent(InWarehousePackageEntity entity) {
        removeData(entity);
    }

    @Override
    public void clearEmptyBox() {
        clearEmptyBox(InWarehousePackageEntity_.isBoxCode, InWarehousePackageEntity_.parentOuterCodeId);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(InWarehousePackageEntity_.configEntity);
    }

    @Override
    public List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        PackageConfigOperator configurationOperator = new PackageConfigOperator();
        return configurationOperator.queryAllDistinctConfigIdList(relationInfo);
    }
}
