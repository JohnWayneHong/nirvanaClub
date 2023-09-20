package com.jgw.delingha.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

public class DisassembleScanBean {

    public static final int STATUS_CODE_VERIFYING = CodeBean.STATUS_CODE_VERIFYING;
    public static final int STATUS_CODE_SUCCESS = CodeBean.STATUS_CODE_SUCCESS;
    public static final int STATUS_CODE_FAIL = CodeBean.STATUS_CODE_FAIL;

    public String outerCodeId;
    public int codeStatus;  // 本地定义:-1 正在验证  1 成功  2 失败
    public String levelName;

    public DisassembleScanBean() {
        super();
    }

    public DisassembleScanBean(String code) {
        outerCodeId = code;

    }

    public DisassembleScanBean(String code, int status, String level) {
        outerCodeId = code;
        codeStatus = status;
        levelName = level;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof DisassembleScanBean) {
            return TextUtils.equals(((DisassembleScanBean) obj).outerCodeId, outerCodeId);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(outerCodeId)) {
            return super.hashCode();
        } else {
            return outerCodeId.hashCode();
        }
    }
}
