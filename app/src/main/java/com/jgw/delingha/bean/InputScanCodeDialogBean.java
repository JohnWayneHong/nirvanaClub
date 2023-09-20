package com.jgw.delingha.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.common_library.BR;

public class InputScanCodeDialogBean extends BaseObservable {
    private String title;
    private String left;
    private String right;
    private String input1;
    private String input2;

    private String count;
    private String inputHint;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.titleText);
    }

    @Bindable
    public CharSequence getTitleText() {
        return getTitle();
    }

    @Bindable
    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
        notifyPropertyChanged(BR.left);
    }

    @Bindable
    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
        notifyPropertyChanged(BR.right);
    }

    @Bindable
    public String getInputHint() {
        return inputHint;
    }

    public void setInputHint(String inputHint) {
        this.inputHint = inputHint;
        notifyPropertyChanged(BR.inputHint);
    }

    @Bindable
    public String getInput1() {
        return input1;
    }

    public void setInput1(String input1) {
        this.input1 = input1;
        notifyPropertyChanged(BR.input1);
    }

    @Bindable
    public String getInput2() {
        return input2;
    }

    public void setInput2(String input2) {
        this.input2 = input2;
        notifyPropertyChanged(BR.input2);
    }

    @Bindable
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
        notifyPropertyChanged(BR.count);
    }
}
