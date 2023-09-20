package com.jgw.delingha.module.disassemble.all.viewmode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.view.CustomBaseRecyclerView;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.module.disassemble.all.model.DisassembleAllModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.DisassembleAllEntity;

import java.util.ArrayList;
import java.util.List;

public class DisassembleAllViewModel extends BaseViewModel {

    private final DisassembleAllModel model;
    public List<DisassembleAllEntity> mList;

    private int mPage = 1;

    private final MutableLiveData<Long> mHasWaitUploadLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mCheckCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<DisassembleAllEntity> mUpdateCodeLiveData = new MutableLiveData<>();

    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();

    public DisassembleAllViewModel(@NonNull Application application) {
        super(application);
        model = new DisassembleAllModel();
    }

    public void setDataList(List<DisassembleAllEntity> dataList) {
        mList = dataList;
    }

    public void hasWaitUpload() {
        mHasWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mHasWaitUploadLiveData, input -> model.hasWaitUpload());
    }

    public void handleScanQRCode(String code, CustomRecyclerAdapter<DisassembleAllEntity> adapter, CustomBaseRecyclerView recyclerView) {
        if (isRepeatCode(code)) {
            return;
        }
        DisassembleAllEntity entity = new DisassembleAllEntity();
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
        entity.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
        if (!model.putData(entity)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<DisassembleAllEntity> tempList = new ArrayList<>();
        if (mList.size() > CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            for (int i = CustomRecyclerAdapter.ITEM_PAGE_SIZE; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CustomRecyclerAdapter.ITEM_PAGE_SIZE, tempList.size());
            }
        }

        calculationTotal();
        mCheckCodeLiveData.setValue(code);
        mPage = 1;
    }

    public LiveData<Resource<DisassembleAllEntity>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, model::checkCode);
    }


    public void updateCodeStatus(DisassembleAllEntity data) {
        mUpdateCodeLiveData.setValue(data);
    }

    public LiveData<Resource<DisassembleAllEntity>> getUpdateCodeLiveData() {
        return Transformations.switchMap(mUpdateCodeLiveData, model::updateCodeInfo);
    }

    public boolean isRepeatCode(String code) {
        return model.isRepeatCode(code);
    }

    public boolean existVerifyingOrFailData() {
        return model.existVerifyingData();
    }

    public void upload() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(0L);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, input -> model.uploadList());
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

    /**
     * 查询统计扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(0L);
    }

    public LiveData<Resource<Integer>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, whatever -> model.getCalculationTotal());
    }

    public void deleteCode(String code) {
        model.removeEntityByCode(code);
    }

    /**
     * 从反扫或确认出库界面返回和码校验为错误后删除码后
     * 省略判断直接刷新当前列表 为最近20条扫码数据
     */
    public void refreshList() {
        mRefreshListLiveData.setValue(0L);
        mPage = 1;
    }

    public LiveData<Resource<List<DisassembleAllEntity>>> getRefreshListLiveData() {
        return Transformations.switchMap(mRefreshListLiveData, whatever -> model.loadListData());
    }
}
