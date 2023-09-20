package com.jgw.delingha.bean;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.List;

public class OrderStockInListBean {


    public List<ListBean> list;

    public static class ListBean {
        public String inHouseList;
        //审核状态:0 未审核，1 已审核 2，审核不通过, 3 待作废 ,4 已作废,5 作废不通过
        public int auditStatus;
        public String createTime;

        public String wareHouseName;
        public String wareHouseCode;
        public String supperName;
        public String supperCode;
        public int status;
        public int updateVersion;
        public String remarks;
        public String inTypeName;
        public String inHouseDateInterval;
        public String wareHouseInId;
        public String operatorName;

        public String getStatusDesc() {
            switch (status) {
                case 0:
                    return "待入库";
                case 1:
                    return "部分入库";
                case 2:
                    return "完成扫码";
                default:
                    return "异常";
            }
        }

        public int getStatusTextColor() {
            switch (auditStatus) {
                case 0:
                    return ResourcesUtils.getColor(R.color.dealing_color);
                case 1:
                    return ResourcesUtils.getColor(R.color.success_color);
                case 2:
                case 3:
                case 4:
                case 5:
                    return ResourcesUtils.getColor(R.color.error_color);
                default:
                    return ResourcesUtils.getColor(R.color.error_color);
            }
        }
    }
}
