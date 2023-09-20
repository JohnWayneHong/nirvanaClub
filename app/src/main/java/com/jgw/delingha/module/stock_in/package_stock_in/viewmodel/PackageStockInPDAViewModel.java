package com.jgw.delingha.module.stock_in.package_stock_in.viewmodel;

import android.app.Application;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.view.CustomBaseRecyclerView;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.PackageCheckCodeRequestParamBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.PackageConfigInfoModel;
import com.jgw.delingha.module.stock_in.package_stock_in.model.PackageStockInModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.PackageStockInEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * author : Cxz
 * data : 2019/12/17
 * description :
 */
public class PackageStockInPDAViewModel extends BaseViewModel {

    private final PackageConfigInfoModel mConfigModel;
    private final PackageStockInModel model;
    private boolean isBoxCode = true; //用于判断是否为父码
    private String boxCode;

    private long mConfigId = -1;

    private PackageConfigEntity mConfigEntity;
    private final MutableLiveData<Long> mConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCountTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCurrentCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageStockInEntity> mShowBoxListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageCheckCodeRequestParamBean> mCheckBoxCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageCheckCodeRequestParamBean> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mOnFullBoxLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mRefreshCurrentBoxListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mTryUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();

    private List<PackageStockInEntity> mList = new ArrayList<>();

    public PackageStockInPDAViewModel(@NonNull Application application) {
        super(application);
        model = new PackageStockInModel();
        mConfigModel = new PackageConfigInfoModel();

    }

    public long getConfigId() {
        return mConfigId;
    }

    public void setDataList(List<PackageStockInEntity> list) {
        mList = list;
    }

    public void getConfig(long configId) {
        mConfigId = configId;
        mConfigLiveData.setValue(configId);
    }

    public LiveData<Resource<PackageConfigEntity>> getConfigLiveData() {
        return Transformations.switchMap(mConfigLiveData, mConfigModel::getConfigInfo);
    }

    public void setConfigEntity(PackageConfigEntity entity) {
        mConfigEntity = entity;
    }

    public PackageConfigEntity getConfigEntity() {
        return mConfigEntity;
    }

