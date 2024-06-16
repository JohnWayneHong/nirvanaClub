package com.ggb.nirvanahappyclub.bean;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ggb.nirvanahappyclub.BR;

public class WordLoaderBean extends BaseObservable {

    private String currentProcess;

    private String totalProcess;

    private boolean isComplete;

    public String getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(String currentProcess) {
        this.currentProcess = currentProcess;
        notifyPropertyChanged(BR.showProcessText);
    }

    public String getTotalProcess() {
        return totalProcess;
    }

    public void setTotalProcess(String totalProcess) {
        this.totalProcess = totalProcess;
        notifyPropertyChanged(BR.showProcessText);
    }

    @Bindable
    public String getShowProcessText() {
        return "插入单词进度" + (TextUtils.isEmpty(currentProcess) ? "0" : currentProcess) + "/" + totalProcess;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
