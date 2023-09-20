package com.jgw.delingha.sql.entity;

/**
 * @author : J-T
 * @date : 2022/2/22 13:34
 * description :
 */
public abstract class BasePackageCodeEntity extends BaseCodeEntity {
    public abstract String getParentCode();

    public abstract Boolean getIsBoxCode();
}
