package com.jgw.delingha.custom_module.delingha.image;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jgw.common_library.base.CustomApplication;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;
import com.jgw.delingha.utils.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by XiongShaoWu
 * on 2019/10/18
 * 图片上传
 */
public class ImageUploadModel {
    public Observable<String> getImagesStr(@NonNull String uriStr) {
        if (TextUtils.isEmpty(uriStr)) {
            return null;
        }
        String[] split = uriStr.split(",");
        ArrayList<Observable<String>> observables = new ArrayList<>();
        for (String s : split) {
            observables.add(uploadImage(s));
        }
        return Observable.zip(observables, objects -> {
                    StringBuilder sb = new StringBuilder();
                    for (Object object : objects) {
                        sb.append(object);
                        sb.append(",");
                    }
                    return sb.toString();
                })
                .map(s -> TextUtils.isEmpty(s) ? s : s.substring(0, s.length() - 1))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());

    }

    private Observable<String> uploadImage(String uriStr) {
        Uri parse = Uri.parse(uriStr);
        File file = BitmapUtils.getRealPathFromUri(CustomApplication.getCustomApplicationContext(), parse);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型

        RequestBody imageBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        builder.addFormDataPart("file", file.getName(), imageBody);// "file"后台接收图片流的参数名

        List<MultipartBody.Part> parts = builder.build().parts();
        return HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .uploadFiles(parts, file.getName())
                .compose(HttpUtils.applyIOSchedulers());
    }
}
