package com.ggb.nirvanahappyclub.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.ggb.common_library.base.ui.BaseActivity;
import com.ggb.common_library.http.rxjava.CustomObserver;
import com.ggb.common_library.provider.CameraFileProvider;
import com.ggb.common_library.utils.BuildConfigUtils;
import com.ggb.common_library.utils.CommonDialogUtil;
import com.ggb.common_library.utils.LogUtils;
import com.ggb.common_library.utils.MMKVUtils;
import com.ggb.common_library.utils.NetUtils;
import com.ggb.common_library.utils.ToastUtils;
import com.ggb.common_library.utils.json.JsonUtils;
import com.ggb.common_library.widget.commonDialog.CommonDialog;
import com.ggb.nirvanahappyclub.R;
import com.ggb.nirvanahappyclub.bean.SaveFileBean;
import com.ggb.nirvanahappyclub.bean.VersionBean;
import com.ggb.nirvanahappyclub.network.HttpUtils;
import com.ggb.nirvanahappyclub.network.api.ApiService;
import com.ggb.nirvanahappyclub.view.dialog.ApkUpdateDialog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by hwj
 * on 2024-1-8 15:00:15
 * 检测升级工具类
 */
public class CheckUpdateUtils implements View.OnClickListener {

    private static ApkUpdateDialog dialog;
    private String mApkUrl;
    private CustomObserver<SaveFileBean> subscriber;
    private TextView tv_dialog_confirm;
    private TextView tv_dialog_cancel;
    private ProgressBar pb_update_progress;
    private Activity mActivity;


