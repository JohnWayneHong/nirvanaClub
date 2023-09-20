package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.List;

public class OrderStockOutListBean {


    public List<ListBean> list;

    public static class ListBean {
        public String outHouseList;
        public String wareHouseOutId;

        public int status;
        public String createTime;

        public String wareHouseName;
        public String wareHouseCode;
        public String receiveOrganizationName;
        public String receiveOrganizationCode;
        public String warehouse;
        public String logisticsCompanyName;
        public String logisticsNumber;
        public String operatorName;
        public String orderNo;
        public String linkName;
        public String linkPhoneNumber;
        public String outTypeName;
        public String provinceName;
        public String cityName;
        public String countyName;
        public String detailedAddress;
        public String remark;

        public String getStatusDesc() {
            switch (status) {
                case 0:
                    return "待发货";
                case 1:
                    return "部分发货";
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

        public String getCustomerText() {
            if (!TextUtils.isEmpty(receiveOrganizationCode) && !TextUtils.isEmpty(receiveOrganizationName)) {
                return receiveOrganizationName + "(" + receiveOrganizationCode + ")";
            } else if (!TextUtils.isEmpty(receiveOrganizationName)) {
                return receiveOrganizationName;
            } else {
                return null;
            }
        }

        public String getLinkText() {
            if (TextUtils.isEmpty(linkName)) {
                return "";
            }
            return linkName + "(" + linkPhoneNumber + ")";
        }

        public String getLocationText() {
            if (TextUtils.isEmpty(provinceName)) {
                return "";
            }
            return provinceName + cityName + countyName;
        }

    }
}
