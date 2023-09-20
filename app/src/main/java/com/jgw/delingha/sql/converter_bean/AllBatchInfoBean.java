package com.jgw.delingha.sql.converter_bean;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/3/5 获取所有批次
 * converter_bean仅用来从网络获取转存数据库时使用
 */
public class AllBatchInfoBean {

    /**
     * list : [{"objectUniqueValue":"73380fb777944f85bc48a4c05ff56d75","field":"批次0305","objectMap":{"batchName":"批次0305","organizationName":"天门天升畜禽有限责任公司","productId":"fb68727790fe4529a4c855842080e1d8","founder":"零六","batchCode":"20200305CP000068000001","updateTime":1583373483000,"batchId":"73380fb777944f85bc48a4c05ff56d75","productName":"0305产品","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"73380fb777944f85bc48a4c05ff56d75","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1583373483000,"id":9341,"productUrl":""}},{"objectUniqueValue":"2e58a5baf4b940cabe8d9a00c4b73ae5","field":"批次0303","objectMap":{"batchName":"批次0303","organizationName":"天门天升畜禽有限责任公司","productId":"710d7ebad47149289c9981d06a3c6eb1","founder":"零六","batchCode":"20200303CP000055000001","updateTime":1583223652000,"batchId":"2e58a5baf4b940cabe8d9a00c4b73ae5","productName":"产品0303","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"2e58a5baf4b940cabe8d9a00c4b73ae5","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1583223652000,"id":9336,"productUrl":""}},{"objectUniqueValue":"50dbcec515ab4893819f957c1baf86d5","field":"122","objectMap":{"batchName":"122","organizationName":"天门天升畜禽有限责任公司","productId":"f1625883aba441df9e68d239414c5edd","founder":"零六","batchCode":"12","updateTime":1583143914000,"batchId":"50dbcec515ab4893819f957c1baf86d5","productName":"测试产品2006","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"50dbcec515ab4893819f957c1baf86d5","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1583143914000,"marketDate":1582560000000,"id":9330,"productUrl":""}},{"objectUniqueValue":"b2ff60396f8146de931240793e336e1e","field":"批次0108","objectMap":{"batchName":"批次0108","organizationName":"天门天升畜禽有限责任公司","productId":"85ddfaed74bc4e2593eaa316531fc592","founder":"零六","batchCode":"20200108CP000081000001","updateTime":1578454656000,"batchId":"b2ff60396f8146de931240793e336e1e","productName":"0108产品","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"b2ff60396f8146de931240793e336e1e","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1578454656000,"id":7692,"productUrl":""}},{"objectUniqueValue":"70994a9ff85745b9a69394750d1b0b43","field":"06批次1","objectMap":{"batchName":"06批次1","organizationName":"天门天升畜禽有限责任公司","productId":"e35c4d32cc91418c89fa2ea06f4ce932","founder":"零六","batchCode":"20200106CP000079000001","updateTime":1578273786000,"batchId":"70994a9ff85745b9a69394750d1b0b43","productName":"0106产品1","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"70994a9ff85745b9a69394750d1b0b43","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1578273786000,"id":7684,"productUrl":""}},{"objectUniqueValue":"a5f4491eb81f48b4911bea3e4d2f27e2","field":"1230产品批次","objectMap":{"batchName":"1230产品批次","organizationName":"天门天升畜禽有限责任公司","productId":"68bb64e331bf44779b378406f6ccb059","founder":"零六","batchCode":"20191230CP000075000001","updateTime":1577699202000,"batchId":"a5f4491eb81f48b4911bea3e4d2f27e2","productName":"1230产品","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"a5f4491eb81f48b4911bea3e4d2f27e2","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1577699202000,"marketDate":1575216000000,"id":7520,"productUrl":""}},{"objectUniqueValue":"9f4646d4abe84bd8a36d3fb9d448864d","field":"三级商品（垛，组，瓶）_舟山渔场_20191225","objectMap":{"batchName":"三级商品（垛，组，瓶）_舟山渔场_20191225","organizationName":"天门天升畜禽有限责任公司","productId":"8f334c077901452b8c4cd86520f58d94","founder":"125","batchCode":"HNRDZZPC_20191225174441715","updateTime":1577267081000,"batchId":"9f4646d4abe84bd8a36d3fb9d448864d","productName":"三级商品（垛，组，瓶）","founderId":"a782b6b7c59c49e4bb9b88fe24b9c25a","traceBatchInfoId":"9f4646d4abe84bd8a36d3fb9d448864d","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1577267081000,"id":3965,"productUrl":""}},{"objectUniqueValue":"2e83cf59a10b441081b48ad368a784bd","field":"三级商品（垛，组，瓶）_D09_20191225","objectMap":{"batchName":"三级商品（垛，组，瓶）_D09_20191225","organizationName":"天门天升畜禽有限责任公司","productId":"8f334c077901452b8c4cd86520f58d94","founder":"125","batchCode":"HNRDZZPC_20191225174423194","updateTime":1577267063000,"batchId":"2e83cf59a10b441081b48ad368a784bd","productName":"三级商品（垛，组，瓶）","founderId":"a782b6b7c59c49e4bb9b88fe24b9c25a","traceBatchInfoId":"2e83cf59a10b441081b48ad368a784bd","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1577267063000,"id":3964,"productUrl":""}},{"objectUniqueValue":"f10584c28f564283a6e19fdf8b721317","field":"1205","objectMap":{"batchName":"1205","organizationName":"天门天升畜禽有限责任公司","productId":"3bdf20078bea437bb8104fed7fe0ede6","founder":"零六","batchCode":"20191205CP000070000001","updateTime":1575524719000,"batchId":"f10584c28f564283a6e19fdf8b721317","productName":"模板2西瓜","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"f10584c28f564283a6e19fdf8b721317","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1575524719000,"id":1974,"productUrl":"e7fb4992d01341a592c6440a302db1f5"}},{"objectUniqueValue":"bcf2a4b5f45a4e1eb520a2b25ae746b1","field":"12","objectMap":{"batchName":"12","organizationName":"天门天升畜禽有限责任公司","productId":"3bdf20078bea437bb8104fed7fe0ede6","founder":"零六","batchCode":"20191204CP000070000001","updateTime":1575448819000,"batchId":"bcf2a4b5f45a4e1eb520a2b25ae746b1","productName":"模板2西瓜","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"bcf2a4b5f45a4e1eb520a2b25ae746b1","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1575448819000,"marketDate":1575302400000,"id":1911,"productUrl":"e7fb4992d01341a592c6440a302db1f5"}}]
     * pagination : {"startNumber":0,"current":1,"totalPage":11,"pageSize":10,"total":104}
     */

