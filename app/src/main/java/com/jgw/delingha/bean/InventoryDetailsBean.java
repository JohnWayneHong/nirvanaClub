package com.jgw.delingha.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class InventoryDetailsBean {
    public List<ListBean> inventoryProducts;

    public static class ListBean {

        public String inventoryProductId;

        public String houseList;

        public String wareHouseName;

        public String wareHouseCode;

        public String wareHouseId;

        public String productName;
        public String productId;

        public String productCode;

        public int sysFirstNumber;

        public int inventoryFirstNumber;

        public String firstUnitName;

        public int sysSecondNumber;

        public int inventorySecondNumber;

        public String secondUnitName;

        public String thirdUnitName;

        public int inventoryThirdNumber;

        public int sysThirdNumber;

        public int status;

        public String createTime;

        public String operatorName;

        public String getSystemNumber() {
            ArrayList<String> allUnit = new ArrayList<>();
            allUnit.add(sysFirstNumber + firstUnitName);
            allUnit.add(sysSecondNumber + secondUnitName);
            allUnit.add(sysThirdNumber + thirdUnitName);

            return getFilterString(allUnit);
        }

        public String getCurrentNumber() {
            ArrayList<String> allUnit = new ArrayList<>();
            allUnit.add(inventoryFirstNumber + firstUnitName);
            allUnit.add(inventorySecondNumber + secondUnitName);
            allUnit.add(inventoryThirdNumber + thirdUnitName);

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
    }
}
