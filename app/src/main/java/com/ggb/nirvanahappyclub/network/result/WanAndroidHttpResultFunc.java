package com.ggb.nirvanahappyclub.network.result;

import com.ggb.common_library.http.CustomHttpApiException;

import io.reactivex.functions.Function;

/**
 * Created by xsw on 2017/4/21.
 */

public class WanAndroidHttpResultFunc<T> implements Function<WanAndroidHttpResult<T>, T> {

    @Override
    public T apply(WanAndroidHttpResult<T> httpResult)  {
        //完成类型转换 通过请求码统一处理异常 并通过泛型返回数据
        switch (httpResult.errorCode) {
            case 0:
            case 200:
                if (httpResult.data ==null){
                    throw new CustomHttpApiException(200,httpResult.errorMsg);
                }
                break;
            case 401:
                throw new CustomHttpApiException(401,httpResult.errorMsg);
            default:
                throw new CustomHttpApiException(httpResult.errorCode,httpResult.errorMsg);
        }

        return httpResult.data;
    }
}