    public List<ListBean> list;

    public static class ListBean {
        /**
         * objectUniqueValue : 73380fb777944f85bc48a4c05ff56d75
         * field : 批次0305
         * objectMap : {"batchName":"批次0305","organizationName":"天门天升畜禽有限责任公司","productId":"fb68727790fe4529a4c855842080e1d8","founder":"零六","batchCode":"20200305CP000068000001","updateTime":1583373483000,"batchId":"73380fb777944f85bc48a4c05ff56d75","productName":"0305产品","founderId":"c227b286b54e4b8f986e52a78e75e50c","traceBatchInfoId":"73380fb777944f85bc48a4c05ff56d75","organizationId":"18355c8780094ca1b786464fb652f471","createTime":1583373483000,"id":9341,"productUrl":""}
         */

        public String objectUniqueValue;
        public String field;
        public ObjectMapBean objectMap;

        public static class ObjectMapBean {
            /**
             * batchName : 批次0305
             * organizationName : 天门天升畜禽有限责任公司
             * productId : fb68727790fe4529a4c855842080e1d8
             * founder : 零六
             * batchCode : 20200305CP000068000001
             * updateTime : 1583373483000
             * batchId : 73380fb777944f85bc48a4c05ff56d75
             * productName : 0305产品
             * founderId : c227b286b54e4b8f986e52a78e75e50c
             * traceBatchInfoId : 73380fb777944f85bc48a4c05ff56d75
             * organizationId : 18355c8780094ca1b786464fb652f471
             * createTime : 1583373483000
             * id : 9341
             * productUrl :
             */

            public String batchName;
            public String organizationName;
            public String productId;
            public String founder;
            public String batchCode;
            public long updateTime;
            public String batchId;
            public String productName;
            public String founderId;
            public String traceBatchInfoId;
            public String organizationId;
            public long createTime;
            public int id;
            public String productUrl;
        }
    }
}
