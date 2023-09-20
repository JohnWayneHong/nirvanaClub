package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity_;
import com.jgw.delingha.sql.entity.PackagingAssociationEntity;
import com.jgw.delingha.sql.entity.PackagingAssociationEntity_;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.RelationInfo;

public class PackagingAssociationOperator extends BasePackageCodeOperator<PackagingAssociationEntity> {
    @Override
    public PackagingAssociationEntity queryEntityByCode(String code) {
        return queryEntityByString(PackagingAssociationEntity_.outerCode, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(PackagingAssociationEntity_.outerCode, code);
    }

    @Override
    public List<PackagingAssociationEntity> querySonListByBoxCode(String code) {
        return querySonListByBoxCode(code, 0, 0);
    }

    @Override
    public List<PackagingAssociationEntity> querySonListByBoxCode(String code, int offset, int count) {
        return querySonListByBoxCode(PackagingAssociationEntity_.parentOuterCodeId, code, PackagingAssociationEntity_.id, offset, count);
    }

    @Override
    public PackagingAssociationEntity findBox(long configId, long boxId) {
        return findBox(PackagingAssociationEntity_.configEntityId, configId,
                PackagingAssociationEntity_.isBoxCode, PackagingAssociationEntity_.id,
                PackagingAssociationEntity_.id, boxId);
    }

    @Override
    public PackagingAssociationEntity findLastBox(long configId) {
        return findLastBox(PackagingAssociationEntity_.configEntityId, configId,
                PackagingAssociationEntity_.isBoxCode, PackagingAssociationEntity_.id);
    }

    @Override
    public Map<String, Integer> queryBoxAndChildCountByConfig(long configId) {
        return queryBoxAndChildCountByConfig(PackagingAssociationEntity_.configEntityId, PackagingAssociationEntity_.isBoxCode, configId);
    }

    /**
     * 删除父码下的所有子码
     *
     * @param code 父码
     */
    public void deleteAllSonByPrentCode(String code) {
        deleteAllSonByPrentCode(PackagingAssociationEntity_.parentOuterCodeId, code);
    }

    @Override
    public List<PackagingAssociationEntity> querySonListByParentCode(String code, int count) {
        return querySonListByParentCode(PackagingAssociationEntity_.parentOuterCodeId, code, PackagingAssociationEntity_.isBoxCode, count);
    }

    /**
     * 修改码的父码
     */
    public void updateCodeParentCode(String boxCode, String codeTypeId, List<PackagingAssociationEntity> list) {
        ArrayList<PackagingAssociationEntity> tempList = new ArrayList<>();
        for (PackagingAssociationEntity bean : list) {
            PackagingAssociationEntity entity = queryEntityByCode(bean.getOuterCode());
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
    public List<PackagingAssociationEntity> queryBoxListByConfigurationId(long configurationId) {
        return queryBoxListByConfigurationId(configurationId, 0, 0);
    }

    @Override
    public List<PackagingAssociationEntity> queryBoxListByConfigurationId(long configurationId, long id, int pageSize) {
        return queryBoxListByConfigurationId(PackagingAssociationEntity_.configEntityId, configurationId,
                PackagingAssociationEntity_.id, id, PackagingAssociationEntity_.isBoxCode, pageSize);
    }

    /**
     * 修改父码是否满箱的状态
     */
    public void updateParentCodeIsFull(String code, boolean isFull) {
        PackagingAssociationEntity entity = queryEntityByCode(code);
        if (entity != null) {
            //更新子码时父码已被删除
            entity.setIsFull(isFull);
            putData(entity);
        }
    }

    public int queryBoxCodeSize(Long configId) {
        return queryBoxCodeSize(PackagingAssociationEntity_.configEntityId, configId, PackagingAssociationEntity_.isBoxCode);
    }

    @Override
    public List<PackagingAssociationEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return null;
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(PackagingAssociationEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(PackagingAssociationEntity_.configEntityId, configId);
    }

    public List<String> queryParentCodeList() {
        return queryParentCodeList(PackagingAssociationEntity_.isBoxCode, PackagingAssociationEntity_.outerCode);
    }

    public List<String> queryRealParentCodeList() {
        return queryRealParentCodeList(PackagingAssociationEntity_.parentOuterCodeId);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(PackagingAssociationEntity_.configEntity
                , PackageConfigEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
    public long queryCountByType(int type) {
        QueryBuilder<PackagingAssociationEntity> qb = box.query();
        qb.link(PackagingAssociationEntity_.configEntity)
                .apply(PackageConfigEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
        qb.link(PackagingAssociationEntity_.configEntity)
                .apply(PackageConfigEntity_.packageSceneType.equal(type));
        Query<PackagingAssociationEntity> query = qb.build();
        return queryCountByQB(query);
    }

    public boolean isRepeatBoxCode(String code) {
        return isRepeatBoxCode(PackagingAssociationEntity_.outerCode, code, PackagingAssociationEntity_.isBoxCode);
    }

    public void deleteCodeAndCheckParent(PackagingAssociationEntity entity) {
        removeData(entity);
    }

    @Override
    public void clearEmptyBox() {
        clearEmptyBox(PackagingAssociationEntity_.isBoxCode, PackagingAssociationEntity_.parentOuterCodeId);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(PackagingAssociationEntity_.configEntity);
    }

    @Override
    public List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        PackageConfigOperator configurationOperator = new PackageConfigOperator();
        return configurationOperator.queryAllDistinctConfigIdList(relationInfo);
    }
}
