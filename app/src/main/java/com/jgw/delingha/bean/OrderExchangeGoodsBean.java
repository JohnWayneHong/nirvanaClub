package com.jgw.delingha.bean;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderExchangeGoodsBean {

    public List<ListBean> list;

    public static class ListBean{
        public String houseList;
        public String wareGoodsOutId;
        public String wareGoodsOutCode;
        public String wareGoodsOutName;
        public String wareGoodsInId;
        public String wareGoodsInCode;
        public String wareGoodsInName;
        public String operationType;
        public String remark;
        public String createTime;
        public String operationName;
    }
}
