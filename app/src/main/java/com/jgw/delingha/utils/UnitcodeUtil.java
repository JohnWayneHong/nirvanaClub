package com.jgw.delingha.utils;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.HashMap;

/**
 * @Author chenguanyu
 * @Description 单位编码和名称的关系
 * @Date 2019/3/28
 **/
public class UnitcodeUtil {

    private static HashMap<String, String> unitcode;

    static {
        unitcode = new HashMap<>();
        unitcode.put("017001", "单品<--关联-->盒");
        unitcode.put("017002", "盒<--关联-->箱");
        unitcode.put("023001", "单个拆解");
        unitcode.put("023002", "整组拆解");
        unitcode.put("018101", "个");
        unitcode.put("018102", "盒");
        unitcode.put("018103", "瓶");
        unitcode.put("018104", "袋");
        unitcode.put("018105", "件");
        unitcode.put("018106", "包");
        unitcode.put("018107", "支");
        unitcode.put("018108", "罐");
        unitcode.put("018109", "套");
        unitcode.put("018110", "只");
        unitcode.put("018111", "听");
        unitcode.put("018201", "盒");
        unitcode.put("018202", "件");
        unitcode.put("018203", "组");
        unitcode.put("018204", "包");
        unitcode.put("018205", "箱");
        unitcode.put("018301", "箱");
        unitcode.put("018302", "垛");
        unitcode.put("018303", "托");
        unitcode.put("019001", "元(￥)");
        unitcode.put("019002", "美元($)");
        unitcode.put("032001", "发货出库");
        unitcode.put("032002", "调仓出库");
        unitcode.put("032003", "使用出库");
        unitcode.put("032004", "调货出库");
        unitcode.put("032005", "销售出库");
        unitcode.put("032006", "报损出库");
        unitcode.put("032007", "退货出库");
        unitcode.put("032008", "替换出库");
        unitcode.put("032009", "重置出库");
        unitcode.put("032010", "自动出库");
        unitcode.put("032011", "拆解出库");
        unitcode.put("032101", "生产入库");
        unitcode.put("032102", "收货入库");
        unitcode.put("032103", "盘点入库");
        unitcode.put("032104", "其他入库");
        unitcode.put("032105", "采购入库");
        unitcode.put("032106", "退货入库");
        unitcode.put("032107", "自动入库");
        unitcode.put("032108", "调仓入库");
        unitcode.put("032109", "调货入库");
        unitcode.put("032110", "替换入库");
        unitcode.put("032111", "重置入库");
        unitcode.put("032112", "装箱入库");
        unitcode.put("032113", "拆箱入库");
        unitcode.put("030001", "PC录入");
        unitcode.put("030002", "PDA录入");
        unitcode.put("030003", "APP录入");
        unitcode.put("030004", "产线录入");
        unitcode.put("040001", "退货");
        unitcode.put("050001", "调货");
        unitcode.put("060001", "调仓");
    }

    public static String getName(@NonNull String code) {
        return unitcode.get(code);
    }

    public static int getLevel(@NonNull String code) {
        String name = unitcode.get(code);
        if (TextUtils.isEmpty(name)) {
            return -1;
        }
        if (!code.contains("018")) {
            // 当前单位编号: code 不存在层级关系
            return -1;
        }
        return Integer.parseInt(code.charAt(3) + "");
    }

    // 根据第三位判断层级关系
    public static int level(String code) {
        return Integer.parseInt(code.charAt(3) + "");
    }

}
