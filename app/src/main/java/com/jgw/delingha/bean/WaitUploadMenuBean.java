package com.jgw.delingha.bean;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.delingha.BR;

public class WaitUploadMenuBean extends BaseObservable {

    private String title;
    private String icon;
    private String appAuthCode;
    private Class<?> functionClass;
    private boolean isEmpty;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Class<?> getFunctionClass() {
        return functionClass;
    }

    public void setFunctionClass(Class<?> functionClass) {
        this.functionClass = functionClass;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
        notifyPropertyChanged(BR.redPointVisible);
    }

    public String getAppAuthCode() {
        return appAuthCode;
    }

    public void setAppAuthCode(String appAuthCode) {
        this.appAuthCode = appAuthCode;
    }

    @Bindable
    public int getRedPointVisible() {
        return isEmpty ? View.GONE : View.VISIBLE;
    }
}
