package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Created by XiongShaoWu
 * on 2019/9/12  信息详情界面数据模型
 */
public class InfoDetailsDemoBean {
    /**
     * 条目类型名
     */
    public String key;
    /**
     * 条目内容值
     */
    public ValueBean value;

    public String buttonText;

    /**
     * itemType类型
     * 0 文本输入条目 不可编辑
     * 1 文本输入条目 可编辑
     * 2 文本选择条目 选择设置 不可输入
     * 3 备注条目     多行输入
     * 4 照片选择
     * 5 文本输入条目 可编辑 带单位
     */
    public int itemType;
    /**
     * required 是否必填
     * true 必填
     * false 非必填
     */
    public boolean required;
    public boolean checkNumber;
    /**
     * inputType 输入类型
     * 0文本
     * 1整数
     * 2小数
     * 3英文数字文本
     */
    public int inputType;
    /**
     * hintText hint文本
     */
    public String hintText;
    /**
     * unit 可编辑输入框单位
     */
    public String unit;
    /**
     * maxLength 可编辑输入长度
     */
    public int maxLength = 300;

    /**
     * imageLimit 可选图片的张数
     */
    public int imageLimit = 5;

    /**
     * imageLimit 可选图片的张数
     */
    public boolean isShowUnit = false;

    /**
     * isDouble 是否限制小数格式
     */
    public boolean isDouble;
    /**
     * integerLength 整数位长度
     */
    public int integerLength;
    /**
     * doubleLength 小数位长度
     */
    public int doubleLength;

    public boolean enable = true;
    /**
     * 物料Id type=6 专用
     */
    public String publicMaterialId;
    /**
     * 物料名称 type=6 专用
     */
    public String publicMaterialName;
    /**
     * 物料使用量 type=6 专用
     */
    public String useCount;
    /**
     * 物料批次 type=6 专用
     */
    public String materialBatch;
    /**
     * 过期时间 type=6 专用
     */
    public String expireTime;
    /**
     * 物料单位
     */
    public String specificationUnitValue;
    /**
     * 物料 单位使用量
     */
    public double specificationNum;

    public int getButtonTextVisible() {
        if (TextUtils.isEmpty(buttonText) || value == null || TextUtils.isEmpty(value.valueStr)) {
            return View.GONE;
        }
        return View.VISIBLE;
    }


    public static class ValueBean {
        public String valueStr;
        public String valueId;

        public ValueBean(String valueStr, String valueId) {
            this.valueStr = valueStr;
            this.valueId = valueId;
        }

        public ValueBean() {

        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof InfoDetailsDemoBean)) {
            return false;
        } else {
            return TextUtils.equals(key, ((InfoDetailsDemoBean) obj).key);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public int getInputType() {
        switch (inputType) {
            case 0:
            case 3:
                return 0x00000001;
            case 1:
                return 0x00000002;
            case 2:
                return 0x00002002;
        }
        return inputType;
    }

    public int getRequiredVisible() {
        return required ? View.VISIBLE : View.GONE;
    }
}
