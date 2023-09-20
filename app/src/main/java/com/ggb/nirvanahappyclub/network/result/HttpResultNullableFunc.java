package com.ggb.nirvanahappyclub.network.result;


import com.ggb.common_library.http.CustomHttpApiException;

import io.reactivex.functions.Function;

/**
 * Created by xsw on 2017/4/21.
 */

public class HttpResultNullableFunc implements Function<HttpResult<String>, String> {

    @Override
    public String apply(HttpResult<String> httpResult) throws Exception {
        //完成类型转换 通过请求码统一处理异常 并通过泛型返回数据
        switch (httpResult.state) {
            case 200:
                if (httpResult.results == null) {
                    httpResult.results = "";
                }
                break;
            case 401:
                throw new CustomHttpApiException(401, httpResult.msg);
            case 500:
                throw new CustomHttpApiException(500, httpResult.msg);
            default:
                throw new CustomHttpApiException(httpResult.state, httpResult.msg);
        }
        return httpResult.results;
    }
}
