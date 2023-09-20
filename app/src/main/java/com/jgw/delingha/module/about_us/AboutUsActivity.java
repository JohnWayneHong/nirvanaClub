package com.jgw.delingha.module.about_us;


import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.SelectDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.widget.selectDialog.SelectDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.AboutUsBean;
import com.jgw.delingha.databinding.ActivityAboutUsBinding;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.ArrayList;


public class AboutUsActivity extends BaseActivity<BaseViewModel, ActivityAboutUsBinding> {


    private int mSecretNumber = 0;
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;

    @Override
    protected void initView() {
        setTitle("关于我们");
    }

    @Override
    protected void initData() {
        AboutUsBean bean = new AboutUsBean();
        bean.weChat = "18969929959";
        bean.mailBox = "chenyong@app315.net";
        bean.phone = "4006-822-110";
        bean.offical = "https://www.chaojima.com/";
        mBindingView.setData(bean);
    }

    @Override
    protected void initListener() {
        super.initListener();

        findViewById(R.id.tv_about_us_debug).setOnClickListener(v -> {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    if (elapsedTime < MIN_CLICK_INTERVAL) {
                        ++mSecretNumber;
                        if (9 == mSecretNumber) {
                            // to do 在这处理你想做的事件
                            LogUtils.setDebugShowLog(true);
                            LogUtils.xswShowLog("debugLog开启");
                        }
                    } else {
                        mSecretNumber = 0;
                    }
                }
        );
        findViewById(R.id.tv_about_us_switch_http).setOnClickListener(v -> {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    if (elapsedTime < MIN_CLICK_INTERVAL) {
                        ++mSecretNumber;
                        if (9 == mSecretNumber) {
                            // to do 在这处理你想做的事
                            ArrayList<String> list = new ArrayList<String>();
                            list.add("开发");
                            list.add("测试");
                            list.add("预发");
                            SelectDialogUtil.showSelectDialog(this, list, new SelectDialog.OnSelectDialogItemClickListener() {
                                @SuppressWarnings("DuplicateBranchesInSwitch")
                                @Override
                                public void onSingleItemClick(View view, int position, String string, SelectDialog dialog) {
                                    int type ;
                                    String buildType ;
                                    switch (position) {
                                        case 0:
                                            type= ConstantUtil.TYPE_DEBUG;
                                            buildType = "debug";
                                            break;
                                        case 1:
                                            type=ConstantUtil.TYPE_TEST;
                                            buildType = "customtest";
                                            break;
                                        case 2:
                                            type=ConstantUtil.TYPE_PRERELEASE;
                                            buildType = "prerelease";
                                            break;
                                        default:
                                            type=ConstantUtil.TYPE_DEBUG;
                                            buildType = "debug";
                                    }
                                    HttpUtils.buildType = buildType;
                                    MMKVUtils.save(ConstantUtil.HTTP_TYPE, type);
                                    MMKVUtils.save(ConstantUtil.USER_TOKEN, "");
                                    ToastUtils.showToast("切换成功");
                                }
                            });
                        }
                    } else {
                        mSecretNumber = 0;
                    }
                }
        );

    }

    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, AboutUsActivity.class);
            context.startActivity(intent);
        }
    }


}
