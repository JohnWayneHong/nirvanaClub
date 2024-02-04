package com.ggb.nirvanahappyclub.network;

import com.ggb.common_library.http.HttpClient;
import com.ggb.common_library.utils.MMKVUtils;
import com.ggb.nirvanahappyclub.BuildConfig;
import com.ggb.nirvanahappyclub.network.okhttp.OkHttpUtils;
import com.ggb.nirvanahappyclub.network.result.JokerAndroidHttpResult;
import com.ggb.nirvanahappyclub.network.result.JokerAndroidHttpResultFunc;
import com.ggb.nirvanahappyclub.network.result.JokerAndroidHttpResultNullableFunc;
import com.ggb.nirvanahappyclub.utils.ConstantUtil;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class JokerAndroidHttpUtils {

    // 文件的url
    private static final String FILE_URL_RELEASE = "https://file.jgwcjm.com/";
    private static final String FILE_URL_TEST = "http://filetest.jgwcjm.com/";

    // 网关正式域名  预发布使用正式域名 通过拦截器添加Cookie链接预发布服务
    private static final String BASE_GATEWAY_URL_RELEASE = "https://nirvana1234.xyz/";
    // 网关测试域名
    private static final String BASE_GATEWAY_URL_TEST = "https://nirvana1234.xyz/";
    //开发环境域名(不稳定,和后台联调处理后再使用)
    private static final String BASE_GATEWAY_URL_DEBUG = "https://dev-api-gateway.cjm3.kf315.net/";

    private static final String CJM_BASE_GATEWAY_URL_RELEASE = "https://api-gateway.jgwcjm.com/";
    private static final String CJM_BASE_GATEWAY_URL_TEST = "https://api-gateway.cjm3.kf315.net/";
    private static final String CJM_BASE_GATEWAY_URL_DEBUG = "https://dev-api-gateway.cjm3.kf315.net/";

    private static final String MY_URL = "http://www.xionghaoyue.xyz:9999/";

    public static String buildType = BuildConfig.BUILD_TYPE;
    public static boolean testRelease = false;

    @SuppressWarnings({"DuplicateBranchesInSwitch"})
    public static String getGatewayUrl() {
        if (testRelease) {
            return BASE_GATEWAY_URL_RELEASE;
        }
        switch (buildType) {
            case "debug":
                return BASE_GATEWAY_URL_TEST;
            case "customtest":
                int current = MMKVUtils.getInt(ConstantUtil.HTTP_TYPE);
                return current == ConstantUtil.TYPE_PRERELEASE ? BASE_GATEWAY_URL_RELEASE : BASE_GATEWAY_URL_TEST;
            case "prerelease":
                return BASE_GATEWAY_URL_RELEASE;
            case "release":
                return BASE_GATEWAY_URL_RELEASE;
            default:
                return BASE_GATEWAY_URL_RELEASE;
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public static String getCJMGatewayUrl() {
        if (testRelease) {
            return CJM_BASE_GATEWAY_URL_RELEASE;
        }
        switch (buildType) {
            case "debug":
                return CJM_BASE_GATEWAY_URL_TEST;
            case "customtest":
                int current = MMKVUtils.getInt(ConstantUtil.HTTP_TYPE);
                return current == ConstantUtil.TYPE_PRERELEASE ? CJM_BASE_GATEWAY_URL_RELEASE : CJM_BASE_GATEWAY_URL_TEST;
            case "prerelease":
                return CJM_BASE_GATEWAY_URL_RELEASE;
            case "release":
                return CJM_BASE_GATEWAY_URL_RELEASE;
            default:
                return CJM_BASE_GATEWAY_URL_RELEASE;
        }
    }

    @SuppressWarnings({"DuplicateBranchesInSwitch"})
    public static String getFileUrl() {
        if (testRelease) {
            return FILE_URL_RELEASE;
        }
        switch (buildType) {
            case "debug":
            case "customtest":
                int current = MMKVUtils.getInt(ConstantUtil.HTTP_TYPE);
                return current == ConstantUtil.TYPE_PRERELEASE ? FILE_URL_RELEASE : FILE_URL_TEST;
            case "prerelease":
                return FILE_URL_RELEASE;
            case "release":
                return FILE_URL_RELEASE;
            default:
                return FILE_URL_RELEASE;
        }
    }

    public static String getMyUrl() {
        return MY_URL;
    }

    public static <T> T getGatewayApi(Class<T> clz) {
        return HttpClient.getApi(clz, getGatewayUrl(), OkHttpUtils.getOkHttpClient());
    }

    public static <T> T getGatewayApiBigFile(Class<T> clz) {
        return HttpClient.getApi(clz, getGatewayUrl(), OkHttpUtils.getLongTimeOkHttpClient());
    }

    public static <T> T getCJMGatewayApi(Class<T> clz) {
        return HttpClient.getApi(clz, getCJMGatewayUrl(), OkHttpUtils.getOkHttpClient());
    }

    public static <T> T getCJMGatewayApiBigFile(Class<T> clz) {
        return HttpClient.getApi(clz, getCJMGatewayUrl(), OkHttpUtils.getLongTimeOkHttpClient());
    }

    public static <T> T getMyApi(Class<T> clz) {
        return HttpClient.getApi(clz, getMyUrl(), OkHttpUtils.getLongTimeOkHttpClient());
    }

    @SuppressWarnings("unchecked")
    public static <K> ObservableTransformer<JokerAndroidHttpResult<K>, K> applyMainSchedulers() {
        return (ObservableTransformer<JokerAndroidHttpResult<K>, K>) schedulersTransformer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new JokerAndroidHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    };

    @SuppressWarnings("unchecked")
    public static <K> ObservableTransformer<JokerAndroidHttpResult<K>, K> applyResultNullableMainSchedulers() {
        return (ObservableTransformer<JokerAndroidHttpResult<K>, K>) schedulersResultNullableTransformer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersResultNullableTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new JokerAndroidHttpResultNullableFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    };

    @SuppressWarnings("unchecked")
    public static <K> ObservableTransformer<JokerAndroidHttpResult<K>, K> applyResultNullableIOSchedulers() {
        return (ObservableTransformer<JokerAndroidHttpResult<K>, K>) schedulersResultNullableIOTransformer;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersResultNullableIOTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new JokerAndroidHttpResultNullableFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    };

    @SuppressWarnings("unchecked")
    public static <K> ObservableTransformer<JokerAndroidHttpResult<K>, K> applyIOSchedulers() {
        return (ObservableTransformer<JokerAndroidHttpResult<K>, K>) schedulersIOTransformer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersIOTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new JokerAndroidHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    };
}
