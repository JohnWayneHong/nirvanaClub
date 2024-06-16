package com.ggb.nirvanahappyclub.sql.operator;

import com.ggb.nirvanahappyclub.sql.BaseOperator;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordEntity;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity;

import java.util.List;


/**
 * author : Cxz
 * data : 2020/1/15
 * description : 用户数据库操作类
 */
public class BasicPhraseOperator extends BaseOperator<BasicWordPhraseEntity> {

    public List<BasicWordPhraseEntity> queryAllBasicWordPhraseEntity() {
        List<BasicWordPhraseEntity> resultList = queryAll();
        return resultList;
    }
}
