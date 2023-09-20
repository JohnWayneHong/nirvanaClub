package com.jgw.delingha.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.jgw.common_library.provider.CameraFileProvider;

import java.io.File;
import java.io.IOException;

/**
 * 拍照 选照片工具类
 * Created by XiongShaoWu
 * on 2019/10/9
 */
public class CameraGalleryUtils {
    public static final int MY_PERMISSION_REQUEST_CODE = 100;
    public static final int START_CAMERA = 10;
    public static final int CHOOSE_PHOTO = 109;
    public static String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    /**
     * 打开相机并裁剪上传
     */
    public static Uri startCamera(Activity activity) {
        File outputImage = new File(activity.getExternalCacheDir(), "output_image"+System.currentTimeMillis()+".png");
        Uri cameraFileUri;
        try {
            if (outputImage.exists()) {
                boolean delete = outputImage.delete();
            }
            boolean newFile = outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            7.0以上
            cameraFileUri = CameraFileProvider.getUriForFile(activity, CameraFileProvider.getProviderName(activity), outputImage);
        } else {
            cameraFileUri = Uri.fromFile(new File(outputImage.toURI()));
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        activity.startActivityForResult(intent, START_CAMERA);
        return cameraFileUri;
    }


    /**
     * 打开图库
     */
    public static void startAlbum(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }
}
