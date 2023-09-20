package com.jgw.delingha.sql.operator;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity_;

import java.util.List;

import io.objectbox.query.QueryBuilder;

/**
 * author : xsw
 * data : 2020/3/2
 * description : 仓库信息操作类
 */
public class StorePlaceOperator extends BaseOperator<StorePlaceEntity> {


    /**
     * 通过关键字查询仓库列表
     *
     * @param searchStr 关键字 仓库名
     * @return 仓库列表 查询异常时结果为null 无数据时为空集合
     */
    @Nullable
    public List<StorePlaceEntity> queryDataBySearchStr(@NonNull String searchStr, @NonNull String wareHouseId, int page, int page_size) {
        QueryBuilder<StorePlaceEntity> query = box.query();
        if (!TextUtils.isEmpty(searchStr)) {
            query.contains(StorePlaceEntity_.storeHouseCode, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .or()
                    .contains(StorePlaceEntity_.storeHouseName, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        query.equal(StorePlaceEntity_.wareHouseId, wareHouseId, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        return queryListByPage(query.build(), page, page_size);
    }
}
