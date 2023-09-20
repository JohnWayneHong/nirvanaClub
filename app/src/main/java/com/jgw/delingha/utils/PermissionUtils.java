package com.jgw.delingha.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/**
 * Created by Administrator on 2017/8/26 0026.
 */

public class PermissionUtils {
    public static boolean checkPermissionAllGranted(Context contexts, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(contexts, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }
}
