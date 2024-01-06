package com.ggb.nirvanahappyclub.common;

import android.os.Environment;

import com.ggb.common_library.base.ui.BaseActivity;


/**
 * app相关配置
 */

public class AppConfig {

    public static String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    //文件存储根目录
    public final static String FILE_SAVE_PATH = SD_CARD_PATH + "/jgw/";
    //缓存根目录
    public final static String CACHE_PATH = FILE_SAVE_PATH + "cache/";
    //图片根目录
    public final static String IMAGE_PATH = FILE_SAVE_PATH + "image/";
    //日志根目录
    public final static String LOG_PATH = FILE_SAVE_PATH + "logs/";
    //文件根目录
    public final static String FILE_PATH = FILE_SAVE_PATH + "file/";
    //文件根目录
    public final static String FILE_DOWNLOAD_PATH = FILE_SAVE_PATH + "download/";

    //牛蛙呐博客地址 用于webview的前缀
    public final static String NIRVANA_BLOG_PATH =  "https://nirvana1234.xyz/v2/blog/";

    public static String getFullWidthImage(){
        return "?imageView/2/w/"+ BaseActivity.mPhoneWidth +"/";
    }
    public static String getCustomWidthImage(int dp){
        int width= (int) (dp*BaseActivity.getXMultiple());
        return "?imageView/2/w/"+ width +"/";
    }

    //当前版本 德令哈
    public final static int CURRENT_VERSION = 35;

    //发布时使用上次线上提交产生default.json文件 然后切换至最新提交覆盖json 生成差分文件 版本号+1
    //代表线上环境每次发布之间数据库版本差异 ObjectBox无法有效处理隔代升级
    //通过自定义版本号管理顺序升级 隔代删表
    public final static int CURRENT_DATABASE_VERSION = 6;

    //        operationType 01pc 02pda 03app 04产线
    public final static String OPERATION_TYPE = "030002";

}
