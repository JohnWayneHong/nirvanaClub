package com.jgw.delingha.sql.operator;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity_;

import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * author : xsw
 * data : 2020/3/3
 * description : 快递公司操作类
 */
public class LogisticsCompanyOperator extends BaseOperator<LogisticsCompanyEntity> {

    /**
     * 通过关键字查询仓库列表
     *
     * @param searchStr 关键字 仓库名
     * @return 仓库列表 查询异常时结果为null 无数据时为空集合
     */
    @Nullable
    public List<LogisticsCompanyEntity> queryDataBySearchStr(String searchStr, int page, int pageSize) {
        QueryBuilder<LogisticsCompanyEntity> qb = box.query();
        if (!TextUtils.isEmpty(searchStr)) {
            qb.contains(LogisticsCompanyEntity_.shipperName, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                    .contains(LogisticsCompanyEntity_.shipperCode, searchStr, QueryBuilder.StringOrder.CASE_INSENSITIVE);
        }
        Query<LogisticsCompanyEntity> query = qb.build();
        return queryListByPage(query, page, pageSize);
    }
}
