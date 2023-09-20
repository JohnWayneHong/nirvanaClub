package com.jgw.delingha.module.fail_log.viewmodel;

import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_EXCHANGE_GOODS;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_EXCHANGE_WAREHOUSE;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_IN_WAREHOUSE_PACKAGE;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_LABEL_EDIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_PACKAGE_STOCK_IN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_PACKAGING_ASSOCIATION;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_RELATE_TO_NFC;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_GROUP_SPLIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_IN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_OUT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_RETURN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_SINGLE_SPLIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FailLogBean;
import com.jgw.delingha.bean.FailLogListParamsToRequestBean;
import com.jgw.delingha.module.fail_log.adapter.FailLogListRecyclerAdapter;
import com.jgw.delingha.module.fail_log.model.FailLogListModel;

import java.util.List;

public class FailLogListViewModel extends BaseViewModel {


    private int mLogType;
    private String mHouseList;
    private final FailLogListModel model;
    private int mCurrentPage = 1;
    private List<FailLogBean.ListBean> mList;
    private String date;
    private String code;
    private final MutableLiveData<FailLogListParamsToRequestBean> mParamsToRequestFailLogListLiveData = new ValueKeeperLiveData<>();

    public FailLogListViewModel(@NonNull Application application) {
        super(application);
        model = new FailLogListModel();
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void saveData(int logTypeToVM, String houseListToVM) {
        mLogType = logTypeToVM;
        mHouseList = houseListToVM;
    }

    private void RequestFailLogData() {
        FailLogListParamsToRequestBean paramsToRequestFailLogList = new FailLogListParamsToRequestBean(mHouseList, date, code, mLogType, mCurrentPage);
        mParamsToRequestFailLogListLiveData.setValue(paramsToRequestFailLogList);
    }

    public LiveData<Resource<FailLogBean>> getFailLogList() {
        return Transformations.switchMap(mParamsToRequestFailLogListLiveData, input -> model.getFailLogList(input.houseList, input.logType, input.code, input.date, input.currentPage));
    }

    public void setFailLogListBean(FailLogBean failLogBean, FailLogListRecyclerAdapter adapter) {
        if (failLogBean.list == null) {
            return;
        }
        if (mCurrentPage == 1) {
            adapter.notifyRemoveListItem();
        }
        adapter.notifyAddListItem(failLogBean.list);
    }

    public void setDataList(List<FailLogBean.ListBean> list) {
        mList = list;
    }

    //判断是界面旋转 不重新请求
    public void requestFirstData() {
        if (mList.isEmpty() && mCurrentPage == 1) {
            RequestFailLogData();
        }
    }

    public void onRefresh() {
        mCurrentPage = 1;
        RequestFailLogData();
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        RequestFailLogData();
    }

    public void setData(String time) {
        mCurrentPage = 1;
        if (!TextUtils.isEmpty(time)) {
            date = time;
            RequestFailLogData();
        }
    }

    public void setCode(String mCode) {
        mCurrentPage = 1;
        code = mCode;
    }

    public int getTitleRes() {
        switch (mLogType) {
            case TYPE_TASK_STOCK_IN:
                return R.string.upload_fail_list_stock_in_title;
            case TYPE_TASK_STOCK_OUT:
                return R.string.upload_fail_list_stock_out_title;
            case TYPE_TASK_STOCK_RETURN:
                return R.string.upload_fail_list_stock_return_title;
            case TYPE_TASK_STOCK_GROUP_SPLIT:
                return R.string.upload_fail_list_group_split_title;
            case TYPE_TASK_STOCK_SINGLE_SPLIT:
                return R.string.upload_fail_list_single_split_title;
            case TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX:
                return R.string.upload_fail_list_supplement_to_box_title;
            case TYPE_TASK_PACKAGING_ASSOCIATION:
                return R.string.upload_fail_list_packaging_association_title;
            case TYPE_TASK_EXCHANGE_WAREHOUSE:
                return R.string.upload_fail_list_exchange_warehouse_title;
            case TYPE_TASK_EXCHANGE_GOODS:
                return R.string.upload_fail_list_exchange_goods_title;
            case TYPE_TASK_PACKAGE_STOCK_IN:
                return R.string.upload_fail_list_package_stock_in_title;
            case TYPE_TASK_LABEL_EDIT:
                return R.string.upload_fail_list_label_edit_title;
            case TYPE_TASK_IN_WAREHOUSE_PACKAGE:
                return R.string.upload_fail_list_in_warehouse_package_title;
            case TYPE_TASK_RELATE_TO_NFC:
                return R.string.upload_fail_list_relate_to_nfc_title;
            default:
                return R.string.upload_fail_title;
        }
    }

}

