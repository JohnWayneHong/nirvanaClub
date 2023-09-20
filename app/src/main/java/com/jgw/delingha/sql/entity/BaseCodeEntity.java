package com.jgw.delingha.sql.entity;

import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;

import io.objectbox.relation.ToOne;

/**
 * Created by XiongShaoWu
 * on 2020/3/16
 */
public abstract class BaseCodeEntity extends BaseEntity{
    public static final int STATUS_CODE_VERIFYING = -1;
    public static final int STATUS_CODE_SUCCESS = 1;
    public static final int STATUS_CODE_FAIL = 2;

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract int getCodeStatus();

    public abstract void setCodeStatus(int status);

    public abstract void setCodeLevel(int level);

    public abstract int getCodeLevel();

    public abstract void setCodeLevelName(String unit);

    public abstract String getCodeLevelName();

    public int getCodeLevelNameVisible() {
        return TextUtils.isEmpty(getCodeLevelName()) ? View.GONE : View.VISIBLE;
    }

    //单码数量
    public abstract void setSingleNumber(int number);
    public abstract int getSingleNumber();

    public  String getSingleNumberText() {
        return String.valueOf(getSingleNumber());
    }

    public int getSingleNumberVisible() {
        return  getSingleNumber() == 0 ? View.GONE : View.VISIBLE;
    }

    public String getCodeStatusText() {
        switch (getCodeStatus()) {
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

    public int getCodeStatusTextColor() {
        switch (getCodeStatus()) {
            case CodeBean.STATUS_CODE_VERIFYING:
            case CodeBean.STATUS_CODE_SUCCESS:
                return ResourcesUtils.getColor(R.color.main_color);
            case CodeBean.STATUS_CODE_FAIL:
                return ResourcesUtils.getColor(R.color.red_f1);
            default:
                return ResourcesUtils.getColor(R.color.main_color);
        }
    }

    public int getCodeStatusImageVisible() {
        return getCodeStatus() == CodeBean.STATUS_CODE_SUCCESS ? View.VISIBLE : View.GONE;
    }

    public int getCodeStatusTextVisible() {
        return getCodeStatus() == CodeBean.STATUS_CODE_SUCCESS ? View.GONE : View.VISIBLE;
    }

    public abstract ToOne<UserEntity> getUserEntity();

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(getCode())) {
            return super.hashCode();
        } else {
            return getCode().hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseCodeEntity)) {
            return false;
        }
        BaseCodeEntity that = (BaseCodeEntity) o;
        return TextUtils.equals(getCode(), that.getCode());
    }
}
