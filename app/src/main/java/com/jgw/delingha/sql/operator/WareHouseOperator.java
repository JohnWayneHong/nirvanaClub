package com.jgw.delingha.sql.operator;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity_;

import java.util.List;

import io.objectbox.query.QueryBuilder;

/**
 * author : xsw
 * data : 2020/3/2
 * description : 仓库信息操作类
 */
public class WareHouseOperator extends BaseOperator<WareHouseEntity> {

    @Nullable
    public List<WareHouseEntity> queryDataBySearchStr(String searchStr, int page, int page_size) {
        QueryBuilder<WareHouseEntity> query = box.query();
        if (!TextUtils.isEmpty(searchStr)) {
            query.contains(WareHouseEntity_.wareHouseCode, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(WareHouseEntity_.wareHouseName, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        return queryListByPage(query.build(), page, page_size);
    }

}
