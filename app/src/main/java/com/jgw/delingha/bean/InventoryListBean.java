package com.jgw.delingha.bean;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.List;

public class InventoryListBean {


    public List<ListBean> list;

    public static class ListBean{
        public String houseList;
        public String wareHouseName;
        public String wareHouseCode;
        //value = "0-未开始，1-正在盘点，2-已取消，3-已完成"
        public int status;
        public String remarks;
        public String inventoryReason;
        public String createTime;
        public String operatorName;
        public String getStatusDesc(){
            switch (status) {
                case 0:
                    return "未开始";
                case 1:
                    return "正在盘点";
                case 2:
                    return "已取消";
                case 3:
                    return "已完成";
                default:
                    return "异常";
            }
        }

        public int getStatusTextColor() {
            switch (status) {
                case 1:
                    return ResourcesUtils.getColor(R.color.dealing_color);
                case 2:
                    return ResourcesUtils.getColor(R.color.success_color);
                case 3:
                    return ResourcesUtils.getColor(R.color.error_color);
                default:
                    return ResourcesUtils.getColor(R.color.dealing_color);
            }
        }
    }
}
