package com.jgw.delingha.module.disassemble.base.viewmodel;

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
import com.jgw.delingha.module.disassemble.base.model.DisassembleModel;
import com.jgw.delingha.sql.entity.BaseCodeEntity;

import java.util.List;

public class DisassembleWaitUploadViewModel extends BaseViewModel {

    private final DisassembleModel model;
    private final MutableLiveData<Boolean> mClearDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Integer> mLoadListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();

    private int mPage = 1;

    public List<BaseCodeEntity> mList;

    private boolean isSingleDisassemble;

    public DisassembleWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new DisassembleModel();
    }

    public void setIsSingleDisassemble(boolean isSingleDisassemble) {
        this.isSingleDisassemble = isSingleDisassemble;
        model.switchType(isSingleDisassemble);
    }

    public void setDataList(List<BaseCodeEntity> dataList) {
        mList = dataList;
    }

    public boolean getIsSingleDisassemble() {
        return isSingleDisassemble;
    }

    public void loadList() {
        mLoadListLiveData.setValue(mPage);
    }

    public LiveData<Resource<List<BaseCodeEntity>>> getLoadListLiveData() {
        return Transformations.switchMap(mLoadListLiveData, model::loadList);
    }


    public void clearData() {
        mClearDataLiveData.setValue(isSingleDisassemble);
    }

    public LiveData<Resource<String>> getClearDataLiveData() {
        return Transformations.switchMap(mClearDataLiveData, input -> model.getClearDataByCurrentUserLiveDate());
    }

    public void deleteCode(String code) {
        model.removeCode(code);
    }

    public void upload() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(null);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, firstId -> model.upload());
    }

    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadListLiveData.setValue(mPage);
        }
    }

    public int getPage() {
        return mPage;
    }

    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, firstId -> model.getCalculationTotal());
    }

}
