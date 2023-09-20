package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by XiongShaoWu
 * on 2020/7/8
 */
public class OrderExchangeGoodsCodeBean {

    /**
     * currentLevel :
     * outerCodeId :
     * singleCodeNumber : 0
     * sysVersion :
     */

    public OrderExchangeGoodsCodeBean(){}
    public OrderExchangeGoodsCodeBean(String code){
        outerCodeId=code;
    }
    public String currentLevel;
    public String outerCodeId;
    public int singleCodeNumber;
    public String sysVersion;
    public String productName;
    public String productCode;
    public int codeStatus;  // 本地定义:-1 正在验证  1 成功  2 失败

    public String getCodeStatusText() {
        switch (codeStatus) {
            case CodeBean.STATUS_CODE_VERIFYING:
                return "正在验证...";
            //成功时隐藏 显示√
//            case CodeBean.STATUS_CODE_SUCCESS:
//                return "验证成功";
            case CodeBean.STATUS_CODE_FAIL:
                return "验证失败";
            default:
                return "";
        }
    }

    public int getCodeStatusVisible() {
        return codeStatus == CodeBean.STATUS_CODE_SUCCESS ? View.VISIBLE : View.GONE;
    }
    public int getCodeStatusTextVisible() {
        return codeStatus == CodeBean.STATUS_CODE_SUCCESS ? View.GONE : View.VISIBLE;
    }

    @Override
    public int hashCode() {
        return outerCodeId == null ? super.hashCode() : outerCodeId.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof OrderExchangeGoodsCodeBean) {
            return TextUtils.equals(((OrderExchangeGoodsCodeBean) obj).outerCodeId, outerCodeId);
        } else {
            return false;
        }
    }
}
