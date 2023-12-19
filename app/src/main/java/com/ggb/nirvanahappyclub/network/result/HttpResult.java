package com.ggb.nirvanahappyclub.network.result;

/**
 * Created by Xiongshaowu on 16/3/5.
 */
public class HttpResult<T> {

    public int state;
    public String message;
    public int code;
    //data泛型
    public T data;
    public boolean success;
}
