package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.delingha.BR;

/**
 * @author : J-T
 * @date : 2022/8/2 11:32
 * description :标识替换 dataBinding bean类
 */
public class IdentificationReplaceBean extends BaseObservable {
    private String sourceCode;
    private String targetCode;

    @Bindable
    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
        notifyPropertyChanged(BR.sourceCode);
        notifyPropertyChanged(BR.sourceCodeClearVisible);
    }

    @Bindable
    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
        notifyPropertyChanged(BR.targetCode);
        notifyPropertyChanged(BR.targetCodeClearVisible);
    }

    @Bindable
    public int getSourceCodeClearVisible() {
        return TextUtils.isEmpty(sourceCode) ? View.GONE : View.VISIBLE;
    }

    @Bindable
    public int getTargetCodeClearVisible() {
        return TextUtils.isEmpty(targetCode) ? View.GONE : View.VISIBLE;
    }

}
