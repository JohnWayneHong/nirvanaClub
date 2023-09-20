package com.jgw.delingha.bean;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.delingha.BR;
import com.jgw.delingha.common.RegisterUUIDActivity;

import java.io.Serializable;

/**
 * @author : J-T
 * @date : 2022/5/5 13:35
 * description :注册Bean类
 */
public class RegisterBean extends BaseObservable implements Serializable {

    /**
     * brand :
     * companyId :
     * companyName :
     * modelType :
     * remarks :
     * secretKey :
     */

    private String brand;
    private String companyId;
    private String companyName = "";
    private String modelType;
    private String remarks = "";
    private String secretKey;

    /**
     * 0 为已注册 123为未注册
     */
    private int type = 0;
    private String errorMessage = "";

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        notifyPropertyChanged(BR.companyName);
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
        notifyPropertyChanged(BR.remarks);
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Bindable
    public String getCompanyName() {
        return companyName;
    }

    @Bindable
    public String getRemarks() {
        return remarks;
    }


    public void setType(int type) {
        this.type = type;
        notifyPropertyChanged(BR.unRegisterVisible);
        notifyPropertyChanged(BR.waitedReviewVisible);
        notifyPropertyChanged(BR.disableVisible);
    }

    public String getBrand() {
        return brand;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getModelType() {
        return modelType;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        notifyPropertyChanged(BR.errorMessageText);
    }

    @Bindable
    public String getErrorMessageText() {
        return "原因:" + errorMessage;
    }

    @Bindable
    public int getUnRegisterVisible() {
        return type == RegisterUUIDActivity.UNREGISTERED ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getWaitedReviewVisible() {
        return type == RegisterUUIDActivity.WAITED_REVIEW ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getDisableVisible() {
        return type == RegisterUUIDActivity.DISABLE ? View.VISIBLE : View.GONE;
    }
}
