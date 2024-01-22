package com.jgw.newsupercodepda;




import com.ggb.common_library.http.HttpClient;
import com.ggb.nirvanahappyclub.BuildConfig;
import com.ggb.nirvanahappyclub.common.AppConfig;
import com.ggb.nirvanahappyclub.network.result.HttpResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ReleasePublishTest {

    @Test
    public void testReleasePublish() throws JSONException {
        if (BuildConfig.DEBUG) {
            throw new RuntimeException("test version must is release!");
        }
        String pathname = "../apk";
        File file = new File(pathname);
        boolean directory = file.isDirectory();
        if (!directory) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        File apk = null;
        String testName = BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE + "_nomalRelease.apk";
        for (File f : files) {
            String name = f.getName();
            if (name.equals(testName)) {
                apk = f;
                break;
            }
        }
        if (apk == null) {
            throw new RuntimeException(testName + " not exist");
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        HashMap<String, Object> map = new HashMap<>();
        map.put("check", true);
        map.put("extranet", true);
        map.put("businessType", 1);
        map.put("fileName", apk.getName());
        map.put("size", apk.length());
        map.put("md5", calculateMD5(apk));

        System.out.println("map=" + new JSONObject(map));

        HttpResult<String> result = HttpClient.getApi(UploadServices.class, "https://api-gateway.jgwcjm.com/", client)
                .getUploadInfo(map)
                .blockingSingle();

        if (result == null || result.state != 200) {
            return;
        }
        System.out.println("result=" + result.data);

        JSONObject jb = new JSONObject(result.data);
        JSONObject formBody = jb.getJSONObject("formBody");
        String fullPath = formBody.getString("key");
        JSONObject headers = jb.getJSONObject("headers");
        Map<String, Object> formBodyMap = new HashMap<>();
        Map<String, Object> headersMap = new HashMap<>();

        for (Iterator<String> it = formBody.keys(); it.hasNext(); ) {
            String key = it.next();
            formBodyMap.put(key, formBody.get(key));
        }
        for (Iterator<String> it = headers.keys(); it.hasNext(); ) {
            String key = it.next();
            headersMap.put(key, headers.get(key));
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : formBodyMap.entrySet()) {
            String value = String.valueOf(entry.getValue());
            if ("${file}".equals(value)) {
                builder.addFormDataPart(entry.getKey(), apk.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), apk));
            } else {
                builder.addFormDataPart(entry.getKey(), value);
            }
        }
        String url = jb.getString("url");
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method("POST", builder.build());
        for (Map.Entry<String, Object> entry : headersMap.entrySet()) {
            requestBuilder.header(entry.getKey(), String.valueOf(entry.getValue()));
        }
        Request request = requestBuilder.build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!response.isSuccessful()) {
            throw new RuntimeException("apk upload error");
        }
        System.out.println("正式环境fullPath=" + fullPath);
    }

    @Test
    public void testPublish() {
        String fullPath = "master/1/202401161928422291747219319659864065";
        //noinspection ConstantConditions
        if (fullPath == null || fullPath.equals("")) {
            throw new RuntimeException("fullPath must not empty!");
        }
        String updateDescription = "收购需求发布";
        if (updateDescription == null || updateDescription.equals("")) {
            throw new RuntimeException("updateDescription must not empty!");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("forceUpdate", 1);
        map.put("lastestVersion", BuildConfig.VERSION_NAME);
        map.put("lastestVersionCode", BuildConfig.VERSION_CODE);
        map.put("title", AppConfig.APP_NAME);
        map.put("type", AppConfig.CURRENT_VERSION);
        map.put("updateDescription", updateDescription);
        map.put("upgradeUrl", "https://file.jgwcjm.com/" + fullPath);
        HttpResult<String> result = HttpClient.getApi(UploadServices.class, "https://api-gateway.jgwcjm.com/", new OkHttpClient().newBuilder()
                        .build())
                .postPublish(map)
                .blockingSingle();

        if (result == null || result.state != 200) {
            return;
        }
        System.out.println("发布完成");
    }

    public static String calculateMD5(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);

            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            fis.close();

            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface UploadServices {

        @GET("hydra-open-thirdpart-service/hydra-open-third-party/api/v1/hydra-file/upload")
        Observable<HttpResult<String>> getUploadInfo(@QueryMap Map<String, Object> map);

        @POST("hydra-mobile-terminal/api/v1/common/version")
        Observable<HttpResult<String>> postPublish(@Body Map<String, Object> map);
    }
}