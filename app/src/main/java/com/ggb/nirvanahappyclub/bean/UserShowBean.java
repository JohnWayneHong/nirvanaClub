package com.ggb.nirvanahappyclub.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ggb.nirvanahappyclub.BR;

import java.util.List;

public class UserShowBean extends BaseObservable {

    public String mobile;
    public String userID;
    public String userName;
    public String token;
    public String role;
    public String storeId;
    public String storeName;
    public String orgId;
    public String orgName;
    public String showName;
    public String subsribeCount;
    public String fansCount;
    public List<String> powerCode;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.showUserInfo);
    }

    @Bindable
    public String getShowUserInfo() {
        return "姓名" + userName + "电话" + mobile;
    }
}
