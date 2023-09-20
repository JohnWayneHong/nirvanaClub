package com.jgw.delingha.bean;


import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.BR;
import com.jgw.delingha.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author : J-T
 * @date : 2022/7/21 13:58
 * description :扫码状态查询页面的Bean
 */
public class CodeStatusQueryDetailsDataBean extends BaseObservable {

    /**
     * 标签图标 顺序不能错误
     */
    private final ArrayList<Integer> icons;
    /**
     * 标签标题 顺序不能错误
     */
    private final ArrayList<String> titles;
    /**
     * 表示标签的选中状态
     */
    private final boolean[] selectArray = new boolean[]{false, false, false, false, false};
    public String code;

    public CodeStatusQueryDetailsDataBean() {
        icons = new ArrayList<>(Arrays.asList(R.drawable.icon_status_query_stock_in_small,
                R.drawable.icon_status_query_stock_out_small,
                R.drawable.icon_status_query_exchange_warehouse_small,
                R.drawable.icon_status_query_exchange_goods_small,
                R.drawable.icon_status_query_stock_return_small));
        titles = new ArrayList<>(Arrays.asList(ResourcesUtils.getString(R.string.code_status_query_stock_in_info),
                ResourcesUtils.getString(R.string.code_status_query_stock_out_info),
                ResourcesUtils.getString(R.string.code_status_query_exchange_warehouse_info),
                ResourcesUtils.getString(R.string.code_status_query_exchange_goods_info),
                ResourcesUtils.getString(R.string.code_status_query_stock_return_info)));
    }

    /**
     * 参数 0-4 对应 入库 出库 调仓 调货 退货
     */
    public void setSelect(int position) {
        for (int i = 0; i < selectArray.length; i++) {
            selectArray[i] = (i == position);
        }
        notifyPropertyChanged(BR.rvVisible);
        notifyPropertyChanged(BR.emptyLayoutVisible);
        notifyPropertyChanged(BR.detailInfoIcon);
        notifyPropertyChanged(BR.detailInfoText);
    }

    @Bindable
    public int getEmptyLayoutVisible() {
        for (Boolean b : selectArray) {
            if (b) {
                return View.INVISIBLE;
            }
        }
        return View.VISIBLE;
    }

    @Bindable
    public int getRvVisible() {
        for (Boolean b : selectArray) {
            if (b) {
                return View.VISIBLE;
            }
        }
        return View.INVISIBLE;
    }

    public String getCodeText() {
        return "身份码: " + code;
    }

    @Bindable
    public int getDetailInfoIcon() {
        for (int i = 0; i < selectArray.length; i++) {
            if (selectArray[i]) {
                return icons.get(i);
            }
        }
        return icons.get(0);
    }

    @Bindable
    public String getDetailInfoText() {
        for (int i = 0; i < selectArray.length; i++) {
            if (selectArray[i]) {
                return titles.get(i);
            }
        }
        return titles.get(0);
    }
}
