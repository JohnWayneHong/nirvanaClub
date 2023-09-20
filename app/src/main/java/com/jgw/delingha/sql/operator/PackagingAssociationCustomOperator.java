package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity_;
import com.jgw.delingha.sql.entity.PackagingAssociationCustomEntity;
import com.jgw.delingha.sql.entity.PackagingAssociationCustomEntity_;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.objectbox.query.Query;
import io.objectbox.relation.RelationInfo;

public class PackagingAssociationCustomOperator extends BasePackageCodeOperator<PackagingAssociationCustomEntity> {
    @Override
    public PackagingAssociationCustomEntity queryEntityByCode(String code) {
        return queryEntityByString(PackagingAssociationCustomEntity_.outerCode, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(PackagingAssociationCustomEntity_.outerCode, code);
    }

    @Override
    public List<PackagingAssociationCustomEntity> querySonListByBoxCode(String code) {
        return querySonListByBoxCode(code, 0, 0);
    }

    public List<PackagingAssociationCustomEntity> querySonListByBoxCode(String code, int offset, int count) {
        return querySonListByBoxCode(PackagingAssociationCustomEntity_.parentOuterCodeId, code, PackagingAssociationCustomEntity_.id, offset, count);
    }

    @Override
    public PackagingAssociationCustomEntity findBox(long configId, long boxId) {
        return findBox(PackagingAssociationCustomEntity_.configEntityId, configId,
                PackagingAssociationCustomEntity_.isBoxCode, PackagingAssociationCustomEntity_.id,
                PackagingAssociationCustomEntity_.id, boxId);
    }

    @Override
    public PackagingAssociationCustomEntity findLastBox(long configId) {
       return findLastBox(PackagingAssociationCustomEntity_.configEntityId, configId,
                PackagingAssociationCustomEntity_.isBoxCode, PackagingAssociationCustomEntity_.id);
    }

    @Override
    public Map<String, Integer> queryBoxAndChildCountByConfig(long configId) {
        return queryBoxAndChildCountByConfig(PackagingAssociationCustomEntity_.configEntityId, PackagingAssociationCustomEntity_.isBoxCode, configId);
    }

    public void deleteAllSonByPrentCode(String code) {
        deleteAllSonByPrentCode(PackagingAssociationCustomEntity_.parentOuterCodeId, code);
    }

    @Override
    public List<PackagingAssociationCustomEntity> querySonListByParentCode(String code, int count) {
        return querySonListByParentCode(PackagingAssociationCustomEntity_.parentOuterCodeId, code, PackagingAssociationCustomEntity_.isBoxCode, count);
    }


    /**
     * 修改码的父码
     */
    public void updateCodeParentCode(String boxCode, String codeTypeId, List<PackagingAssociationCustomEntity> list) {
        ArrayList<PackagingAssociationCustomEntity> tempList = new ArrayList<>();
        for (PackagingAssociationCustomEntity bean : list) {
            PackagingAssociationCustomEntity entity = queryEntityByCode(bean.getOuterCode());
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

    public List<PackagingAssociationCustomEntity> queryBoxCodeListByConfigId(long configId) {
        Query<PackagingAssociationCustomEntity> query = box.query()
                .equal(PackagingAssociationCustomEntity_.configEntityId, configId)
                .equal(PackagingAssociationCustomEntity_.isBoxCode, true)
                .build();
        return queryListByLimit(query, 0, 0);
    }


    @Override
    public List<PackagingAssociationCustomEntity> queryBoxListByConfigurationId(long configurationId) {
        return queryBoxListByConfigurationId(configurationId, 0, 0);
    }

    @Override
    public List<PackagingAssociationCustomEntity> queryBoxListByConfigurationId(long configurationId, long id, int pageSize) {
        return queryBoxListByConfigurationId(PackagingAssociationCustomEntity_.configEntityId, configurationId,
                PackagingAssociationCustomEntity_.id, id, PackagingAssociationCustomEntity_.isBoxCode, pageSize);
    }


    /**
     * 修改父码是否满箱的状态
     */
    public void updateParentCodeIsFull(String code, boolean isFull) {
        PackagingAssociationCustomEntity entity = queryEntityByCode(code);
        if (entity != null) {
            //更新子码时父码已被删除
            entity.setIsFull(isFull);
            putData(entity);
        }
    }

    /**
     * 查询当前包装时有多少父码
     */
    public int queryBoxCodeSize(Long configId) {
        return queryBoxCodeSize(PackagingAssociationCustomEntity_.configEntityId, configId, PackagingAssociationCustomEntity_.isBoxCode);
    }

    @Override
    public List<PackagingAssociationCustomEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return null;
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(PackagingAssociationCustomEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(PackagingAssociationCustomEntity_.configEntityId, configId);
    }

    /**
     * 查询父码
     */
    public List<String> queryParentCodeList() {
        return queryParentCodeList(PackagingAssociationCustomEntity_.isBoxCode, PackagingAssociationCustomEntity_.outerCode);
    }


    @Override
    public List<String> queryRealParentCodeList() {
        return queryRealParentCodeList(PackagingAssociationCustomEntity_.parentOuterCodeId);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(PackagingAssociationCustomEntity_.configEntity
                , PackageConfigEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }

    public boolean isRepeatBoxCode(String code) {
        return isRepeatBoxCode(PackagingAssociationCustomEntity_.outerCode, code, PackagingAssociationCustomEntity_.isBoxCode);
    }

    public void deleteCodeAndCheckParent(PackagingAssociationCustomEntity entity) {
        removeData(entity);
    }

    @Override
    public void clearEmptyBox() {
        clearEmptyBox(PackagingAssociationCustomEntity_.isBoxCode, PackagingAssociationCustomEntity_.parentOuterCodeId);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(PackagingAssociationCustomEntity_.configEntity);
    }

    @Override
    public List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        PackageConfigOperator configurationOperator = new PackageConfigOperator();
        return configurationOperator.queryAllDistinctConfigIdList(relationInfo);
    }
}
