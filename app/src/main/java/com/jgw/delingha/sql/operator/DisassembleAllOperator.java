package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.entity.DisassembleAllEntity;
import com.jgw.delingha.sql.entity.DisassembleAllEntity_;

import java.util.List;

public class DisassembleAllOperator extends BaseNoConfigCodeOperator<DisassembleAllEntity> {

    @Override
    public DisassembleAllEntity queryEntityByCode(String code) {
        return queryEntityByString(DisassembleAllEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(DisassembleAllEntity_.code, code);
    }


    @Override
    public void deleteAllByCurrentUser() {
        deleteAllByCurrentUser(DisassembleAllEntity_.userEntityId);
    }


    @Nullable
    @Override
    public List<DisassembleAllEntity> queryListByPage(int page, int pageSize) {
        return queryListByPage(DisassembleAllEntity_.userEntityId, DisassembleAllEntity_.id, page, pageSize);
    }

    @Nullable
    @Override
    public List<DisassembleAllEntity> queryListByCount(int count) {
        return queryListByCount(DisassembleAllEntity_.userEntityId, DisassembleAllEntity_.id, count);
    }

    @Nullable
    public List<DisassembleAllEntity> queryListByPageV2(Long currentId, int page, int pageSize) {
        return queryGroupDataByUserId(DisassembleAllEntity_.userEntityId, DisassembleAllEntity_.id, currentId, page, pageSize);
    }

    public long queryVerifyingCount() {
        return queryCountByStatus(DisassembleAllEntity_.userEntityId, DisassembleAllEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(DisassembleAllEntity_.userEntityId);
    }
}
