package com.jgw.delingha.module.disassemble.base.viewmodel;

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
import com.jgw.delingha.bean.SplitCheckResultBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.module.disassemble.base.model.DisassembleModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.GroupDisassembleEntity;
import com.jgw.delingha.sql.entity.SingleDisassembleEntity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class DisassembleViewModel extends BaseViewModel {

    private final DisassembleModel model;
    public boolean isSingleDisassemble;
    public List<BaseCodeEntity> mList;

    private int mPage = 1;

    private final MutableLiveData<Long> mHasWaitUploadLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mCheckCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<SplitCheckResultBean> mUpdateCodeLiveData = new MutableLiveData<>();

    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Integer> mLoadListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();

    public DisassembleViewModel(@NonNull Application application) {
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

    public void hasWaitUpload() {
        mHasWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mHasWaitUploadLiveData, input -> model.hasWaitUpload());
    }

    public void handleScanQRCode(String code, CodeEntityRecyclerAdapter<BaseCodeEntity> adapter, CustomBaseRecyclerView recyclerView) {
        if (isRepeatCode(code)) {
            return;
        }

        BaseCodeEntity entity;
        if (isSingleDisassemble) {
            entity = new SingleDisassembleEntity();
        } else {
            entity = new GroupDisassembleEntity();
        }
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
        if (model.putData(entity) < 1) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<BaseCodeEntity> tempList = new ArrayList<>();
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

    public LiveData<Resource<SplitCheckResultBean>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, model::checkCode);
    }

    public void updateCodeStatus(SplitCheckResultBean data) {
        mUpdateCodeLiveData.setValue(data);
    }

    public LiveData<Resource<BaseCodeEntity>> getUpdateCodeLiveData() {
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
        mUploadLiveData.setValue(null);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, firstId -> model.upload());
    }

    /**
     * 当前页数据完整时尝试请求下一页数据
     */
    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadListLiveData.setValue(mPage);
        }
    }

    public LiveData<Resource<List<BaseCodeEntity>>> getLoadListLiveData() {
        return Transformations.switchMap(mLoadListLiveData, model::loadList);
    }

    /**
     * 查询统计firstId之后本次扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, firstId -> model.getCalculationTotal());
    }

    public void deleteCode(String code) {
        model.removeEntityByCode(code);
    }

    /**
     * 从反扫或确认出库界面返回和码校验为错误后删除码后
     * 省略判断直接刷新当前列表 为firstId后最近20条扫码数据
     */
    public void refreshList() {
        mPage = 1;
        mLoadListLiveData.setValue(mPage);
    }

    public int getPage() {
        return mPage;
    }
}
