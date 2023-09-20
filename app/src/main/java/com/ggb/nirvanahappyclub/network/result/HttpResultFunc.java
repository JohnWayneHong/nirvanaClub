package com.ggb.nirvanahappyclub.network.result;

import com.jgw.common_library.http.CustomHttpApiException;

import io.reactivex.functions.Function;

/**
 * Created by xsw on 2017/4/21.
 */

public class HttpResultFunc<T> implements Function<HttpResult<T>, T> {

    @Override
    public T apply(HttpResult<T> httpResult)  {
        //完成类型转换 通过请求码统一处理异常 并通过泛型返回数据
        switch (httpResult.state) {
            case 200:
                if (httpResult.results==null){
                    throw new CustomHttpApiException(200,httpResult.msg);
                }
                break;
            case 401:
                throw new CustomHttpApiException(401,httpResult.msg);
            default:
                throw new CustomHttpApiException(httpResult.state,httpResult.msg);
        }

        return httpResult.results;
    }
}