    public void showUpdateDialog(FragmentActivity activity, boolean type, String apkUrl,VersionBean dialogBean) {
        mActivity = activity;
        mApkUrl = apkUrl;
        if (dialog !=null && dialog.isShowing()){
            return;
        }
        if (BaseActivity.isActivityNotFinished(activity)) {
            //这里传的style是根据接口返回样式进行修改
            String tempVersion = JsonUtils.parseObject(dialogBean.message).getString("updateStyle");
            int style = Integer.parseInt(tempVersion);

            dialog = new ApkUpdateDialog(activity, style);
            dialog.setOwnerActivity(activity);

            subscriber = new CustomObserver<SaveFileBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    super.onSubscribe(d);
                }

                @Override
                public void onNext(SaveFileBean saveFileBean) {
                    if (saveFileBean.code == 1) {
                        dispose();
                        tv_dialog_confirm.setEnabled(true);
                        pb_update_progress.setVisibility(View.GONE);
                        tv_dialog_confirm.setText("立即安装");
                        tv_dialog_cancel.setText("下载完成");

                        if (type || !dialog.isShowing()) {
                            dismissDialog();
                            installApk(activity, saveFileBean.savePath);
                        } else {
                            tv_dialog_confirm.setOnClickListener(v -> {
                                dismissDialog();
                                installApk(activity, saveFileBean.savePath);
                            });
                        }
                    } else if (saveFileBean.code == -1) {
                        tv_dialog_confirm = dialog.getUpdateDialogBinding().getRoot().findViewById(R.id.tv_update_new_confirm);
                        tv_dialog_cancel = dialog.getUpdateDialogBinding().getRoot().findViewById(R.id.tv_update_new_cancel);
                        pb_update_progress = dialog.getUpdateDialogBinding().getRoot().findViewById(R.id.pb_update_progress);
                        tv_dialog_confirm.setText("下载进度:" + saveFileBean.progress + "%");
                        pb_update_progress.setVisibility(View.VISIBLE);
                        pb_update_progress.setProgress(saveFileBean.progress);
                        LogUtils.xswShowLog("下载进度:" + saveFileBean.progress + "%");
                    } else if (saveFileBean.code == 0) {
                        ToastUtils.showToast(saveFileBean.msg);
                        tv_dialog_confirm.setEnabled(true);
                        dismissDialog();
                    }
                }
            };
            dialog.showUpdate(dialogBean);
            dialog.setOnApkDownloadConfirmListener(new ApkUpdateDialog.OnApkDownloadConfirmListener() {
                @Override
                public void onConfirmDownload(@NonNull VersionBean info) {
                    XXPermissions.with(dialog.getContext())
                            .permission(Permission.REQUEST_INSTALL_PACKAGES)
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (!all) {
                                        return;
                                    }
                                    startUpdate();
                                }

                                @Override
                                public void onDenied(List<String> permissions, boolean never) {
                                    CommonDialogUtil.showSelectDialog(dialog.getContext(), "权限不足", "系统权限不足,无法正常使用,是否前往系统设置?",
                                            "取消", "设置", new CommonDialog.OnButtonClickListener() {
                                                @Override
                                                public void onRightClick() {
                                                    XXPermissions.startPermissionActivity(dialog.getContext(), permissions);
                                                }
                                            });
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        if (id == R.id.tv_update_cancel) {
//            dismissDialog();
//        }
    }

    public void startUpdate() {
        realStartUpdate();
    }

    private void realStartUpdate() {
        String localFilePath = FileUtils.getFileDirPath() + "app_upgrade.apk";
        FileUtils.saveFileFromNet(localFilePath, mApkUrl, true, subscriber);
        tv_dialog_confirm.setEnabled(false);
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, String apkFilePath) {
        File apkFile = new File(apkFilePath);
        if (!apkFile.exists()) {
            ToastUtils.showToast("安装文件不存在!");
            return;
        }
        if (!isApkCanInstall(context, apkFilePath)) {
            ToastUtils.showToast("安装失败!");
            apkFile.deleteOnExit();
            return;
        }

        if (TextUtils.isEmpty(apkFilePath))
            return;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            7.0以上
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            uri = CameraFileProvider.getUriForFile(context, CameraFileProvider.getProviderName(context), apkFile);
        } else {
            uri = Uri.fromFile(new File(apkFilePath));
        }
        i.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(i);

        //升级后重新登录
        MMKVUtils.save("index", 0);
        MMKVUtils.save(ConstantUtil.USER_TOKEN, "");
    }

    /**
     * 检查apk安装包是否可以安装
     */
    public static boolean isApkCanInstall(Context context, String apkFilePath) {
        try {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo info = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
                return info != null;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public static void getVersionType(boolean showDialog, FragmentActivity activity) {
        if (!BaseActivity.isActivityNotFinished(activity)) {
            //Activity finished
            return;
        }
       HttpUtils.getGatewayApi(ApiService.class)
                        .getVersion()
                        .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<VersionBean>() {
                    @Override
                    public void onNext(VersionBean versionBean) {
                        LogUtils.xswShowLog("当前版本是===》"+BuildConfigUtils.getVersionCode());
                        LogUtils.xswShowLog("最新版本是===》"+versionBean.versionCode);
                        if (BuildConfigUtils.getVersionCode() >= versionBean.versionCode) {
                            if (showDialog) {
                                ToastUtils.showToast("当前已经是最新版");
                            }
                            MMKVUtils.save(ConstantUtil.NUMBER_OF_REMINDERS, 0);
                            return;
                        }
                        //牛蛙呐定制修改 改为增加标记是否强更判断
                        if (showDialog || versionBean.isForce == 1) {
                            showUpdateDialog(versionBean.forceUpdate == 1, versionBean.downloadUrl, activity,versionBean);
                            return;
                        }
                        //版本有变化开启提醒
                        int last_version_code = MMKVUtils.getInt(ConstantUtil.LAST_CHECK_VERSION_CODE);
                        if (last_version_code != versionBean.versionCode) {
                            showUpdateDialog(false, versionBean.downloadUrl, activity,versionBean);
                            MMKVUtils.save(ConstantUtil.LAST_CHECK_VERSION_CODE, versionBean.versionCode);
                            return;
                        }
                        //超过24小时继续开启提醒
                        long last_version_time = MMKVUtils.getLong(ConstantUtil.LAST_CHECK_VERSION_TIME);
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - last_version_time > 1000 * 60 * 60 * 24) {
                            MMKVUtils.save(ConstantUtil.NUMBER_OF_REMINDERS, 0);
                        }
                        //记录上次提醒时间 24小时内提醒不超过3次
                        long number_of_reminders = MMKVUtils.getLong(ConstantUtil.NUMBER_OF_REMINDERS);
                        if (number_of_reminders >= 3) {
                            return;
                        }
                        MMKVUtils.save(ConstantUtil.LAST_CHECK_VERSION_TIME, currentTime);
                        MMKVUtils.save(ConstantUtil.NUMBER_OF_REMINDERS, ++number_of_reminders);
                        showUpdateDialog(false, versionBean.downloadUrl, activity,versionBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            return;
                        }
                        super.onError(e);
                    }
                });
    }

    private static void showUpdateDialog(boolean type, String resourceUrl, FragmentActivity activity,VersionBean dialogBean) {
        new CheckUpdateUtils().showUpdateDialog(activity, type, resourceUrl,dialogBean);
    }

    public static boolean isShowDialog() {
        return dialog != null && dialog.isShowing();
    }

}
