package com.ggb.nirvanahappyclub.sql.operator;

import com.ggb.common_library.utils.LogUtils;
import com.ggb.nirvanahappyclub.sql.BaseOperator;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordEntity;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordEntity_;
import com.ggb.nirvanahappyclub.sql.entity.UserEntity;


import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;


/**
 * author : Cxz
 * data : 2020/1/15
 * description : 用户数据库操作类
 */
public class BasicWordOperator extends BaseOperator<BasicWordEntity> {

    public List<BasicWordEntity> queryBasicWordData(String word) {
        Query<BasicWordEntity> query = box.query()
                .equal(BasicWordEntity_.word, word, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .build();

        return queryListByPage(query, 0, 0);
//        BasicWordEntity basicWordEntity = queryUniqueDataByQB(query);
//        return basicWordEntity != null ? basicWordEntity : new BasicWordEntity();
    }
}