    public void getCountTotal() {
        mCountTotalLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<Long>> getCountTotalLiveData() {
        return Transformations.switchMap(mCountTotalLiveData, model::getCountTotal);
    }

    public Spanned getCodeCount() {
        String firstNumberName = mConfigEntity.getFirstNumberName();
        int size = mList.size();
        String lastNumberName = mConfigEntity.getLastNumberName();
        String source = "当前" + firstNumberName + "第<font color='#03A9F4'>" + size + "</font>" + lastNumberName;
        return Html.fromHtml(source);
    }

    public Spanned getBoxCodeCount(long boxCount) {
        String firstNumberName = mConfigEntity.getFirstNumberName();
        String source = "累计<font color='#03A9F4'>" + boxCount + "</font>" + firstNumberName;
        return Html.fromHtml(source);
    }

    public boolean isInputBoxCode() {
        return isBoxCode;
    }

    public String getCurrentBoxCode() {
        return boxCode;
    }

    public void checkBoxCode(String code) {
        isBoxCode = false;
        PackageStockInEntity entity = new PackageStockInEntity();
        entity.setIsBoxCode(true);
        entity.setOuterCode(code);
        entity.getConfigEntity().setTarget(mConfigEntity);
        entity.setCodeLevelName(mConfigEntity.getFirstNumberName());
        entity.setIsFull(false);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        long id = model.putData(entity);
        if (id == -1) {
            ToastUtils.showToast("新增扫码数据失败");
            return;
        }
        if (mList.size() != 0) {
            model.updateCodeListByNewBoxCode(code, model.getCodeTypeId(code), mList);
        }

        PackageCheckCodeRequestParamBean bean = new PackageCheckCodeRequestParamBean();
        bean.code = code;
        bean.productBatchId = mConfigEntity.getProductBatchId();
        bean.productId = mConfigEntity.getProductId();
        bean.packageLevel = mConfigEntity.getPackageLevel();
        mCheckBoxCodeLiveData.setValue(bean);

        this.boxCode = code;
    }

    public LiveData<Resource<String>> getCheckBoxCodeLiveData() {
        return Transformations.switchMap(mCheckBoxCodeLiveData, model::checkBoxCode);
    }

    public void checkSonCode(String sonCode, CodeEntityRecyclerAdapter<PackageStockInEntity> adapter, CustomBaseRecyclerView recyclerView) {
        PackageStockInEntity entity = new PackageStockInEntity();
        entity.setIsBoxCode(false);
        entity.setOuterCode(sonCode);
        entity.getConfigEntity().setTarget(mConfigEntity);
        entity.setCodeLevelName(mConfigEntity.getLastNumberName());
        entity.setParentOuterCodeId(this.boxCode);
        entity.setParentOuterCodeTypeId(model.getCodeTypeId(this.boxCode));
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        long id = model.putData(entity);
        if (id == -1) {
            ToastUtils.showToast("新增扫码数据失败");
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);

        PackageCheckCodeRequestParamBean bean = new PackageCheckCodeRequestParamBean();
        bean.code = sonCode;
        bean.boxCode = this.boxCode;
        bean.productBatchId = mConfigEntity.getProductBatchId();
        bean.productId = mConfigEntity.getProductId();
        bean.packageLevel = mConfigEntity.getPackageLevel();
        mCheckCodeLiveData.setValue(bean);

        if (mList.size() == Integer.parseInt(mConfigEntity.getNumber())) {
            onFullBox();
        }
        updateCurrentCount();
    }

    public LiveData<Resource<PackageStockInEntity>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, model::checkCode);
    }

    private void updateCurrentCount() {
        mCurrentCountLiveData.setValue(null);
    }

    public LiveData<Long> getCurrentCountLiveData() {
        return mCurrentCountLiveData;
    }

    public boolean isCurrentBoxCode(String code) {
        return TextUtils.equals(code, this.boxCode);
    }

    private void onFullBox() {
        updateFullBoxCode(this.boxCode);
        isBoxCode = true;
        this.boxCode = "";
        mOnFullBoxLiveData.setValue(null);
    }

    public LiveData<Long> getOnFullBoxLiveData() {
        return mOnFullBoxLiveData;
    }

    private void updateFullBoxCode(String code) {
        model.updateFullBoxCode(code, true);
    }

    public void updateCodeStatus(String code, int status) {
        model.updateCodeStatus(code, status);
    }

    public boolean hasWaitUpload() {
        return model.hasWaitUpload(mConfigId);
    }


    public LiveData<Resource<PackageStockInEntity>> getShowBoxListLiveData() {
        return Transformations.switchMap(mShowBoxListLiveData, model::showBoxList);
    }

    public void refreshCurrentBoxList() {
        mRefreshCurrentBoxListLiveData.setValue(this.boxCode);
    }

    public LiveData<Resource<List<PackageStockInEntity>>> getRefreshBoxListLiveData() {
        return Transformations.switchMap(mRefreshCurrentBoxListLiveData, model::querySonListByBoxCode);
    }

    public void tryUploadCode() {
        mTryUploadLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<String>> getTryUploadLiveData() {
        return Transformations.switchMap(mTryUploadLiveData, model::checkNotFullBoxCode);
    }

    public void getTaskId() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mRequestTaskIdLiveData.setValue(mConfigEntity.getId());
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mConfigModel::getTaskId);
    }

    public void realUploadCode() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(mConfigEntity.getId());
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::uploadListByConfigId);
    }

    /**
     * 判断码是否重复
     *
     * @param code 扫描的码
     * @return true or false
     */
    public boolean isRepeatCode(String code) {
        PackageStockInEntity data = model.queryEntityByCode(code);
        if (data != null) {
            if (data.getIsBoxCode()) {
                if (data.getConfigEntity().getTarget().getId()!=(mConfigEntity.getId())) {
                    if (data.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                        ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
                    } else {
                        ToastUtils.showToast(mConfigEntity.getFirstNumberName() + "已存在,请去待执行操作");
                    }
                    return true;
                }
                if (TextUtils.equals(boxCode, code)) {
                    ToastUtils.showToast("您已经在当前" + mConfigEntity.getFirstNumberName() + "列表");
                    return true;
                }
                if (!data.getIsFull()) {
                    isBoxCode = false;
                    boxCode = code;
                    mShowBoxListLiveData.setValue(data);
                } else {
                    ToastUtils.showToast("该箱已满无需重新扫码");
                }
            } else {
                if (data.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                    ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
                } else {
                    ToastUtils.showToast(code + "该码已在库存中!");
                }
            }
            return true;
        }
        return false;
    }

    public void removeBoxCode(String code) {
        if (TextUtils.equals(code, this.boxCode)) {
            resetBoxCode();
            model.removeCode(code);
        } else {
            model.removeAllByBoxCode(code);
        }
    }

    public void removeCode(String code) {
        model.removeCode(code);
    }

    public void resetBoxCode() {
        this.boxCode = "";
        isBoxCode = true;
    }

    /**
     * 判断重复码是否是箱码
     */
    public boolean isRepeatBoxCode(String code) {
        return model.isRepeatBoxCode(code);
    }

    public String getFirstNumberName() {
        return mConfigEntity.getFirstNumberName();
    }

    public String getLastNumberName() {
        return mConfigEntity.getLastNumberName();
    }

}
