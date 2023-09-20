package com.jgw.delingha.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2019/11/19
 */
public class ToolsOptionsBean {

    public String menuName;
    public List<MobileFunsBean> mobileFuns;

    public static class MobileFunsBean {
        public String appAuthCode;
        public String funName;
        public String icon;
        public int redirectType;

        public String getIconUrl(int width) {
            return HttpUtils.getFileUrl() + icon + AppConfig.getCustomWidthImage(width);
        }
    }

    public boolean isShow() {
        if (mobileFuns == null || mobileFuns.isEmpty()) {
            return false;
        }
        for (int i = 0; i < mobileFuns.size(); i++) {
            if (mobileFuns.get(i).redirectType == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ToolsOptionsBean)) {
            return false;
        }

        return TextUtils.equals(menuName, ((ToolsOptionsBean) obj).menuName)
                && TextUtils.equals(JsonUtils.toJsonString(mobileFuns), JsonUtils.toJsonString(((ToolsOptionsBean) obj).mobileFuns));
    }

}
