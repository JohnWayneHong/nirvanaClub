package com.jgw.delingha.sql.operator;

import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.sql.entity.BaseCodeEntity;

/**
 * Created by XiongShaoWu
 * on 2020/3/16 通过码操作的数据库操作类
 */
public abstract class BaseCodeOperator<T extends BaseCodeEntity> extends BaseOperator<T> {


    public abstract T queryEntityByCode(String code);

    public abstract long removeEntityByCode(String code);


    public final void updateCodeStatusByCode(String code, int status) {
        T entity = queryEntityByCode(code);
        if (entity == null) {
            ToastUtils.showToast("更新码状态失败!");
            return;
        }
        entity.setCodeStatus(status);
        putData(entity);
    }
}
