package com.jgw.delingha.module.stock_out.order.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.scan_back.ui.CommonOrderProductScanBackActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.module.wait_upload_task.base.BaseWaitUploadViewModel;
import com.jgw.delingha.module.wait_upload_task.model.OrderTaskWaitUploadModel;
import com.jgw.delingha.sql.entity.BaseOrderEntity;

import java.util.List;

public class CommonStockOutWaitUploadViewModel extends BaseWaitUploadViewModel {

    private final OrderTaskWaitUploadModel model;

    public CommonStockOutWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new OrderTaskWaitUploadModel();
    }

    @Override
    protected LiveData<Resource<List<? extends BaseOrderEntity>>> getCustomListLiveData() {
        return model.getLocalOrderStockOutList(CommonOrderProductScanBackActivity.TYPE_STOCK_OUT);
    }

    @Override
    protected LiveData<Resource<String>> getCustomDeleteLiveData(List<BaseOrderEntity> list) {
        return model.deleteOrders(list,CommonOrderProductScanBackActivity.TYPE_STOCK_OUT);
    }

    public int getTaskType() {
        return TaskListViewModel.TYPE_TASK_STOCK_OUT;
    }
}
