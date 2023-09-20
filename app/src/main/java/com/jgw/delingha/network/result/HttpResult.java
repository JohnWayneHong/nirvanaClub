package com.jgw.delingha.network.result;

/**
 * Created by Xiongshaowu on 16/3/5.
 */
public class HttpResult<T> {

    public int state;
    public String msg;
    public String internalErrorCode;
    //data泛型
    public T results;
}
