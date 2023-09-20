package com.jgw.delingha.custom_module.delingha.image_preview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.delingha.bean.ImageBean;
import com.jgw.delingha.databinding.ActivityImagePreviewBinding;

/**
 * Created by XiongShaoWu
 * on 2019/10/21
 * 图片预览
 */
public class ImagePreviewActivity extends BaseActivity<ImagePreviewViewModel, ActivityImagePreviewBinding> {

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle("图片预览");
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        ImageBean data = null;
        if (!TextUtils.isEmpty(uri)) {
            data = new ImageBean(uri);
        }
        String url = intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            data = new ImageBean(url);
        }
        mBindingView.setData(data);
    }

    public static void start(Context context, @NonNull Uri uri) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ImagePreviewActivity.class);
            intent.putExtra("uri", uri.toString());
            context.startActivity(intent);
        }
    }

    public static void start(Context context, @NonNull String url) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ImagePreviewActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
        }
    }
}
