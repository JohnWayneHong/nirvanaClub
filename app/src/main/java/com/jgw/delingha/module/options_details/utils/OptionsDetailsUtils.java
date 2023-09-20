package com.jgw.delingha.module.options_details.utils;

import android.text.TextUtils;

import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.InfoDetailsDemoBean;

import java.util.List;

public class OptionsDetailsUtils {
    public static boolean checkItem(List<InfoDetailsDemoBean> dataList){
        for (int i = 0; i < dataList.size(); i++) {
            InfoDetailsDemoBean bean = dataList.get(i);
            if (bean.required) {
                if (bean.value == null || TextUtils.isEmpty(bean.value.valueStr)) {
                    ToastUtils.showToast(bean.key + "不能为空");
                    return true;
                }
            }
            if (bean.checkNumber && bean.value!=null){
                String valueStr = bean.value.valueStr;
                if (!TextUtils.isEmpty(valueStr)&&Double.parseDouble(valueStr)==0){
                    ToastUtils.showToast("请输入有效的"+bean.key+"!");
                    return true;
                }

            }
        }
        return false;
    }
}
