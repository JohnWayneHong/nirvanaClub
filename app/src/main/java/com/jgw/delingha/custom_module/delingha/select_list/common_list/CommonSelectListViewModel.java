package com.jgw.delingha.custom_module.delingha.select_list.common_list;



import static com.jgw.delingha.custom_module.delingha.select_list.common_list.CommonSelectListActivity.*;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class CommonSelectListViewModel extends BaseSelectItemListViewModel {

    private final CommonSelectListModel model;
    private String mId;

    public CommonSelectListViewModel(@NonNull Application application) {
        super(application);
        model = new CommonSelectListModel();
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getList(mSearchStr,mId);
    }

    public String switchTitle(String type) {
        String title;
        switch (type) {
            case SOW_PIG_STATUS:
                title = "母猪状态";
                break;
            case PREGNANCY_RESULT:
                title = "母猪妊检结果";
                break;
            case PREGNANCY_WAY:
                title = "妊检方式";
                break;
            case IN_TYPE:
                title = "进场类型";
                break;
            case SOW_PRODUCTION_EVENT:
                title = "母猪生产事件";
                break;
            case SOW_BREEDING_METHOD:
                title = "配置方式";
                break;
            case PORKER_PIG_STATUS:
                title = "肉猪状态";
                break;
            case FORMULA_TYPE:
                title = "配方类型";
                break;
            case USAGE_TYPE:
                title = "使用方式";
                break;
            case ROLLOVER_REASON:
                title = "转栏原因";
                break;
            case SOW_PIG_STATUS_PLAN:
                title = "母猪养殖方案状态";
                break;
            case TASK_TYPE:
                title = "标准养殖方案任务类型枚举";
                break;
            case RESPIRATORY_DISEASE:
                title = "呼吸疾病";
                break;
            case SKIN_DISEASE:
                title = "皮肤病";
                break;
            case BACK_FAT_MEASUREMENT_METHOD:
                title = "背膘测量方式";
                break;
            case LEAVE_TYPE:
            case BREED_OUT_TYPE:
                title = "离场类型";
                break;
            case BREED_TASK_TYPE:
                title = "任务";
                break;
            case ANIMALS_CLASSIFICATION:
                title = "对象";
                break;
            default:
                title = "";
        }
        return title;
    }
}
