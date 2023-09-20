package com.jgw.delingha.module.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.DevicesUtils;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.MainMenuBean;
import com.jgw.delingha.databinding.ActivityMainBinding;
import com.jgw.delingha.module.setting_center.ui.SettingCenterFragment;
import com.jgw.delingha.module.tools_table.ui.ToolsTableFragment;
import com.jgw.delingha.utils.CheckUpdateUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> {

    private int lastSelectTab = -1;
    private ArrayList<String> tags;
    private ArrayList<Fragment> fragments;
    private ToolsTableFragment mToolsTableFragment;
    private SettingCenterFragment mSettingCenterFragment;

    private long exitTime;
    public static MainActivity mMainActivityContext;

    @Override
    public void getSaveInstanceState(@NonNull Bundle savedInstanceState) {
        lastSelectTab = savedInstanceState.getInt("LastSelectTab");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("LastSelectTab", lastSelectTab);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMainActivityContext = this;
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;         // 屏幕宽度（像素）
            int height = dm.heightPixels;       // 屏幕高度（像素）
            float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
            int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
            // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
            int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
            int screenHeight = (int) (height / density);// 屏幕高度(dp)
            Log.d("h_bl", "屏幕宽度（像素）：" + width);
            Log.d("h_bl", "屏幕高度（像素）：" + height);
            Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
            Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
            Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
            Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int index = intent.getIntExtra("index", -1);
            if (index != -1) {
                jumpToFragment(index);
            }

            boolean exit = intent.getBooleanExtra("exit", false);
            if (exit) {
                finish();
            }
        }
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        mBindingView.setData(new MainMenuBean());
        Intent intent = getIntent();
        boolean exit = intent.getBooleanExtra("exit", false);
        if (exit) {
            finish();
        }
        tags = new ArrayList<>();
        String HOME_FRAGMENT_KEY = "mToolsTableFragment";
        tags.add(HOME_FRAGMENT_KEY);
        String SETTING_FRAGMENT_KEY = "mSettingCenterFragment";
        tags.add(SETTING_FRAGMENT_KEY);


        fragments = new ArrayList<>();
        if (mToolsTableFragment == null) {
            ToolsTableFragment fragmentByTag = (ToolsTableFragment) fm.findFragmentByTag(tags.get(0));
            if (fragmentByTag == null) {
                mToolsTableFragment = new ToolsTableFragment();
            } else {
                mToolsTableFragment = fragmentByTag;
            }
        }
        fragments.add(mToolsTableFragment);

        if (mSettingCenterFragment == null) {
            SettingCenterFragment fragmentByTag = (SettingCenterFragment) fm.findFragmentByTag(tags.get(1));
            if (fragmentByTag == null) {
                mSettingCenterFragment = new SettingCenterFragment();
            } else {
                mSettingCenterFragment = fragmentByTag;
            }
        }
        fragments.add(mSettingCenterFragment);


        int index = intent.getIntExtra("index", -1);
        if (index != -1) {
            jumpToFragment(index);
        } else {
            jumpToFragment(0);
        }

    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getSystemExpireTimeLiveData().observe(this, resource -> mToolsTableFragment.refreshSystemExpireTime());
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(findViewById(R.id.ll_tool_table))
                .addView(findViewById(R.id.ll_setting_center))
                .submit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckUpdateUtils.getVersionType(false, this);
        mViewModel.cleanEmptyBox();
        mViewModel.cleanEmptyOrder();
        mViewModel.checkUserInfoError();

        Map<String, Object> info = new HashMap<>();
        info.put("user_device_id", DevicesUtils.getDevicesSerialNumber());
        info.put("user_org", MMKVUtils.getString(ConstantUtil.ORGANIZATION_NAME));
        info.put("user_tel", MMKVUtils.getString(ConstantUtil.USER_MOBILE));
        MobclickAgent.onEventObject(this, "homepage_event", info);

        mViewModel.getSystemExpireTime();

        checkNeedChangePassword();
    }

    private void checkNeedChangePassword() {
        int i = MMKVUtils.getInt(ConstantUtil.NEED_CHANGE_PASSWORD, 0);
        String lastCheckDay = MMKVUtils.getString(ConstantUtil.TODAY_CHECK_NEED_CHANGE_PASSWORD);
        String today = FormatUtils.formatDate(new Date(), FormatUtils.DAY_TIME_PATTERN);
        if (i != 1 || TextUtils.equals(lastCheckDay, today)) {
            return;
        }
        CommonDialogUtil.showConfirmDialog(this, "提示", getString(R.string.strong_password_remind),
                "确定", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onRightClick() {
                        MMKVUtils.save(ConstantUtil.TODAY_CHECK_NEED_CHANGE_PASSWORD, today);
                    }
                });
    }


    //根据点击位置跳转不同Fragment
    public void jumpToFragment(int index) {
        mBindingView.getData().setIndex(index);
        if (lastSelectTab != -1 && lastSelectTab != index) {
            Fragment baseFragment = fragments.get(lastSelectTab);
            fm.beginTransaction().hide(baseFragment).commitAllowingStateLoss();
        }
        String tag = tags.get(index);
        if (!fragments.get(index).isAdded() && null == fm.findFragmentByTag(tag)) {
            fm.beginTransaction().add(R.id.fl_main_content, fragments.get(index), tag).addToBackStack(null).commitAllowingStateLoss();
        }
        fm.beginTransaction().show(fragments.get(index)).commitAllowingStateLoss();
        lastSelectTab = index;
        MMKVUtils.save("index", index);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_tool_table) {
            jumpToFragment(0);
        } else if (id == R.id.ll_setting_center) {
            jumpToFragment(1);
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showToast("再按一次退出");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    /**
     * 打开或者回到首页方法
     *
     * @param context
     * @param index   0首页  1我的
     */
    public static void start(Context context, int index) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("index", index);
            context.startActivity(intent);
        }
    }

    public static void start(Context context, int index, Class<? extends Activity> clazz) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("isJump", 1);
            intent.putExtra("clazz", clazz);
            context.startActivity(intent);

        }
    }
}
