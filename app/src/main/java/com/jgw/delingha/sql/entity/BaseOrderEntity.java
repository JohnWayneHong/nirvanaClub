package com.jgw.delingha.sql.entity;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.List;

/**
 * author : xsw
 * data : 2021/3/15
 * description : 订单基类
 */
public abstract class BaseOrderEntity extends BaseEntity {
    public static final int NO_SCAN_CODE = 0;//未扫码
    public static final int PARTIAL_SCAN_CODE = 1;//部分扫码
    public static final int WAIT_UPLOAD = 2;//待上传

    private int itemSelect;

    public abstract int getOrderVersion();//获取订单信息版本号

    public abstract int getOrderStatus();//获取订单状态  0未扫码 1部分扫码 2待上传

    public int getItemSelect() {
        return itemSelect;
    }

    public void setItemSelect(int itemSelect) {
        this.itemSelect = itemSelect;
    }

    public String getTaskId(){
        return null;
    }

    public abstract List<? extends BaseOrderProductEntity> getProductList();

    public abstract String getOrderCode();

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(getOrderCode())) {
            return super.hashCode();
        } else {
            return getOrderCode().hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseOrderEntity)) {
            return false;
        }
        BaseOrderEntity that = (BaseOrderEntity) o;
        return TextUtils.equals(getOrderCode(), that.getOrderCode());
    }

    public Drawable _getSelectImage(){
        int resId=getItemSelect()==1? R.drawable.selected_icon:R.drawable.unselected_icon;
        return ResourcesUtils.getDrawable(resId);
    }
}
