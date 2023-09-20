package com.jgw.delingha.bean;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

public class CommonOrderStockOutListBean {
        public String houseList;
        public String wareHouseOutId;

        public int status;
        public String createTime;
        public String updateTime;

        public String warehouse;
        public String receiveOrganizationName;
        public String receiveOrganizationCode;
        public String remark;
        public String id;

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
