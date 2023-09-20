package com.jgw.delingha.module.scan_back.viewmodel;

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
import com.jgw.delingha.module.scan_back.model.CommonScanBackModel;
import com.jgw.delingha.sql.entity.BaseCodeEntity;

import java.util.List;

public class CommonScanBackViewModel extends BaseViewModel {

    private final CommonScanBackModel model;
    public List<BaseCodeEntity> mList;
    private final MutableLiveData<Integer> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteAllLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetListByIdLiveData = new ValueKeeperLiveData<>();

    //数据库相关
    private int mType;
    private int mCurrentPage = 1;
    private long mConfigId;
    private boolean fromConfig;

    public CommonScanBackViewModel(@NonNull Application application) {
        super(application);
        model = new CommonScanBackModel();
    }

    public void setDatabaseDataByConfigId(int type, long configId) {
        mType = type;
        mConfigId = configId;
        fromConfig = true;
    }

    public void setDatabaseDataByType(int type) {
        mType = type;
    }

    public void loadList() {
        mGetListByIdLiveData.setValue(null);
    }

    public LiveData<Resource<List<BaseCodeEntity>>> getCodeListLiveData() {
        return Transformations.switchMap(mGetListByIdLiveData, input -> {
            if (fromConfig) {
                return model.getCodeListByConfigId(mConfigId, mCurrentPage, mType);
            } else {
                return model.getCodeList(mCurrentPage, mType);
            }
        });
    }

    public void handleScanQRCode(String code) {
        mDeleteCodeLiveData.setValue(code);
    }

    public LiveData<Resource<BaseCodeEntity[]>> getDeleteCodeLiveData() {
        return Transformations.switchMap(mDeleteCodeLiveData, input -> {
            int count = mCurrentPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            boolean isNeedAdd = false;
            for (BaseCodeEntity entity : mList) {
                if (TextUtils.equals(entity.getCode(), input)) {
                    isNeedAdd = true;
                    break;
                }
            }
            if (fromConfig) {
                return model.deleteCodeAndAddOneByConfigId(mConfigId, input, count, mType, isNeedAdd);
            } else {
                return model.deleteCodeAndAddOne(input, count, mType, isNeedAdd);
            }
        });
    }

    /**
     * 查询统计 该type的码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, input -> {
            if (fromConfig) {
                return model.calculationTotalByConfigId(mConfigId, mType);
            } else {
                return model.calculationTotal(mType);
            }
        });
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
        return Transformations.switchMap(mDeleteAllLiveData, input -> {
            if (fromConfig) {
                return model.deleteAllByConfigId(mConfigId, mType);
            } else {
                return model.deleteAllByCurrentUser(mType);
            }
        });
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
