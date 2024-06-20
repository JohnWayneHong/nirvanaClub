package com.ggb.nirvanahappyclub.utils;

import android.content.Context;

import com.ggb.common_library.utils.json.JsonArray;
import com.ggb.common_library.utils.json.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConvertJsonFileUtils {

    // 读取assets目录下的json文件，并转换为JSONArray
    public static JsonArray readJsonFromAssets(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JsonUtils.parseArray(stringBuilder.toString());
    }


}
