package com.ggb.nirvanahappyclub.network.result;

/**
 * 玩安卓的--返回類型
 */
public class WanAndroidHttpResult<T> {

    public int errorCode;
    public String errorMsg;
    //data泛型
    public T data;
}
