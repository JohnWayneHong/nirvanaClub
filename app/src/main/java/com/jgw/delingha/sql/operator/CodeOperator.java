package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.CodeEntity;
import com.jgw.delingha.sql.entity.CodeEntity_;

/**
 * author : Cxz
 * data : 2020/1/19
 * description :
 */
public class CodeOperator extends BaseCodeOperator<CodeEntity> {

    @Nullable
    @Override
    public CodeEntity queryEntityByCode(String code) {
        return queryEntityByString(CodeEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(CodeEntity_.code, code);
    }
}
