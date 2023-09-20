package com.jgw.delingha.bean;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.List;

public class GBOrderStockOutListBean {


    public List<ListBean> list;

    public static class ListBean {
        public String outHouseList;
        public String wareHouseOutId;
        //审核状态:0 未审核，1 已审核 2，审核不通过, 3 待作废 ,4 已作废,5 作废不通过
        public int auditStatus;
        public String createTime;

        public String receiveOrganizationCode;
        public String receiveOrganizationName;
        public int updateVersion;
        public String orderId;

        public String getStatusDesc() {
            switch (auditStatus) {
                case 0:
                    return "未审核";
                case 1:
                    return "已审核";
                case 2:
                    return "审核不通过";
                case 3:
                    return "待作废";
                case 4:
                    return "已作废";
                case 5:
                    return "作废不通过";
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
