package com.jgw.delingha.view.dialog.input;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jgw.common_library.base.view.CustomDialog;
import com.jgw.common_library.utils.InputFormatUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.databinding.DialogInputCheckNumberBinding;

public class InputCheckNumberDialog extends CustomDialog implements View.OnClickListener {

    private DialogInputCheckNumberBinding viewDataBinding;
    private OrderStockOutDetailsBean.ListBean mData;
    private TextWatcher watcher;
    private OnButtonClickListener mOnButtonClickListener;

    public InputCheckNumberDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    public static InputCheckNumberDialog newInstance(Context context) {
        return newInstance(context, R.style.CustomDialog);
    }

    public static InputCheckNumberDialog newInstance(Context context, int resId) {
        return new InputCheckNumberDialog(context, resId);
    }

    private void init() {
        LayoutInflater inflater = getLayoutInflater();
        viewDataBinding = DataBindingUtil.inflate(inflater
                , R.layout.dialog_input_check_number, null, false);
//        viewDataBinding.setData(mData);
        viewDataBinding.tvInputCheckNumberCancel.setOnClickListener(this);
        viewDataBinding.tvInputCheckNumberConfirm.setOnClickListener(this);
        setContentView(viewDataBinding.getRoot());
        new InputFormatUtils().limitInput(6, 0, viewDataBinding.etInputCheckNumber1);
        new InputFormatUtils().limitInput(6, 0, viewDataBinding.etInputCheckNumber2);
        new InputFormatUtils().limitInput(6, 0, viewDataBinding.etInputCheckNumber3);
        new InputFormatUtils().limitInput(6, 3, viewDataBinding.etInputCheckNumberAmount);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == viewDataBinding.tvInputCheckNumberConfirm.getId()) {
            mData.inputAmount = mData.inputTempAmount;
            mData.inputThirdNumber = mData.inputTempThirdNumber;
            mData.inputSecondNumber = mData.inputTempSecondNumber;
            mData.inputFirstNumber = mData.inputTempFirstNumber;
            if (mOnButtonClickListener!=null){
                mOnButtonClickListener.onRightClick();
            }
        }
        dismiss();
    }


    public OrderStockOutDetailsBean.ListBean getData() {
        return mData;
    }

    public void setData(OrderStockOutDetailsBean.ListBean data) {
        mData = data;
        viewDataBinding.setData(data);
    }


    public void setOnButtonClickListener(OnButtonClickListener l) {
        mOnButtonClickListener = l;
    }


    public interface OnButtonClickListener {
        void onRightClick();
    }

    /**
     * 隐藏软键盘(可用于Activity，Fragment)
     */
    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void dismiss() {
        try {
            hideSoftKeyboard();
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
