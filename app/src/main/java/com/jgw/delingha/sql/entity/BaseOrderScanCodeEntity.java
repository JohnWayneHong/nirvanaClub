package com.jgw.delingha.sql.entity;

import io.objectbox.relation.ToOne;

/**
 * 订单模块码表基类
 * Created by xswwg
 * on 2021/3/15
 */
public abstract class BaseOrderScanCodeEntity extends BaseCodeEntity {

    /**
     * 订单码跟随订单商品 订单商品跟随订单 订单跟随用户 码不直接保存用户信息
     */
    @Override
    public ToOne<UserEntity> getUserEntity() {
        return null;
    }
}
