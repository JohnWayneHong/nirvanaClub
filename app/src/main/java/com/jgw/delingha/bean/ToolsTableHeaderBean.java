package com.jgw.delingha.bean;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2019/11/19
 */
public class ToolsTableHeaderBean {
    public String appName;
    public String mobile;
    public String systemName;
    public String companyName;
    public String companyIcon;
    public int companyNomalIcon;
    public String version;
    public String versionType;

    public String getCompanyIcon() {
        if (TextUtils.isEmpty(companyIcon)) {
            return companyIcon;
        }
        String baseUrl = HttpUtils.getFileUrl();
        return baseUrl + companyIcon;
    }

    public int packagingTodayNumber;
    public int stockInTodayNumber;
    public int stockOutTodayNumber;

    public String getPackagingTodayNumber() {
        return String.valueOf(packagingTodayNumber);
    }

    public String getStockInTodayNumber() {
        return String.valueOf(stockInTodayNumber);
    }

    public String getStockOutTodayNumber() {
        return String.valueOf(stockOutTodayNumber);
    }

    public int getSystemRenewalReminderVisible() {
        long time = MMKVUtils.getLong(ConstantUtil.SYSTEM_EXPIRE_TIME);
        long distance = time - System.currentTimeMillis();
        if (time == -1) {
            return View.GONE;
        }
        if (distance > 1000 * 60 * 60 * 24 * 30L) {
            return View.GONE;
        }
        return View.VISIBLE;
    }

    public List<CharSequence> getSystemRenewalReminderText() {
        long time = MMKVUtils.getLong(ConstantUtil.SYSTEM_EXPIRE_TIME);
        long distance = time - System.currentTimeMillis();

        long day = distance / (1000 * 60 * 60 * 24);
        long hour = distance % (1000 * 60 * 60 * 24) / (1000 * 60 * 60);
        day = day < 0 ? 0 : day;
        hour = hour < 0 ? 0 : hour;
        List<CharSequence> list = new ArrayList<CharSequence>();
        if (distance < 0) {
            list.add("您的系统已过期，将影响您的正常使用，请");
            list.add("尽快续费延期！");
        } else {
            Spanned spanned = Html.fromHtml("您的系统<font color='#03A9F4'>" + day + "</font>天" +
                    "<font color='#03A9F4'>" + hour + "</font>小时后到期，延期请拨打电话：");
            list.add(spanned);
            list.add("4006-822-110");
        }
        return list;
    }
}
