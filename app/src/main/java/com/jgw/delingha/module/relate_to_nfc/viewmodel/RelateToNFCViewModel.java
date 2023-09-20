package com.jgw.delingha.module.relate_to_nfc.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.view.CustomBaseRecyclerView;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.RelateToNFCBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.module.relate_to_nfc.adapter.RelateToNFCAdapter;
import com.jgw.delingha.module.relate_to_nfc.model.RelateToNFCModel;
import com.jgw.delingha.sql.entity.RelateToNFCEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : J-T
 * @date : 2022/6/8 15:58
 * description : 关联NFC viewModel
 */
public class RelateToNFCViewModel extends BaseViewModel {
    private static final int PAGE_CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;
    private final RelateToNFCModel mModel;
    private RelateToNFCBean nfcBean;

    private final MutableLiveData<String> mGetCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mLoadMoreListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mRefreshListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mGetTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mUploadLiveData = new ValueKeeperLiveData<>();
    private List<RelateToNFCEntity> mList;
    private int mCurrentPage = 1;


    public RelateToNFCViewModel(@NonNull Application application) {
        super(application);
        mModel = new RelateToNFCModel();
    }

    public RelateToNFCBean getNfcBean() {
        if (nfcBean == null) {
            nfcBean = new RelateToNFCBean();
        }
        return nfcBean;
    }

    public void setDataList(List<RelateToNFCEntity> dataList) {
        mList = dataList;
    }

    /**
     * 获取当前用户数量
     */
    public void getCount() {
        mGetCountLiveData.setValue("");
    }

    public LiveData<Resource<Long>> getCountLiveData() {
        return Transformations.switchMap(mGetCountLiveData, input -> mModel.getCount());
    }

    public boolean checkCodeExisted(String code) {
        return mModel.checkIsRepeatQrCode(code);
    }

    public boolean checkNFCExisted(String NFCCode) {
        return mModel.checkIsRepeatNFCCode(NFCCode);
    }

    public void handleScanQRCode(String code, RelateToNFCAdapter mAdapter, CustomBaseRecyclerView rvRelateToNfcRecord) {
        nfcBean.setQRCode(code);
        checkAddRecode(mAdapter, rvRelateToNfcRecord);
    }

    private void checkAddRecode(RelateToNFCAdapter mAdapter, CustomBaseRecyclerView rvRelateToNfcRecord) {
        if (TextUtils.isEmpty(nfcBean.getQRCode()) || TextUtils.isEmpty(nfcBean.getNFCCode())) {
            return;
        }
        RelateToNFCEntity relateToNFCEntity = new RelateToNFCEntity();
        relateToNFCEntity.setQRCode(nfcBean.getQRCode());
        relateToNFCEntity.setNFCCode(nfcBean.getNFCCode());
        if (!mModel.putData(relateToNFCEntity)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        mAdapter.notifyAddItem(relateToNFCEntity);
        rvRelateToNfcRecord.scrollToPosition(0);
        nfcBean.setNFCCode("");
        nfcBean.setQRCode("");
        List<RelateToNFCEntity> tempList = new ArrayList<>();
        if (mList.size() > PAGE_CAPACITY) {
            for (int i = PAGE_CAPACITY; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                mAdapter.notifyItemRangeRemoved(PAGE_CAPACITY, tempList.size());
            }
        }
        getCount();
        mCurrentPage = 1;
    }

    public void handleScanNFCCode(String code, RelateToNFCAdapter mAdapter, CustomBaseRecyclerView rvRelateToNfcRecord) {
        nfcBean.setNFCCode(code);
        checkAddRecode(mAdapter, rvRelateToNfcRecord);
    }

    public void refreshList() {
        mCurrentPage = 1;
        mRefreshListLiveData.setValue(null);
    }

    /**
     * 加载更多
     */
    public void onLoadMore() {
        if (mList.size() != PAGE_CAPACITY * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        mLoadMoreListLiveData.setValue(null);
    }

    /**
     * 加载更多时获取更多list数据
     */
    public LiveData<Resource<List<RelateToNFCEntity>>> getLoadMoreListLiveData() {
        return Transformations.switchMap(mLoadMoreListLiveData, input -> mModel.loadList(mCurrentPage));
    }

    public LiveData<Resource<List<RelateToNFCEntity>>> getRefreshListLiveData() {
        return Transformations.switchMap(mRefreshListLiveData, input -> mModel.loadList(mCurrentPage));
    }


    public boolean checkCode() {
        if (mList.isEmpty()) {
            ToastUtils.showToast("请先扫码!");
            return true;
        }
        return false;
    }

    public void getTaskId() {
        mGetTaskIdLiveData.postValue("");
    }

    public LiveData<Resource<String>> getTaskIdLiveData() {
        return Transformations.switchMap(mGetTaskIdLiveData, input -> mModel.getTaskId());
    }

    public void uploadData(String taskId) {
        mUploadLiveData.postValue(taskId);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, mModel::uploadList);
    }
}
