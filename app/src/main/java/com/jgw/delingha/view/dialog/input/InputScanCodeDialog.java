package com.jgw.delingha.view.dialog.input;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jgw.common_library.base.view.CustomDialog;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InputScanCodeDialogBean;
import com.jgw.delingha.databinding.DialogInputScanCodeBinding;

import java.math.BigInteger;

public class InputScanCodeDialog extends CustomDialog implements View.OnClickListener {

    private InputScanCodeDialog.OnButtonClickListener mOnButtonClickListener;
    private DialogInputScanCodeBinding viewDataBinding;
    private final InputScanCodeDialogBean mData = new InputScanCodeDialogBean();
    private TextWatcher watcher;

    public InputScanCodeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    public static InputScanCodeDialog newInstance(Context context) {
        return newInstance(context, R.style.CustomDialog);
    }

    public static InputScanCodeDialog newInstance(Context context, int resId) {
        return new InputScanCodeDialog(context, resId);
    }

    private void init() {
        LayoutInflater inflater = getLayoutInflater();
        viewDataBinding = DataBindingUtil.inflate(inflater
                , R.layout.dialog_input_scan_code, null, false);
        viewDataBinding.setData(mData);
        viewDataBinding.tvInputScanCodeDialogCommonLeft.setOnClickListener(this);
        viewDataBinding.tvInputScanCodeDialogCommonRight.setOnClickListener(this);
        viewDataBinding.tvInputScanCodeDialogCommonRight.setOnClickListener(this);
        setContentView(viewDataBinding.getRoot());

        watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().trim();
                if (trim.length() != 16) {
                    return;
                }
                calculateCount();
            }
        };
        viewDataBinding.etInputScanCodeDialogInput1.addTextChangedListener(watcher);
        viewDataBinding.etInputScanCodeDialogInput2.addTextChangedListener(watcher);
    }

    private void calculateCount() {
        InputScanCodeDialogBean data = getData();
        String input1 = data.getInput1();
        if (TextUtils.isEmpty(input1) || input1.length() != 16) {
            return;
        }
        String input2 = data.getInput2();
        if (TextUtils.isEmpty(input2) || input2.length() != 16) {
            return;
        }
        BigInteger startCode = new BigInteger(input1);
        BigInteger endCode = new BigInteger(input2);
        BigInteger subtract = endCode.subtract(startCode);
        long l = subtract.longValue();
        long abs = Math.abs(l) + 1;
        if (abs > 1000) {
            ToastUtils.showToast("码数量过大不能录入!");
            return;
        }
        data.setCount(abs + "");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == viewDataBinding.tvInputScanCodeDialogCommonLeft.getId()) {
            onLeftClick();
        } else if (id == viewDataBinding.tvInputScanCodeDialogCommonRight.getId()) {
            onRightClick();
        }
    }

    private void onRightClick() {
        hideSoftKeyboard();
        doRightButtonClick();
    }

    private void onLeftClick() {
        hideSoftKeyboard();
        doLeftButtonClick();
    }

    public InputScanCodeDialog setCustomTitle(CharSequence title) {
        mData.setTitle((String) title);
        return this;
    }

    public InputScanCodeDialog setInputHintText(String hintText) {
        mData.setInputHint(hintText);
        return this;
    }

    public InputScanCodeDialog setLeftButtonStr(String leftButtonStr) {
        mData.setLeft(leftButtonStr);
        return this;
    }

    public InputScanCodeDialog setRightButtonStr(String rightButtonStr) {
        mData.setRight(rightButtonStr);
        return this;
    }

    public InputScanCodeDialog setOnButtonClickListener(InputScanCodeDialog.OnButtonClickListener l) {
        mOnButtonClickListener = l;
        return this;
    }

    public InputScanCodeDialogBean getData() {
        return mData;
    }

    private void doLeftButtonClick() {
        if (mOnButtonClickListener == null) {
            dismiss();
            return;
        }
        if (!mOnButtonClickListener.isRightConfirm()) {
            boolean inputOk = mOnButtonClickListener.onInput(mData.getInput1());
            if (!inputOk) {
                return;
            }
        }
        mOnButtonClickListener.onLeftClick();
        boolean autoDismiss = mOnButtonClickListener.onAutoDismiss();
        if (autoDismiss) {
            dismiss();
        }
    }

    private void doRightButtonClick() {
        if (mOnButtonClickListener == null) {
            dismiss();
            return;
        }
        if (mOnButtonClickListener.isRightConfirm()) {
            boolean inputOk = mOnButtonClickListener.onInput(mData.getInput1());
            if (!inputOk) {
                return;
            }
        }
        mOnButtonClickListener.onRightClick();
        boolean autoDismiss = mOnButtonClickListener.onAutoDismiss();
        if (autoDismiss) {
            dismiss();
        }
    }


    public interface OnButtonClickListener {
        default void onLeftClick() {
        }

        default void onRightClick() {
        }

        default boolean isRightConfirm() {
            return true;
        }

        default boolean onInput(String input) {
            return true;
        }

        default boolean onAutoDismiss() {
            return true;
        }
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
