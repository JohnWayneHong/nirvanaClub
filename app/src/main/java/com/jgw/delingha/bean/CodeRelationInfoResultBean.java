package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.delingha.BR;
import com.jgw.delingha.R;
import com.jgw.delingha.utils.UnitcodeUtil;

import java.util.List;

public class CodeRelationInfoResultBean extends BaseObservable {

    public int codeLevel;
    public String createTime;
    public String outerCode;
    public String packageSpecification;
    public String parentCode;
    public String parentProductName;
    public String parentProductCode;
    public String productBatchId;
    public String productBatchName;
    public String productCode;
    public String productId;
    public String productName;
    public String productNumberCode;
    public String remark;
    public String userId;
    public String userName;//操作人
    public String workOrderCode;//工单号
    public String productionLineName;//工单号
    public int sonLeafCount;//最小单位码数量
    public List<SonCodeVoListBean> sonCodeVoList;

    private int showDetailsStatus;//详细信息展示状态 0隐藏 1展示

    @Bindable
    public int getShowDetailsVisible() {
        return showDetailsStatus == 1 ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getShowDetailsArrow() {
        return showDetailsStatus == 1 ? R.drawable.icon_up_arrow2 : R.drawable.icon_down_arrow2;
    }

    public void switchShowDetailsStatus() {
        showDetailsStatus = showDetailsStatus == 0 ? 1 : 0;
        notifyPropertyChanged(BR.showDetailsArrow);
        notifyPropertyChanged(BR.showDetailsVisible);
    }

    public int getSonNumber() {
        int number = 0;
        if (sonCodeVoList != null) {
            number = sonCodeVoList.size();
        }
        return number;
    }

    public String getPackageLevelNameStr() {
        if (TextUtils.isEmpty(productNumberCode)) {
            if (codeLevel == 1) {
                return "单码";
            } else if (codeLevel == 2) {
                return "盒码";
            } else if (codeLevel == 3) {
                return "箱码";
            } else {
                return "";
            }
        } else {
            return UnitcodeUtil.getName(productNumberCode);
        }
    }
    public int getPackageLevelNameVisible() {
        return TextUtils.isEmpty(getPackageLevelNameStr())?View.GONE:View.VISIBLE;
    }

    public String getParentProductText() {
        if (!TextUtils.isEmpty(parentProductCode) && !TextUtils.isEmpty(parentProductName)) {
            return "(" + parentProductCode + ")" + parentProductName;
        } else if (!TextUtils.isEmpty(parentProductName)) {
            return parentProductName;
        } else {
            return null;
        }
    }

    public static class SonCodeVoListBean {
        //        public int codeTypeId;
        public String outerCode;
        public String productName;
        public String productCode;

        public String getProductText() {
            if (!TextUtils.isEmpty(productCode) && !TextUtils.isEmpty(productName)) {
                return "(" + productCode + ")" + productName;
            } else if (!TextUtils.isEmpty(productName)) {
                return productName;
            } else {
                return null;
            }
        }

    }
}
