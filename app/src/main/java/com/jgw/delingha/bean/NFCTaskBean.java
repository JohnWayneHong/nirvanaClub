package com.jgw.delingha.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : J-T
 * @date : 2022/6/9 15:04
 * description :
 */
public class NFCTaskBean implements Serializable {

    /**
     * list : [{"extendCode":"","extendCodeType":0,"organizationId":"","organizationName":"","sequenceCode":"","userId":"","userName":""}]
     * taskNumber :
     */

    public String taskNumber;
    public List<ListBean> list = new ArrayList<>();

    public static class ListBean implements Serializable {
        /**
         * extendCode :
         * extendCodeType : 0
         * organizationId :
         * organizationName :
         * sequenceCode :
         * userId :
         * userName :
         */

        /**
         * NFC码
         */
        public String extendCode;
        /**
         * 扩展码类型，50-NFC,51-其他
         */
        public int extendCodeType = 50;
        public String organizationId;
        public String organizationName;
        /**
         * 顺序码
         */
        public String sequenceCode;
        public String userId;
        public String userName;
    }
}
