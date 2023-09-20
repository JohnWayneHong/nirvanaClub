package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.BaseCodeEntity;

import java.util.List;

public abstract class BaseDisassembleOperator<T extends BaseCodeEntity> extends BaseNoConfigCodeOperator<T> {

    public abstract long queryUnverifiedCount();

    public abstract List<T> queryDataByCurrentId(Long currentId, int limit);
}
