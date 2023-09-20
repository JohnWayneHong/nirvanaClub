package com.jgw.delingha.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.delingha.BR;

import java.util.Map;

/**
 * @author : J-T
 * @date : 2022/2/23 9:11
 * description : 通用包装反扫类 数量
 */
public class CommonPackageScanBackCount extends BaseObservable {
    @Bindable
    private int boxCount = 0;
    @Bindable
    private int childCount = 0;

    public String getBoxCount() {
        return "共" + boxCount + "箱";
    }

    public void setBoxCount(int boxCount) {
        this.boxCount = boxCount;
        notifyPropertyChanged(BR.boxCount);
    }

    public String getChildCount() {
        return "共" + childCount + "个";
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
        notifyPropertyChanged(BR.childCount);
    }

    public void setData(Map<String, Integer> map) {
        if (map == null) {
            return;
        }
        Integer boxCount = map.get("boxCount");
        if (boxCount != null) {
            setBoxCount(boxCount);
        }
        Integer childCount = map.get("childCount");
        if (childCount != null) {
            setChildCount(childCount);
        }
    }
}
