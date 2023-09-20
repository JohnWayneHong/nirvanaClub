package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderStockOutDetailsBean {

    public List<ListBean> outHouseProducts;
    public String taskId;

    public static class ListBean {

        public String productRecordId;//产品记录id
        public int singleCodeNumber;//计划数量
        public int actualSingleCodeNumber;//实际扫码单码数量(扫码+手输) 已上传数量
        public int currentInputSingleNumber;//当前手输数量
        public int tempInputSingleNumber;//临时手输数量上传成功后赋值
        public int firstOutNumber;
        public String firstOutNumberUnitCode;
        public String firstOutNumberUnitName;
        public String outHouseProductId;
        public int planFirstOutNumber;
        public int planSecondOutNumber;
        public int planThirdOutNumber;
        public String productBatch;
        public String productBatchId;
        public String productCode;
        public String productId;
        public String productName;
        public int secondOutNumber;
        public String secondOutNumberUnitCode;
        public String secondOutNumberUnitName;
        public String packingSpecification;
        public int thirdOutNumber;
        public String thirdOutNumberUnitCode;
        public String thirdOutNumberUnitName;
        public String wareHouseId;
        public String wareHouseName;
        public String wareHouseCode;
        public String tallAmount;
        public String tallFirstNumber;
        public String tallSecondNumber;
        public String tallThirdNumber;

        public String amount;
        public String deliverAmount;

        public String inputAmount;
        public String inputFirstNumber;
        public String inputSecondNumber;
        public String inputThirdNumber;
        public String inputTempAmount;
        public String inputTempFirstNumber;
        public String inputTempSecondNumber;
        public String inputTempThirdNumber;
        public List<OrderStockScanBean> codeList = new ArrayList<>();

        public String getPlanNumber() {
            ArrayList<String> allUnit = new ArrayList<>();
            if (!TextUtils.isEmpty(thirdOutNumberUnitName)) {
                allUnit.add(planThirdOutNumber + thirdOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(secondOutNumberUnitName)) {
                allUnit.add(planSecondOutNumber + secondOutNumberUnitName);
            }
            allUnit.add(planFirstOutNumber + firstOutNumberUnitName);
            String weight = "";
            if (!TextUtils.isEmpty(amount)) {
                weight = ";" + amount + "千克";
            }
            return getFilterString(allUnit) + weight;
        }

        public String getFinishNumber() {
            ArrayList<String> allUnit = new ArrayList<>();
            if (!TextUtils.isEmpty(thirdOutNumberUnitName)) {
                allUnit.add(thirdOutNumber + thirdOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(secondOutNumberUnitName)) {
                allUnit.add(secondOutNumber + secondOutNumberUnitName);
            }
            allUnit.add(firstOutNumber + firstOutNumberUnitName);
            String filterString = getFilterString(allUnit);

            if (TextUtils.isEmpty(deliverAmount)) {
                deliverAmount="0.00";
            }
            String weight = ";" + deliverAmount + "千克";
            filterString = filterString + weight;
            return filterString;
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
                allUnit.add(third + thirdOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(secondOutNumberUnitName)) {
                allUnit.add(second + secondOutNumberUnitName);
            }
            allUnit.add(first + firstOutNumberUnitName);
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

        public String getWarehouseText() {
            if (!TextUtils.isEmpty(wareHouseCode) && !TextUtils.isEmpty(wareHouseName)) {
                return wareHouseName + "(" + wareHouseCode + ")";
            } else if (!TextUtils.isEmpty(wareHouseName)) {
                return wareHouseName;
            } else {
                return null;
            }
        }

        public String getProductText() {
            if (!TextUtils.isEmpty(productCode) && !TextUtils.isEmpty(productName)) {
                return "(" + productCode + ")" + productName;
            } else if (!TextUtils.isEmpty(productName)) {
                return productName;
            } else {
                return null;
            }
        }

        public String getInputNumberText() {
            ArrayList<String> allUnit = new ArrayList<>();
            String first = TextUtils.isEmpty(inputFirstNumber) ? "0" : inputFirstNumber;
            String second = TextUtils.isEmpty(inputSecondNumber) ? "0" : inputSecondNumber;
            String third = TextUtils.isEmpty(inputThirdNumber) ? "0" : inputThirdNumber;
            if (!TextUtils.isEmpty(third) && !TextUtils.isEmpty(thirdOutNumberUnitName)) {
                allUnit.add(third + thirdOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(second) && !TextUtils.isEmpty(secondOutNumberUnitName)) {
                allUnit.add(second + secondOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(first) && !TextUtils.isEmpty(firstOutNumberUnitName)) {
                allUnit.add(first + firstOutNumberUnitName);
            }
            String filterString = getFilterString(allUnit);

            if (TextUtils.isEmpty(inputAmount)) {
                inputAmount="0.00";
            }
            String weight = ";" + inputAmount + "千克";
            filterString = filterString + weight;
            return filterString;
        }

        public String getCheckNumberText() {
            ArrayList<String> allUnit = new ArrayList<>();
            String first = TextUtils.isEmpty(tallFirstNumber) ? "0" : tallFirstNumber;
            String second = TextUtils.isEmpty(tallSecondNumber) ? "0" : tallSecondNumber;
            String third = TextUtils.isEmpty(tallThirdNumber) ? "0" : tallThirdNumber;
            if (!TextUtils.isEmpty(third) && !TextUtils.isEmpty(thirdOutNumberUnitName)) {
                allUnit.add(third + thirdOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(second) && !TextUtils.isEmpty(secondOutNumberUnitName)) {
                allUnit.add(second + secondOutNumberUnitName);
            }
            if (!TextUtils.isEmpty(first) && !TextUtils.isEmpty(firstOutNumberUnitName)) {
                allUnit.add(first + firstOutNumberUnitName);
            }
            String filterString = getFilterString(allUnit);

            if (TextUtils.isEmpty(tallAmount)) {
                tallAmount="0.00";
            }
            String weight = ";" + tallAmount + "千克";
            filterString = filterString + weight;
            return filterString;
        }

        public int getScanCodeVisible() {
            return TextUtils.equals(packingSpecification, "千克") ? View.GONE : View.VISIBLE;
        }

        public int getThirdOutNumberUnitNameVisible() {
            return TextUtils.isEmpty(thirdOutNumberUnitName) ? View.GONE : View.VISIBLE;
        }

        public int getSecondOutNumberUnitNameVisible() {
            return TextUtils.isEmpty(secondOutNumberUnitName) ? View.GONE : View.VISIBLE;
        }

        public int getFirstOutNumberUnitNameVisible() {
            return TextUtils.isEmpty(firstOutNumberUnitName) ? View.GONE : View.VISIBLE;
        }

        public double getCheckAmount() {
            double amount = 0;
            if (!TextUtils.isEmpty(inputAmount)) {
                amount = MathUtils.add(amount, Double.parseDouble(inputAmount));
            }
            return amount;
        }

        public int getCheckFirstNumber() {
            int first = 0;
            if (!TextUtils.isEmpty(inputFirstNumber)) {
                first = first + Integer.parseInt(inputFirstNumber);
            }
            for (OrderStockScanBean b : codeList) {
                first += b.firstLevel;
            }
            return first;
        }

        public int getCheckSecondNumber() {
            int second = 0;
            if (!TextUtils.isEmpty(inputSecondNumber)) {
                second = second + Integer.parseInt(inputSecondNumber);
            }
            for (OrderStockScanBean b : codeList) {
                second += b.secondLevel;
            }
            return second;
        }

        public int getCheckThirdNumber() {
            int third = 0;
            if (!TextUtils.isEmpty(inputThirdNumber)) {
                third = third + Integer.parseInt(inputThirdNumber);
            }
            for (OrderStockScanBean b : codeList) {
                third += b.thirdLevel;
            }
            return third;
        }
        public int isDetails;

        public int getIsDetailsVisible() {
            return isDetails == 0 ? View.VISIBLE : View.GONE;
        }
    }

    public String linkName;
    public String linkPhoneNumber;
    public String logisticsCompanyName;
    public String logisticsNumber;
    public String orderNo;
    public CustomerBean customerInfoDTO;

    public class CustomerBean {
        public String provinceName;
        public String cityName;
        public String countyName;
        public String detailedAddress;
    }

}
