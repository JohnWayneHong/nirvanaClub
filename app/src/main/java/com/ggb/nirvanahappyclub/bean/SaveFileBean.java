package com.ggb.nirvanahappyclub.bean;

/**
 * 下载提示
 */
public class SaveFileBean {
    public String path;     //下载路径
    public String savePath; //保存路径
    public int code;        //0:失败 1:成功 -1:下载中
    public String msg;      //失败信息
    public int progress;    //下载进度

    public static SaveFileBean obtain(String path, String savePath, int code, String msg) {
        return obtain(path, savePath, code, msg, 0);
    }

    public static SaveFileBean obtain(String path, String savePath, int code, String msg, int progress) {
        SaveFileBean imageBean = new SaveFileBean();
        imageBean.path = path;
        imageBean.savePath = savePath;
        imageBean.code = code;
        imageBean.msg = msg;
        imageBean.progress = progress;
        return imageBean;
    }
}
