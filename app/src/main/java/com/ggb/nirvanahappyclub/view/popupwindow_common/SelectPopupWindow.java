package com.ggb.nirvanahappyclub.view.popupwindow_common;


import static com.ggb.common_library.base.CustomApplication.context;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.ggb.common_library.base.ui.BaseActivity;
import com.ggb.nirvanahappyclub.R;
import com.ggb.nirvanahappyclub.databinding.PopupwindowSelectBinding;

/**
 * 商品编辑(商品管理时点击弹出)
 */
public class SelectPopupWindow implements LifecycleObserver, View.OnClickListener {

    private PopupWindow mPopupWindow;
    private PopupwindowSelectBinding mDataBinding;
    private View.OnTouchListener touchListener = (v, event) -> {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    };
    private OnButtonClickListener mListener;
    private boolean mAutoDismiss;

    public void showPopupWindow(Context context, View rootView, String title, String des, OnButtonClickListener listener) {
        showPopupWindow(context,rootView,title,des,"取消","确定",true,listener);
    }
    public void showPopupWindow(Context context, View rootView, String title, String des, String left, String right, boolean autoDismiss,OnButtonClickListener listener) {
        mListener = listener;
        mAutoDismiss =autoDismiss;
        if (mPopupWindow == null) {
            mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popupwindow_select, null, false);
            //重置和确定
            //LinearLayout.LayoutParams.WRAP_CONTENT -2
            mPopupWindow = new PopupWindow(mDataBinding.getRoot(), -2, -2);
            mPopupWindow.setFocusable(true);//edittext可输入
            mDataBinding.getRoot().setOnTouchListener(touchListener);//拦截事件隐藏键盘
        }
        if (context instanceof BaseActivity) {
            ((BaseActivity<?, ?>) context).getLifecycle().addObserver(this);
        }
        mDataBinding.tvPopupwindowSelectTitle.setText(title);
        mDataBinding.tvPopupwindowSelectDes.setText(des);
        mDataBinding.tvPopupwindowSelectLeft.setText(left);
        mDataBinding.tvPopupwindowSelectRight.setText(right);
        mDataBinding.tvPopupwindowSelectLeft.setOnClickListener(this);
        mDataBinding.tvPopupwindowSelectRight.setOnClickListener(this);
        mPopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    @SuppressWarnings("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        dismiss();
        mPopupWindow = null;
        mDataBinding = null;
        touchListener = null;
    }

    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_popupwindow_select_left) {
            if (mListener != null) {
                mListener.onLeftButtonClick(this);
            }
            if (mAutoDismiss){
                dismiss();
            }
        } else if (id == R.id.tv_popupwindow_select_right) {
            if (mListener != null) {
                mListener.onRightButtonClick(this);
            }
            if (mAutoDismiss){
                dismiss();
            }
        }
    }

    public interface OnButtonClickListener {
        default void onLeftButtonClick(SelectPopupWindow v){};

        void onRightButtonClick(SelectPopupWindow v);
    }
}
