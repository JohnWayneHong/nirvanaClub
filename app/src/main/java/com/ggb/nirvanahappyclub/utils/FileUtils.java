package com.ggb.nirvanahappyclub.utils;

import android.text.TextUtils;

import com.ggb.common_library.base.CustomApplication;
import com.ggb.common_library.http.rxjava.CustomObserver;
import com.ggb.common_library.utils.MMKVUtils;
import com.ggb.common_library.utils.ToastUtils;
import com.ggb.nirvanahappyclub.bean.SaveFileBean;
import com.ggb.nirvanahappyclub.common.AppConfig;
import com.ggb.nirvanahappyclub.network.HttpUtils;
import com.ggb.nirvanahappyclub.network.api.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by XiongShaoWu
 * on 2019/10/23
 */
public class FileUtils {

    /**
     * 获取文件文件夹
     */
    public static String getFileDirPath() {
        if (checkFileSavePath(AppConfig.FILE_PATH)) {
            return AppConfig.FILE_PATH;
        }
        return "";
    }

    /**
     * 获取日志文件文件夹
     */
    public static String getLogDirPath() {
        if (checkFileSavePath(AppConfig.LOG_PATH)) {
            return AppConfig.LOG_PATH;
        }
        return "";
    }

    /**
     * 获取图片文件夹
     */
    public static String getImageDirPath() {
        if (checkFileSavePath(AppConfig.IMAGE_PATH)) {
            return AppConfig.IMAGE_PATH;
        }
//        throw new FileNotFoundException("getFileDirPath exception");
        return "";
    }

    /**
     * 获取数据库临时文件夹
     */
    public static String getDBTempPath() {
        String path = CustomApplication.context.getFilesDir().getAbsolutePath() + "/databases/temp";
        if (checkFileSavePath(path)) {
            return path;
        }
//        throw new FileNotFoundException("getFileDirPath exception");
        return "";
    }

    public static boolean checkFileSavePath(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    /**
     * 下载文件
     *
     * @param absFilePathName    文件名
     * @param fileUrl            文件路径
     * @param needNotifyProgress 是否需要通知
     * @param subscriber         监听回调
     */
    public static void saveFileFromNet(final String absFilePathName, final String fileUrl, final boolean needNotifyProgress, CustomObserver<SaveFileBean> subscriber) {
        if (TextUtils.isEmpty(fileUrl)) {
            if (subscriber != null) {
                SaveFileBean saveFileBean = SaveFileBean.obtain(fileUrl, "", 0, "下载失败!");
                Observable.just(saveFileBean).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
            }
            ToastUtils.showToast("下载地址为空!");
            return;
        }
        HttpUtils.getGatewayApi(ApiService.class)
                .download(MMKVUtils.getString(ConstantUtil.USER_TOKEN), fileUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> response) {
                        InputStream is = null;
                        byte[] buf = new byte[10240];
                        int len;
                        FileOutputStream fos = null;
                        try {
                            ResponseBody body = response.body();
                            if (body == null) {
                                return;
                            }
                            long totalLength = body.contentLength();
//                            if (totalLength <= 0) {
//                                return;
//                            }
                            is = body.byteStream();
                            long tempLength = 0;
                            File file = new File(absFilePathName);
                            file.deleteOnExit();
                            fos = new FileOutputStream(file);
                            SaveFileBean downLoadingBean = null;
                            if (totalLength > 0) {
                                downLoadingBean = SaveFileBean.obtain(fileUrl, file.getAbsolutePath(), -1, "初始化...", 0);
                                Observable.just(downLoadingBean).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
                            }

                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                tempLength += len;
                                if (needNotifyProgress && subscriber != null) {
                                    int progress = (int) (tempLength * 100 / totalLength);
//                                    下载进度每5%通知一次
                                    if (totalLength > 0) {
                                        if (progress - downLoadingBean.progress >= 1) {
                                            downLoadingBean.progress = (int) progress;
                                            Observable.just(downLoadingBean).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
                                        }
                                    }
                                }

                            }
                            fos.flush();
                            if (subscriber != null) {
                                SaveFileBean saveFileBean = SaveFileBean.obtain(fileUrl, file.getAbsolutePath(), 1, "下载成功!", 100);
                                Observable.just(saveFileBean).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
                            }
                        } catch (Exception e) {
                            if (subscriber != null) {
                                SaveFileBean saveFileBean = SaveFileBean.obtain(fileUrl, "", 0, "下载失败!");
                                Observable.just(saveFileBean).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
                            }
                        } finally {
                            try {
                                if (is != null)
                                    is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (fos != null)
                                    fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public static void saveLocalDataToFile(String str, String path, String fileName) {
        String filePath = path + fileName;
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(str);//写入本地文件中
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
