package com.ggb.nirvanahappyclub.network.result;

import com.ggb.common_library.http.CustomHttpApiException;

import io.reactivex.functions.Function;

/**
 * Created by xsw on 2017/4/21.
 */

public class JokerAndroidHttpResultFunc<T> implements Function<JokerAndroidHttpResult<T>, T> {

    @Override
    public T apply(JokerAndroidHttpResult<T> httpResult)  {
        //完成类型转换 通过请求码统一处理异常 并通过泛型返回数据
        switch (httpResult.code) {
            case 0:
            case 200:
                if (httpResult.data ==null){
                    throw new CustomHttpApiException(200,httpResult.msg);
                }
                break;
            case 401:
                throw new CustomHttpApiException(401,httpResult.msg);
            default:
                throw new CustomHttpApiException(httpResult.code,httpResult.msg);
        }

        return httpResult.data;
    }
}
