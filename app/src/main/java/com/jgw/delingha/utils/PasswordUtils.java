package com.jgw.delingha.utils;

public class PasswordUtils {
    public static final String STRONG_PASSWORD_CHECK_RULE = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-z0-9A-Z])(?=\\S+$).{8,20}$";

    /**
     * 强密码校验规则
     * 1、8-20位字符组成。
     * 2、密码要包含：大写字母+小写字母+特殊符号+数字四种组合。
     * @param password
     * @return
     */
    public static boolean strongPasswordIsLegal(String password) {
        return password.matches(STRONG_PASSWORD_CHECK_RULE);
    }
}
