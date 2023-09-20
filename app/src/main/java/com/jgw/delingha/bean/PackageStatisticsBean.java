package com.jgw.delingha.bean;

@SuppressWarnings("rawtypes")
public class PackageStatisticsBean implements Comparable {
    public String productId;
    public String productName;
    public int firstCount;//单
    public int secondCount;//箱
    public int thirdCount;//盒
    public String packageTime;
    public String month;
    public String year;
    public String productCount;//包装产品数

    public String getFirstCountStr() {

        String s = firstCount + "";
        if (s.length()>3){
            String start = s.substring(0, s.length() - 3);
            String end = s.substring(s.length() - 3);
            s=start+" "+end;
        }
        return s;
    }

    public String getSecondCountStr() {
        String s = secondCount + "";
        if (s.length()>3){
            String start = s.substring(0, s.length() - 3);
            String end = s.substring(s.length() - 3);
            s=start+" "+end;
        }
        return s;
    }

    public String getThirdCountStr() {
        String s = thirdCount + "";
        if (s.length()>3){
            String start = s.substring(0, s.length() - 3);
            String end = s.substring(s.length() - 3);
            s=start+" "+end;
        }
        return s;
    }

    public String selectDate;

    @Override
    public int compareTo(Object o) {
        if (o instanceof PackageStatisticsBean) {
            return ((PackageStatisticsBean) o).firstCount - firstCount;
        } else {
            return 0;
        }
    }
}
