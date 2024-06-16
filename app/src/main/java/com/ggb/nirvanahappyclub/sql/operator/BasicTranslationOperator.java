package com.ggb.nirvanahappyclub.sql.operator;

import com.ggb.nirvanahappyclub.sql.BaseOperator;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity;

import java.util.List;


/**
 * author : Cxz
 * data : 2020/1/15
 * description : 用户数据库操作类
 */
public class BasicTranslationOperator extends BaseOperator<BasicWordTranslationEntity> {

    public List<BasicWordTranslationEntity> queryAllBasicWordTranslationEntity() {
        List<BasicWordTranslationEntity> resultList = queryAll();
        return resultList;
    }
}
