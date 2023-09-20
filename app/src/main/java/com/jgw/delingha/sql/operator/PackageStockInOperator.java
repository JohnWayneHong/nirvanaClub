package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity_;
import com.jgw.delingha.sql.entity.PackageStockInEntity;
import com.jgw.delingha.sql.entity.PackageStockInEntity_;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.objectbox.relation.RelationInfo;

public class PackageStockInOperator extends BasePackageCodeOperator<PackageStockInEntity> {

    @Override
    public PackageStockInEntity queryEntityByCode(String code) {
        return queryEntityByString(PackageStockInEntity_.outerCode, code);
    }

    public boolean isRepeatBoxCode(String code) {
        return isRepeatBoxCode(PackageStockInEntity_.outerCode, code, PackageStockInEntity_.isBoxCode);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(PackageStockInEntity_.outerCode, code);
    }

    public List<PackageStockInEntity> querySonListByBoxCode(String code) {
        return querySonListByBoxCode(code, 0, 0);
    }

    public List<PackageStockInEntity> querySonListByBoxCode(String code, int offSet, int limit) {
        return querySonListByBoxCode(PackageStockInEntity_.parentOuterCodeId, code, PackageStockInEntity_.id, offSet, limit);
    }

    @Override
    public PackageStockInEntity findBox(long configId, long boxId) {
        return findBox(PackageStockInEntity_.configEntityId, configId,
                PackageStockInEntity_.isBoxCode, PackageStockInEntity_.id,
                PackageStockInEntity_.id, boxId);
    }

    @Override
    public PackageStockInEntity findLastBox(long configId) {
        return findLastBox(PackageStockInEntity_.configEntityId, configId,
                PackageStockInEntity_.isBoxCode, PackageStockInEntity_.id);

    }

    @Override
    public Map<String, Integer> queryBoxAndChildCountByConfig(long configId) {
        return queryBoxAndChildCountByConfig(PackageStockInEntity_.configEntityId, PackageStockInEntity_.isBoxCode, configId);
    }

    public void deleteAllSonByPrentCode(String code) {
        deleteAllSonByPrentCode(PackageStockInEntity_.parentOuterCodeId, code);
    }

    @Override
    public List<PackageStockInEntity> querySonListByParentCode(String code, int count) {
        return querySonListByParentCode(PackageStockInEntity_.parentOuterCodeId, code, PackageStockInEntity_.isBoxCode, count);
    }

    public void updateCodeParentCode(String boxCode, String codeTypeId, List<PackageStockInEntity> list) {
        ArrayList<PackageStockInEntity> tempList = new ArrayList<>();
        for (PackageStockInEntity bean : list) {
            PackageStockInEntity entity = queryEntityByCode(bean.getOuterCode());
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
    public List<PackageStockInEntity> queryBoxListByConfigurationId(long configurationId) {
        return queryBoxListByConfigurationId(configurationId, 0, 0);
    }

    @Override
    public List<PackageStockInEntity> queryBoxListByConfigurationId(long configurationId, long id, int pageSize) {
        return queryBoxListByConfigurationId(PackageStockInEntity_.configEntityId, configurationId,
                PackageStockInEntity_.id, id, PackageStockInEntity_.isBoxCode, pageSize);
    }

    @Override
    public List<PackageStockInEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return null;
    }

    public void updateParentCodeIsFull(String code, boolean isFull) {
        PackageStockInEntity entity = queryEntityByCode(code);
        if (entity != null) {
            //更新子码时父码已被删除
            entity.setIsFull(isFull);
            putData(entity);
        }
    }

    public int queryBoxCodeSize(Long configId) {
        return queryBoxCodeSize(PackageStockInEntity_.configEntityId, configId, PackageStockInEntity_.isBoxCode);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(PackageStockInEntity_.configEntity);
    }

    @Override
    public List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        PackageConfigOperator configurationOperator = new PackageConfigOperator();
        return configurationOperator.queryAllDistinctConfigIdList(relationInfo);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(PackageStockInEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(PackageStockInEntity_.configEntityId, configId);
    }

    public List<String> queryParentCodeList() {
        return queryParentCodeList(PackageStockInEntity_.isBoxCode, PackageStockInEntity_.outerCode);
    }


    public List<String> queryRealParentCodeList() {
        return queryRealParentCodeList(PackageStockInEntity_.parentOuterCodeId);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(PackageStockInEntity_.configEntity
                , PackageConfigEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }

    public void deleteCodeAndCheckParent(PackageStockInEntity entity) {
        removeData(entity);
    }


    @Override
    public void clearEmptyBox() {
        clearEmptyBox(PackageStockInEntity_.isBoxCode, PackageStockInEntity_.parentOuterCodeId);
    }
}
