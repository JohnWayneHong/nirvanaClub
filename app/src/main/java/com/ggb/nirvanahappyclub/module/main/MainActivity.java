package com.ggb.nirvanahappyclub.module.main;

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

import com.ggb.common_library.base.ui.BaseActivity;
import com.ggb.common_library.utils.CommonDialogUtil;
import com.ggb.common_library.utils.FormatUtils;
import com.ggb.common_library.utils.MMKVUtils;
import com.ggb.common_library.utils.ToastUtils;
import com.ggb.common_library.utils.click_utils.ClickUtils;
import com.ggb.common_library.widget.commonDialog.CommonDialog;
import com.ggb.nirvanahappyclub.R;
import com.ggb.nirvanahappyclub.databinding.ActivityMainBinding;
import com.ggb.nirvanahappyclub.utils.CheckUpdateUtils;
import com.ggb.nirvanahappyclub.utils.ConstantUtil;



import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> {

    private int lastSelectTab = -1;
    private ArrayList<String> tags;
    private ArrayList<Fragment> fragments;

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


    }

    @Override
    public void initLiveData() {
        super.initLiveData();

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
