package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import androidx.databinding.Bindable;

import java.util.List;

/**
 * Created by xswwg
 * on 2021/3/15
 */
public abstract class BaseOrderProductEntity extends BaseEntity {
    public abstract String getProductName();

    public abstract String getProductId();

    public abstract String getProductCode();

    public abstract String getProductBatchName();

    public abstract int getPlanNumber();

    /**
     * 实际扫码数量(已上传)
     */
    public abstract int getScanCodeNumber();

    public abstract long getId();

    public abstract List<? extends BaseOrderScanCodeEntity> getScanCodeList();

    /**
     * 当前扫码的单码数量
     */
    public int getScanCodeSingleNumber() {
        int number = 0;
        List<? extends BaseOrderScanCodeEntity> scanCodeList = getScanCodeList();
        if (scanCodeList == null) {
            return 0;
        }
        for (BaseOrderScanCodeEntity e : scanCodeList) {
            number += e.getSingleNumber();
        }
        return number;
    }

    public int getUnCheckCodeNumber() {
        int number = 0;
        List<? extends BaseOrderScanCodeEntity> scanCodeList = getScanCodeList();
        if (scanCodeList == null) {
            return 0;
        }
        for (BaseOrderScanCodeEntity e : scanCodeList) {
            if (e.getCodeStatus() != BaseCodeEntity.STATUS_CODE_SUCCESS) {
                number++;
            }
        }
        return number;
    }

    @Bindable
    public String getUnCheckCodeNumberText() {
        int unCheckCodeNumber = getUnCheckCodeNumber();
        if (unCheckCodeNumber == 0) {
            return "";
        }
        return "("+getUnCheckCodeNumber()+")";
    }

    /**
     * 当前手输的单码数量
     */
    public int getCurrentInputSingleNumber() {
        return 0;
    }

    /**
     * 当前手输的单码数量
     */
    public int getTotalSingleNumber() {
        return getTotalCurrentSingleNumber() + getScanCodeNumber();
    }

    /**
     * 当前全部单码数量
     */
    public int getTotalCurrentSingleNumber() {
        return getScanCodeSingleNumber() + getCurrentInputSingleNumber();
    }

    @Bindable
    public String getTotalCurrentSingleNumberText() {
        return getTotalCurrentSingleNumber() + "";
    }

    public int getTotalScanCodeSingleNumber() {
        return getScanCodeNumber() + getScanCodeSingleNumber();
    }

    public String getTaskId() {
        return null;
    }

    public abstract List<? extends BaseOrderScanCodeEntity> getCodeList();


    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(getProductId())) {
            return super.hashCode();
        } else {
            return getProductId().hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseOrderProductEntity)) {
            return false;
        }
        BaseOrderProductEntity that = (BaseOrderProductEntity) o;
        return TextUtils.equals(getProductId(), that.getProductId());
    }
}
