package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.entity.GroupDisassembleEntity;
import com.jgw.delingha.sql.entity.GroupDisassembleEntity_;

import java.util.List;

public class GroupDisassembleOperator extends BaseDisassembleOperator<GroupDisassembleEntity> {

    @Override
    public GroupDisassembleEntity queryEntityByCode(String code) {
        return queryEntityByString(GroupDisassembleEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(GroupDisassembleEntity_.code, code);
    }

    @Override
    public void deleteAllByCurrentUser() {
        deleteAllByCurrentUser(GroupDisassembleEntity_.userEntityId);
    }

    @Nullable
    @Override
    public List<GroupDisassembleEntity> queryListByPage(int page, int pageSize) {
        return queryListByPage(GroupDisassembleEntity_.userEntityId, GroupDisassembleEntity_.id, page, pageSize);
    }

    @Override
    public long queryUnverifiedCount() {
        return queryCountByStatus(GroupDisassembleEntity_.userEntityId, GroupDisassembleEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public List<GroupDisassembleEntity> queryDataByCurrentId(Long currentId, int limit) {
        return queryGroupDataByUserId(GroupDisassembleEntity_.userEntityId, GroupDisassembleEntity_.id, currentId, limit);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(GroupDisassembleEntity_.userEntityId);
    }

    @Nullable
    @Override
    public List<GroupDisassembleEntity> queryListByCount(int count) {
        return queryListByCount(GroupDisassembleEntity_.userEntityId, GroupDisassembleEntity_.id, count);
    }

}
