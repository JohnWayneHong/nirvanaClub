package com.jgw.delingha.bean;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.ArrayList;
import java.util.List;

public class GBOrderExchangeWarehouseDetailsBean {

    public String createTime;
    public String houseList;
    public int houseType;
    public String inStoreHouseId;
    public String inStoreHouseName;
    public String inWareHouseCode;
    public String inWareHouseId;
    public String inWareHouseName;
    public String operationType;
    public String operatorName;
    public String outStoreHouseId;
    public String outStoreHouseName;
    public String outWareHouseCode;
    public String outWareHouseId;
    public String outWareHouseName;
    public int status;
    public String updateTime;
    public String wareHouseOutId;
    public String orderId;
    public List<ListBean> products;
    public int updateVersion;

    public static class ListBean implements Comparable<ListBean>{


        public int actualSingleCodeNumber;
        public int firstNumber;
        public String firstNumberUnitCode;
        public String firstNumberUnitName;
        public int planFirstNumber;
        public int planSecondNumber;
        public int planThirdNumber;
        public String productCode;
        public String productId;
        public String productName;
        public String productBatch;
        public String productBatchId;
        public int secondNumber;
        public String secondNumberUnitCode;
        public String secondNumberUnitName;
        public int singleCodeNumber;
        public int thirdNumber;
        public String thirdNumberUnitCode;
        public String thirdNumberUnitName;
        public String wareHouseOutId;//产品调仓仓库流水信息表id
        public String wareHouseOutProductId;
        public String outWareHouseId;//调出仓库id
        public String inWareHouseId;//调出仓库id
        public int reviewStatus;


        public List<OrderStockScanBean> codeList = new ArrayList<>();

        public String getSingleCodeNumberText() {
            return this.singleCodeNumber + "";
        }
        //实际扫码数量
        public String getSingleScanCodeNumberText() {
            return this.actualSingleCodeNumber + "";
        }


        public String getCurrentNumber() {
            ArrayList<String> allUnit = new ArrayList<>();

            int first = 0;
            int second = 0;
            int third = 0;
            for (OrderStockScanBean b : codeList) {
                first += b.firstLevel;
                second += b.secondLevel;
                third += b.thirdLevel;
            }
            if (!TextUtils.isEmpty(thirdNumberUnitName)) {
                allUnit.add(third + thirdNumber + thirdNumberUnitName);
            }
            if (!TextUtils.isEmpty(secondNumberUnitName)) {
                allUnit.add(second + secondNumber + secondNumberUnitName);
            }
            allUnit.add(first + firstNumber + firstNumberUnitName);

            return getFilterString(allUnit);
        }

        public String getFilterString(List<String> allUnit) {
            StringBuilder sb = new StringBuilder();

            ArrayList<String> temp = new ArrayList<>();
            for (int i = 0; i < allUnit.size(); i++) {
                String s = allUnit.get(i);
                if (!TextUtils.equals("0", s)) {
                    temp.add(s);
                }
            }
            for (int i = 0; i < temp.size(); i++) {
                String s = temp.get(i);
                sb.append(s);
                if (i != temp.size() - 1) {
                    sb.append("、");
                }
            }
            return sb.toString();
        }
        public String getReviewStatusText() {
            return reviewStatus == 2 ? "复核通过" : "确认通过";
        }

        public Drawable _getReviewBackground() {
            int id = reviewStatus == 2 ? R.drawable.radius96_gray : R.drawable.radius96_blue;
            return ResourcesUtils.getDrawable(id);
        }


        public boolean getReviewEnable() {
            return reviewStatus != 2;
        }

        @Override
        public int compareTo(ListBean o) {
            return reviewStatus-o.reviewStatus;
        }
    }
}
