package com.jgw.delingha.module.relate_to_nfc.viewmodel;

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
import com.jgw.delingha.module.relate_to_nfc.model.RelateToNFCScanBackModel;
import com.jgw.delingha.sql.entity.RelateToNFCEntity;

import java.util.List;

public class RelateToNFCScanBackViewModel extends BaseViewModel {

    private final RelateToNFCScanBackModel model;
    public List<RelateToNFCEntity> mList;
    private final MutableLiveData<Integer> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteByQRCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteByNFCCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteAllLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetListLiveData = new ValueKeeperLiveData<>();


    private int mCurrentPage = 1;

    public RelateToNFCScanBackViewModel(@NonNull Application application) {
        super(application);
        model = new RelateToNFCScanBackModel();
    }


    public void loadList() {
        mGetListLiveData.setValue(null);
    }

    public LiveData<Resource<List<RelateToNFCEntity>>> getCodeListLiveData() {
        return Transformations.switchMap(mGetListLiveData, input -> model.getCodeList(mCurrentPage));
    }

    public void handleScanQRCode(String code) {
        mDeleteByQRCodeLiveData.setValue(code);
    }

    public void handleScanNFCCode(String NFCCode) {
        mDeleteByNFCCodeLiveData.setValue(NFCCode);
    }

    public LiveData<Resource<RelateToNFCEntity[]>> getDeleteCodeByQRCodeLiveData() {
        return Transformations.switchMap(mDeleteByQRCodeLiveData, input -> {
            int count = mCurrentPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            boolean isNeedAdd = false;
            for (RelateToNFCEntity entity : mList) {
                if (TextUtils.equals(entity.getQRCode(), input)) {
                    isNeedAdd = true;
                    break;
                }
            }
            return model.deleteByQRCodeAndAddOne(input, count, isNeedAdd);
        });
    }

    public LiveData<Resource<RelateToNFCEntity[]>> getDeleteCodeByNFCCodeLiveData() {
        return Transformations.switchMap(mDeleteByNFCCodeLiveData, input -> {
            int count = mCurrentPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            boolean isNeedAdd = false;
            for (RelateToNFCEntity entity : mList) {
                if (TextUtils.equals(entity.getNFCCode(), input)) {
                    isNeedAdd = true;
                    break;
                }
            }
            return model.deleteByNFCCodeAndAddOne(input, count, isNeedAdd);
        });
    }

    /**
     * 查询统计 该type的码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, input -> model.calculationTotal());
    }

    public void setDataList(List<RelateToNFCEntity> list) {
        mList = list;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void deleteAll() {
        mDeleteAllLiveData.setValue(null);
    }

    public LiveData<Resource<String>> getDeleteAllLiveData() {
        return Transformations.switchMap(mDeleteAllLiveData, input -> model.deleteAllByCurrentUser());
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
