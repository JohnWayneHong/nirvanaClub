package com.jgw.newsupercodepda;


import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.ggb.common_library.utils.json.JsonObject;
import com.ggb.common_library.utils.json.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonConverterTest {

    @Test
    public void testConvertJson() throws  IOException {
        String inputFilePath = "../CET4luan_2.json"; // 输入文件路径
        String outputFilePath = "../CET4luan_2_output.json"; // 输出文件路径
        System.out.println("开始转换 ");

        //1272 1363 64 65

        // 读取输入文件并解析为JSONObject列表
        List<JSONObject> jsonObjectList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(inputFilePath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {

                JSONObject jsonObject = new JSONObject(line);
                jsonObjectList.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        System.out.println("读取完成--大小 "+ jsonObjectList.size());

        // 将JSONObject列表转换为JSONArray
        JSONArray jsonArray = new JSONArray(jsonObjectList);

        // 将JSONArray写入输出文件
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFilePath)))) {
            bw.write(jsonArray.toString(4)); // 格式化输出
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        System.out.println("转换完成，结果已写入 " + outputFilePath);
    }

}

