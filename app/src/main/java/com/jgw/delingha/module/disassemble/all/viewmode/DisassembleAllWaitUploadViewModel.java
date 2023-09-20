package com.jgw.delingha.module.disassemble.all.viewmode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.module.disassemble.all.model.DisassembleAllModel;
import com.jgw.delingha.sql.entity.DisassembleAllEntity;

import java.util.List;

public class DisassembleAllWaitUploadViewModel extends BaseViewModel {

    private final DisassembleAllModel model;
    private final MutableLiveData<Long> mGetListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mClearDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();

    public List<DisassembleAllEntity> mList;

    private int mPage = 1;

    public DisassembleAllWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new DisassembleAllModel();
    }


    public void setList(List<DisassembleAllEntity> dataList) {
        mList = dataList;
    }

    public void loadList() {
        mGetListLiveData.setValue(null);
    }

    /**
     * 查询统计扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(0L);
    }

    public LiveData<Resource<Integer>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, whatever -> model.getCalculationTotal());
    }

    public LiveData<Resource<List<DisassembleAllEntity>>> getListLiveData() {
        return Transformations.switchMap(mGetListLiveData, input -> model.loadListData());
    }


    public void deleteCode(String code) {
        model.removeEntityByCode(code);
    }

    public void removeData(DisassembleAllEntity entity) {
        model.removeData(entity);
    }

    /**
     * 当前页数据完整时尝试请求下一页数据
     */
    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadMoreLiveData.setValue(null);
        }
    }

    public LiveData<Resource<List<DisassembleAllEntity>>> getLoadMoreLiveData() {
        return Transformations.switchMap(mLoadMoreLiveData, input -> model.loadMoreList(mPage, CustomRecyclerAdapter.ITEM_PAGE_SIZE));
    }


    public void upload() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(-1L);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, input -> model.uploadList());
    }

    public void clearData() {
        mClearDataLiveData.setValue(null);
    }

    public LiveData<Resource<String>> getClearDataLiveData() {
        return Transformations.switchMap(mClearDataLiveData, s -> model.getClearDataByCurrentUserLiveDate());
    }

}
