package com.ggb.nirvanahappyclub.network.okhttp;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ggb.common_library.utils.BuildConfigUtils;
import com.ggb.common_library.utils.DevicesUtils;
import com.ggb.common_library.utils.LogUtils;
import com.ggb.common_library.utils.MMKVUtils;
import com.ggb.nirvanahappyclub.utils.ConstantUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomInterceptor implements Interceptor {

    public CustomInterceptor() {
    }

    @NotNull
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        String token = MMKVUtils.getString(ConstantUtil.USER_TOKEN);
        if (!TextUtils.isEmpty(token)) {
            String versionName = BuildConfigUtils.getVersionName();
            builder.header("super-token", token)
                    .addHeader("service-auth-key", "b8379aa226f415c7fd71dc6281a1b7ba45eb6b42710cea6092c0a4d108f5088e")
                    .addHeader("versionType", MMKVUtils.getInt("version_type", -1) + "")
                    .addHeader("username", MMKVUtils.getString("user_mobile"))
                    .addHeader("devicesUUID", DevicesUtils.getDevicesSerialNumber())
                    .addHeader("versionCode", BuildConfigUtils.getVersionCode() + "")
                    .addHeader("versionName", TextUtils.isEmpty(versionName) ? "" : versionName);
        }
        int httpType = MMKVUtils.getInt("http_type");
        if (httpType == 3) {
            builder.addHeader("project_token", "806664062DCE4891B4C9650093DE5361");
            builder.addHeader("channel", "cretin_open_api");
            builder.addHeader("token", "cretin_open_api");
            builder.addHeader("uk", "cretin_open_api");
            builder.addHeader("app", "cretin_open_api");
            builder.addHeader("device", "IPHONE 15 PRO MAX");
        }


//        request = builder.addHeader("from", "pda").build();
        request = builder.addHeader("BEAR_ID", "581ce16d-ba89-45c0-9e15-073397d7a0b9").build();
        LogUtils.xswShowLog("token=" + token);
        Response response = chain.proceed(request);
        return response;
    }
}

