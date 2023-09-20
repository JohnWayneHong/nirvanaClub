package com.jgw.delingha.module.scan_back.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.module.scan_back.model.CommonOrderProductScanBackModel;
import com.jgw.delingha.sql.entity.BaseCodeEntity;

import java.util.List;

public class CommonOrderProductScanBackViewModel extends BaseViewModel {

    private final CommonOrderProductScanBackModel model;
    public List<BaseCodeEntity> mList;
    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteAllLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetListByIdLiveData = new ValueKeeperLiveData<>();

    //数据库相关
    private int mType;
    private int mCurrentPage = 1;
    private long mId;

    public CommonOrderProductScanBackViewModel(@NonNull Application application) {
        super(application);
        model = new CommonOrderProductScanBackModel();
    }

    public void setDatabaseDataById(int type, long id) {
        mType = type;
        mId = id;
    }


    public void loadList() {
        mGetListByIdLiveData.setValue(null);
    }

    public LiveData<Resource<List<? extends BaseCodeEntity>>> getCodeListLiveData() {
        return Transformations.switchMap(mGetListByIdLiveData,
                input -> model.getCodeListByProductId(mId, mCurrentPage, mType));
    }

    public void handleScanQRCode(String code) {
        mDeleteCodeLiveData.setValue(code);
    }

    public LiveData<Resource<String>> getDeleteCodeLiveData() {
        return Transformations.switchMap(mDeleteCodeLiveData, input -> model.deleteCode(input, mType,mId));
    }

    /**
     * 查询统计firstId之后本次扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {

        return Transformations.switchMap(mCalculationTotalLiveData, input -> model.calculationTotalById(mId, mType));
    }

    public void setDataList(List<BaseCodeEntity> list) {
        mList = list;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void deleteAll() {
        mDeleteAllLiveData.setValue(null);
    }

    public LiveData<Resource<String>> getDeleteAllLiveData() {
        return Transformations.switchMap(mDeleteAllLiveData,
                input -> model.deleteAllByProductId(mId, mType));
    }

    public void refreshList() {
        mCurrentPage = 1;
        loadList();
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        loadList();
    }

}
