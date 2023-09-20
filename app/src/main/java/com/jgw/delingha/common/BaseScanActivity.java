package com.jgw.delingha.common;

import android.view.KeyEvent;

import androidx.databinding.ViewDataBinding;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;

/**
 * @author : J-T
 * @date : 2023/5/24 9:22
 * description :
 */
public abstract class BaseScanActivity<VM extends BaseViewModel, SV extends ViewDataBinding> extends BaseActivity<VM, SV> {
    public boolean scanable = true;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 191) {
            if (event.getRepeatCount() == 0) {
                scanable = true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
