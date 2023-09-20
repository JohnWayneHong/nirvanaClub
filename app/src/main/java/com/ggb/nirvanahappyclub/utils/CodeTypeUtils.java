package com.ggb.nirvanahappyclub.utils;

import android.text.TextUtils;

/**
 * Created by XiongShaoWu
 * on 2020/5/25
 */
public class CodeTypeUtils {
    //packageSceneType 包装场景类型 0- 是生产包装 1- 是仓储包装 2-混合包装 3-补码入箱
    public static int PackageAssociationType = 0;
    public static int WareHousePackageType = 1;
    public static int MixPackageType = 2;
    public static int SupplementToBoxType = 3;

    public static String getCodeTypeId(String code) {
        int codeTypeId = 0;
        if (TextUtils.isEmpty(code)) {
            throw new IllegalStateException("码" + code + "不存在");
        }
        int codeLength = code.length();

        switch (codeLength) {
            //20位物流,21位防伪
            case 21:
            case 20:
                codeTypeId = codeLength;
                break;
            case 18:
                //18位溯源
                codeTypeId = 15;
                break;
            case 17:
                //17位营销
                codeTypeId = 12;
                break;
            //16顺序
            case 16:
                codeTypeId = 14;
                break;
            case 19:
                codeTypeId = 13;
                break;
            default:
                throw new IllegalStateException("该码不存在，请仔细核对！");
        }
        return String.valueOf(codeTypeId);
    }
}
