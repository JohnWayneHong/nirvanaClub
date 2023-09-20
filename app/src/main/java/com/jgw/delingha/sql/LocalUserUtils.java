package com.jgw.delingha.sql;

import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.sql.entity.UserEntity;
import com.jgw.delingha.sql.operator.UserOperator;
import com.jgw.delingha.utils.ConstantUtil;

public class LocalUserUtils {
    private static UserEntity userEntity;

    public static UserEntity getCurrentUserEntity() {
        long id = LocalUserUtils.getCurrentUserId();
        if (userEntity != null && userEntity.getId() == id) {
            return userEntity;
        }
        userEntity = new UserOperator().queryDataById(id);
        return userEntity;
    }
    public static long getCurrentUserId(){
        return MMKVUtils.getInt(ConstantUtil.USER_ENTITY_ID);
    }
}
