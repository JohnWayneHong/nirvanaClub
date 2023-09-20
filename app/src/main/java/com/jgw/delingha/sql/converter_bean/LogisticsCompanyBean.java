package com.jgw.delingha.sql.converter_bean;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2019/11/26
 * converter_bean仅用来从网络获取转存数据库时使用
 */
public class LogisticsCompanyBean {

    /**
     * list : [{"id":1,"shipperCode":"SF","shipperName":"顺丰速运"},{"id":2,"shipperCode":"HTKY","shipperName":"百世快递"},{"id":3,"shipperCode":"ZTO","shipperName":"中通快递"},{"id":4,"shipperCode":"STO","shipperName":"申通快递"},{"id":5,"shipperCode":"YTO","shipperName":"圆通速递"},{"id":6,"shipperCode":"YD","shipperName":"韵达速递"},{"id":7,"shipperCode":"YZPY","shipperName":"邮政快递包裹"},{"id":8,"shipperCode":"EMS","shipperName":"EMS"},{"id":9,"shipperCode":"HHTT","shipperName":"天天快递"},{"id":10,"shipperCode":"JD","shipperName":"京东快递"},{"id":11,"shipperCode":"UC","shipperName":"优速快递"},{"id":12,"shipperCode":"DBL","shipperName":"德邦快递"},{"id":13,"shipperCode":"ZJS","shipperName":"宅急送"},{"id":14,"shipperCode":"ANE","shipperName":"安能物流"},{"id":15,"shipperCode":"ANEKY","shipperName":"安能快运"},{"id":16,"shipperCode":"BTWL","shipperName":"百世快运"},{"id":17,"shipperCode":"CND","shipperName":"承诺达"},{"id":18,"shipperCode":"DBLKY","shipperName":"德邦快运"},{"id":19,"shipperCode":"HOAU","shipperName":"天地华宇"},{"id":20,"shipperCode":"JDKY","shipperName":"京东快运"}]
     * pagination : {"current":1,"pageSize":20,"startNumber":0,"total":35,"totalPage":2}
     */

    public PaginationBean pagination;
    public List<ListBean> list;

    public static class PaginationBean {
        /**
         * current : 1
         * pageSize : 20
         * startNumber : 0
         * total : 35
         * totalPage : 2
         */

        public int current;
        public int pageSize;
        public int startNumber;
        public int total;
        public int totalPage;
    }

    public static class ListBean {
        /**
         * id : 1
         * shipperCode : SF
         * shipperName : 顺丰速运
         */

        public int id;
        public String shipperCode;
        public String shipperName;
    }
}
