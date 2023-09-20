package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.entity.SingleDisassembleEntity;
import com.jgw.delingha.sql.entity.SingleDisassembleEntity_;

import java.util.List;

public class SingleDisassembleOperator extends BaseDisassembleOperator<SingleDisassembleEntity> {

    @Override
    public SingleDisassembleEntity queryEntityByCode(String code) {
        return queryEntityByString(SingleDisassembleEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(SingleDisassembleEntity_.code, code);
    }

    @Override
    public void deleteAllByCurrentUser() {
        deleteAllByCurrentUser(SingleDisassembleEntity_.userEntityId);
    }

    @Nullable
    @Override
    public List<SingleDisassembleEntity> queryListByPage(int page, int pageSize) {
        return queryListByPage(SingleDisassembleEntity_.userEntityId, SingleDisassembleEntity_.id, page, pageSize);
    }

    @Override
    public long queryUnverifiedCount() {
        return queryCountByStatus(SingleDisassembleEntity_.userEntityId, SingleDisassembleEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public List<SingleDisassembleEntity> queryDataByCurrentId(Long currentId, int limit) {
        return queryGroupDataByUserId(SingleDisassembleEntity_.userEntityId, SingleDisassembleEntity_.id, currentId, limit);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(SingleDisassembleEntity_.userEntityId);
    }

    @Nullable
    @Override
    public List<SingleDisassembleEntity> queryListByCount(int count) {
        return queryListByCount(SingleDisassembleEntity_.userEntityId, SingleDisassembleEntity_.id, count);
    }


}
