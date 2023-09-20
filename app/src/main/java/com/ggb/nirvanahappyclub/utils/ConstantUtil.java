package com.ggb.nirvanahappyclub.utils;

/**
 * @author : J-T
 * @date : 2022/1/25 9:32
 * description : 常量
 */
public class ConstantUtil {
    /**
     * intent传递configId值的常量
     */
    public static String CONFIG_ID = "configId";


    public static final String USER_REMEMBER_ME = "user_remember_me";
    public static final String USER_MOBILE = "user_mobile";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_TOKEN = "user_token";

    public static final String ORGANIZATION_ID = "organization_id";
    public static final String ORGANIZATION_NAME = "organization_name";
    public static final String ORGANIZATION_ICON = "organization_icon";
    public static final String SYSTEM_ID = "system_id";
    public static final String SYSTEM_NAME = "system_name";

    public static final String FIRST_IN = "first_in";

    public static final String HOME_MENU = "home_menu";//首页菜单带层级
    public static final String LOCAL_MENU = "local_menu";//本地菜单平铺功能
    public static final String CURRENT_CUSTOMER_ID = "current_customer_id";
    public static final String CURRENT_CUSTOMER_TYPE = "current_customer_type";// 0客户 1企业总部
    public static final String LAST_CHECK_VERSION_CODE = "last_check_version_code";//上次检测版本的版本号
    public static final String LAST_CHECK_VERSION_TIME = "last_check_version_time";//上次检测版本的时间
    public static final String NUMBER_OF_REMINDERS = "number_of_reminders";//提醒次数
    public static final String SYSTEM_EXPIRE_TIME = "system_expire_time";//系统到期时间
    public static final String SCAN_RULE = "scan_rule";

    public static final String NEED_CHANGE_PASSWORD = "need_change_password";
    public static final String TODAY_CHECK_NEED_CHANGE_PASSWORD = "today_check_need_change_password";

    public static final String HTTP_TYPE = "http_type"; //环境类型 用来切换测试和预发布
    public static final int TYPE_DEBUG = 1; //测试环境
    public static final int TYPE_TEST = 2;//预发布环境
    public static final int TYPE_PRERELEASE = 3;//预发布环境

    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";

    /**
     * 数据库相关信息
     */
    public static final String USER_ENTITY_ID = "user_entity_id";

    //当前版本
    public static final String VERSION_TYPE = "version_type";

    public static final String DATABASE_VERSION = "database_version";

    //暂存合格证
    public static final String SAVA_TEMP_IMAGE = "sava_temp_image";

}
