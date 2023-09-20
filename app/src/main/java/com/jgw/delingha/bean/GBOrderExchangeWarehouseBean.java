package com.jgw.delingha.bean;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.List;

public class GBOrderExchangeWarehouseBean {

    public List<ListBean> list;

    public static class ListBean {
        public String createTime;
        public String houseList;
        public String inStoreHouseName;
        public String inWareHouseName;
        public String inWareHouseCode;
        public String operationType;
        public String operatorName;
        public String outStoreHouseName;
        public String outWareHouseName;
        public String outWareHouseCode;
        public int status;
        public String updateTime;
        public String wareHouseOutId;
        public int updateVersion;
        public String orderId;

        public String getStatusDesc(){
            switch (status) {
                case 0:
                    return "待扫码";
                case 1:
                    return "部分扫码";
                case 2:
                    return "完成扫码";
                default:
                    return "异常";
            }
        }

        public int getStatusTextColor() {
            switch (status) {
                case 0:
                case 1:
                    return ResourcesUtils.getColor(R.color.dealing_color);
                case 2:
                    return ResourcesUtils.getColor(R.color.success_color);
                default:
                    return ResourcesUtils.getColor(R.color.error_color);
            }
        }
    }
}
