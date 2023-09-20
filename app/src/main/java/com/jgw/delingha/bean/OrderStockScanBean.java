package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 订单出入库码校验返回接口
 */
public class OrderStockScanBean {
    public OrderStockScanBean() {
    }
    public OrderStockScanBean(String code) {
        outerCodeId = code;
    }

    public String outerCodeId;
    public int codeStatus;  // 本地定义:-1 正在验证  1 成功  2 失败
    public int firstLevel;
    public int secondLevel;
    public int thirdLevel;
    public int currentLevel;
    public int singleNumber;
    public int codeNumber;
    public boolean isRealError;//用来标识服务器验证错误的码
    public String currentUnitName;
    public String getCodeStatusText() {
        switch (codeStatus) {
            case CodeBean.STATUS_CODE_VERIFYING:
                return "正在验证...";
            case CodeBean.STATUS_CODE_SUCCESS:
                return "验证成功";
            case CodeBean.STATUS_CODE_FAIL:
                return "验证失败";
            default:
                return "";
        }
    }
    public int getCheckStatusVisible() {
        switch (codeStatus) {
            case CodeBean.STATUS_CODE_VERIFYING:
                return View.VISIBLE;
            case CodeBean.STATUS_CODE_SUCCESS:
                return View.GONE;
            case CodeBean.STATUS_CODE_FAIL:
                //noinspection DuplicateBranchesInSwitch
                return View.VISIBLE;
            default:
                return View.VISIBLE;
        }
    }
    public int getSuccessStatusVisible() {
        switch (codeStatus) {
            case CodeBean.STATUS_CODE_VERIFYING:
                //noinspection DuplicateBranchesInSwitch
                return View.GONE;
            case CodeBean.STATUS_CODE_SUCCESS:
                return View.VISIBLE;
            case CodeBean.STATUS_CODE_FAIL:
                //noinspection DuplicateBranchesInSwitch
                return View.GONE;
            default:
                return View.GONE;
        }
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof OrderStockScanBean) {
            return TextUtils.equals(((OrderStockScanBean) obj).outerCodeId, outerCodeId);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(outerCodeId)) {
            return super.hashCode();
        } else {
            return outerCodeId.hashCode();
        }
    }
}
