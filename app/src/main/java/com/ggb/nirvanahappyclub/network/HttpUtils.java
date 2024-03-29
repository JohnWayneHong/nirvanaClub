package com.ggb.nirvanahappyclub.network;

import com.ggb.common_library.http.HttpClient;
import com.ggb.common_library.utils.MMKVUtils;
import com.ggb.nirvanahappyclub.BuildConfig;
import com.ggb.nirvanahappyclub.network.okhttp.OkHttpUtils;
import com.ggb.nirvanahappyclub.network.result.HttpResult;
import com.ggb.nirvanahappyclub.network.result.HttpResultFunc;
import com.ggb.nirvanahappyclub.network.result.HttpResultNullableFunc;
import com.ggb.nirvanahappyclub.utils.ConstantUtil;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HttpUtils {

    // 文件的url
    private static final String FILE_URL_RELEASE = "https://file.jgwcjm.com/";
    private static final String FILE_URL_TEST = "http://filetest.jgwcjm.com/";

    // 网关正式域名  预发布使用正式域名 通过拦截器添加Cookie链接预发布服务
    private static final String BASE_GATEWAY_URL_RELEASE = "https://nirvana1234.xyz/";
    // 网关测试域名
    private static final String BASE_GATEWAY_URL_TEST = "https://nirvana1234.xyz/";

    //玩安卓 网关域名
    private static final String WAN_ANDROID_GATEWAY_URL_TEST = "https://www.wanandroid.com/";

    //段子乐 网关域名
    private static final String JOKER_ANDROID_GATEWAY_URL_TEST = "http://tools.cretinzp.com/jokes/";


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
    public static String getWanAndroidGatewayUrl() {
        if (testRelease) {
            return WAN_ANDROID_GATEWAY_URL_TEST;
        }
        switch (buildType) {
            case "debug":
                return WAN_ANDROID_GATEWAY_URL_TEST;
            case "customtest":
                int current = MMKVUtils.getInt(ConstantUtil.HTTP_TYPE);
                return current == ConstantUtil.TYPE_PRERELEASE ? WAN_ANDROID_GATEWAY_URL_TEST : WAN_ANDROID_GATEWAY_URL_TEST;
            case "prerelease":
                return WAN_ANDROID_GATEWAY_URL_TEST;
            case "release":
                return WAN_ANDROID_GATEWAY_URL_TEST;
            default:
                return WAN_ANDROID_GATEWAY_URL_TEST;
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public static String getJokerAndroidGatewayUrl() {
        if (testRelease) {
            return JOKER_ANDROID_GATEWAY_URL_TEST;
        }
        switch (buildType) {
            case "debug":
                return JOKER_ANDROID_GATEWAY_URL_TEST;
            case "customtest":
                int current = MMKVUtils.getInt(ConstantUtil.HTTP_TYPE);
                return current == ConstantUtil.TYPE_PRERELEASE ? JOKER_ANDROID_GATEWAY_URL_TEST : JOKER_ANDROID_GATEWAY_URL_TEST;
            case "prerelease":
                return JOKER_ANDROID_GATEWAY_URL_TEST;
            case "release":
                return JOKER_ANDROID_GATEWAY_URL_TEST;
            default:
                return JOKER_ANDROID_GATEWAY_URL_TEST;
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
        MMKVUtils.save("http_type",1);
        return HttpClient.getApi(clz, getGatewayUrl(), OkHttpUtils.getOkHttpClient());
    }

    public static <T> T getWanAndroidGatewayApi(Class<T> clz) {
        MMKVUtils.save("http_type",2);
        return HttpClient.getApi(clz, getWanAndroidGatewayUrl(), OkHttpUtils.getOkHttpClient());
    }

    public static <T> T getJokerAndroidGatewayApi(Class<T> clz) {
        MMKVUtils.save("http_type",3);
        return HttpClient.getApi(clz, getJokerAndroidGatewayUrl(), OkHttpUtils.getOkHttpClient());
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
    public static <K> ObservableTransformer<HttpResult<K>, K> applyMainSchedulers() {
        return (ObservableTransformer<HttpResult<K>, K>) schedulersTransformer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new HttpResultFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    };

    @SuppressWarnings("unchecked")
    public static <K> ObservableTransformer<HttpResult<K>, K> applyResultNullableMainSchedulers() {
        return (ObservableTransformer<HttpResult<K>, K>) schedulersResultNullableTransformer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersResultNullableTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new HttpResultNullableFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    };

    @SuppressWarnings("unchecked")
    public static <K> ObservableTransformer<HttpResult<K>, K> applyResultNullableIOSchedulers() {
        return (ObservableTransformer<HttpResult<K>, K>) schedulersResultNullableIOTransformer;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersResultNullableIOTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new HttpResultNullableFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    };

    @SuppressWarnings("unchecked")
    public static <K> ObservableTransformer<HttpResult<K>, K> applyIOSchedulers() {
        return (ObservableTransformer<HttpResult<K>, K>) schedulersIOTransformer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ObservableTransformer schedulersIOTransformer = upstream -> {
//        List<String> objects = Collections.<String>emptyList();
        return upstream.map(new HttpResultFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    };
}
