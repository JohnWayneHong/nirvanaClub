package com.jgw.delingha.module.tools_table.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.bean.ToolsTableHeaderBean;
import com.jgw.delingha.module.tools_table.model.ToolsTableModel;

import java.util.List;

public class ToolsTableViewModel extends BaseViewModel {

    private final ToolsTableModel model;

    private final MutableLiveData<String> mToolsTableHeaderLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mToolsTableListLiveData = new ValueKeeperLiveData<>();

    public ToolsTableViewModel(@NonNull Application application) {
        super(application);
        model = new ToolsTableModel();
    }

    /**
     * 触发获取ToolsTableHeader
     */
    public void getToolsTableHeaderData() {
        mToolsTableHeaderLiveData.setValue("");
    }

    /**
     * 获取ToolsTableHeader
     */
    public LiveData<Resource<ToolsTableHeaderBean>> getToolsTableHeaderLiveData() {
        return Transformations.switchMap(mToolsTableHeaderLiveData, input -> model.getToolsTableHeaderData());
    }

    /**
     * 触发获取ToolsTableHeader
     */
    public void getToolsTableListData() {
        mToolsTableListLiveData.setValue("");
    }

    /**
     * 获取ToolsTableHeader
     */
    public LiveData<Resource<List<ToolsOptionsBean>>> getToolsTableListLiveData() {
        return Transformations.switchMap(mToolsTableListLiveData, input -> model.getToolsTableListData());
    }

    public void clearEmptyBoxCode() {
        model.clearEmptyBoxCode();
    }
}
