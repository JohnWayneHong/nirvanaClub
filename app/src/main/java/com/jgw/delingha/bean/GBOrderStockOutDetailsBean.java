package com.jgw.delingha.bean;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.ArrayList;
import java.util.List;

public class GBOrderStockOutDetailsBean {

    public int auditStatus;
    public String createTime;
    public List<ListBean> outHouseProducts;
    public String orderId;
    public int updateVersion;

    public static class ListBean implements Comparable<ListBean> {


        public int singleCodeNumber;//计划扫码数量(单码)
        public int actualSingleCodeNumber;//实际扫码数量(单码)

        public int firstOutNumber;
        public String firstOutNumberUnitCode;
        public String firstOutNumberUnitName;
        public Object harvestType;
        public String outHouseProductId;
        public Object marketDate;
        public String packingSpecification;
        public int planFirstOutNumber;
        public int planSecondOutNumber;
        public int planThirdOutNumber;
        public String productBatch;
        public String productBatchId;
        public String productCode;
        public String productId;
        public String productName;
        public String productSpecificationsName;
        public Object productWareHouse;
        public int secondOutNumber;
        public String secondOutNumberUnitCode;
        public String secondOutNumberUnitName;
        public String storeHouseId;
        public String storeHouseName;
        public String sweepStatus;
        public int thirdOutNumber;
        public String thirdOutNumberUnitCode;
        public String thirdOutNumberUnitName;
        public String wareHouseId;
        public String wareHouseCode;
        public String wareHouseName;
        public int reviewStatus;
        public int pickingStatus;//0-待拣货，1-拣货完成
        public int firstEnterNumber;//手输数量

        public List<OrderStockScanBean> codeList = new ArrayList<>();

        public String getSingleCodeNumberText() {
            return this.singleCodeNumber + "";
        }

        //实际扫码数量
        public String getSingleScanCodeNumberText() {
            return this.actualSingleCodeNumber +firstEnterNumber+ "";
        }

        public String getPlanNumber() {
            ArrayList<String> allUnit = new ArrayList<>();
            allUnit.add(planFirstOutNumber + firstOutNumberUnitName);
            if (!TextUtils.isEmpty(secondOutNumberUnitName)) {
                allUnit.add(planSecondOutNumber + secondOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(thirdOutNumberUnitName)) {
                allUnit.add(planThirdOutNumber + thirdOutNumberUnitName);
            }

            return getFilterString(allUnit);
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
            if (!TextUtils.isEmpty(thirdOutNumberUnitName)) {
                allUnit.add(third + thirdOutNumber + thirdOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(secondOutNumberUnitName)) {
                allUnit.add(second + secondOutNumber + secondOutNumberUnitName);
            }
            allUnit.add(first + firstOutNumber + firstOutNumberUnitName);

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


        public int getPickingVisibility() {
            return pickingStatus == 0 ? View.VISIBLE : View.GONE;
        }

        public int getPickedVisibility() {
            return pickingStatus == 0 ? View.GONE : View.VISIBLE;
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
