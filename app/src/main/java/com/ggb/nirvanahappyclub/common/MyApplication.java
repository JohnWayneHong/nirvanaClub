package com.ggb.nirvanahappyclub.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ggb.common_library.base.CustomApplication;
import com.ggb.common_library.base.view.CustomDialog;
import com.ggb.common_library.utils.LogUtils;
import com.ggb.common_library.utils.json.JsonUtils;
import com.ggb.nirvanahappyclub.BuildConfig;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.tamsiree.rxkit.RxTool;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import io.objectbox.relation.ToOne;
import io.reactivex.plugins.RxJavaPlugins;
import me.jessyan.autosize.AutoSizeConfig;

/**
 * Created by XiongShaoWu
 * on 2019/11/14
 */
public class MyApplication extends CustomApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        checkDatabaseVersion();
        //ObjectBox数据库初始化
//        ObjectBoxUtils.init(this);
//        initUMeng();
        RxTool.init(this);
        CustomDialog.globalShowStatusBar = true;
        RxJavaPlugins.setErrorHandler(e -> {
        });
        AutoSizeConfig.getInstance().setExcludeFontScale(true);
        LogUtils.setDebugShowLog(BuildConfig.DEBUG);
        JsonUtils.addFilterClass(ToOne.class);

        initCustomRouter();
        initQiangDongBRV();
    }

    private void initCustomRouter() {
//        RegisterRouter.register(LoginPlugin.DOMAIN,LoginActivity.class);
    }

    private void initQiangDongBRV() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new BezierRadarHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context);
            }
        });


    }

    private void checkDatabaseVersion() {
//        int databaseVersion = MMKVUtils.getInt(ConstantUtil.DATABASE_VERSION);
//        if (databaseVersion == -1 || AppConfig.CURRENT_DATABASE_VERSION - databaseVersion > 1) {
//            //跨版本时删除数据库
//            BoxStore.deleteAllFiles(context, null);
//        }
//        MMKVUtils.save(ConstantUtil.DATABASE_VERSION, AppConfig.CURRENT_DATABASE_VERSION);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void initUMeng() {
        if (BuildConfig.DEBUG) {
            return;
        }
        UMConfigure.preInit(this, "64a38ca5a1a164591b41c056", "delingha-" + AppConfig.CURRENT_VERSION);

        UMConfigure.init(this, null, "delingha-" + AppConfig.CURRENT_VERSION, UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
        UMConfigure.setProcessEvent(true);
    }
}
