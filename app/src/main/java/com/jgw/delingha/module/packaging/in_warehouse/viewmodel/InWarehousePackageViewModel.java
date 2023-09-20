package com.jgw.delingha.module.packaging.in_warehouse.viewmodel;

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
import com.jgw.delingha.module.packaging.in_warehouse.adpter.InWarehousePackageAdapter;
import com.jgw.delingha.module.packaging.in_warehouse.model.InWarehousePackageModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.InWarehousePackageEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity;

import java.util.List;

/**
 * author : Cxz
 * data : 2019/12/17
 * description :
 */
public class InWarehousePackageViewModel extends BaseViewModel {

    private final InWarehousePackageModel model;

    private final PackageConfigInfoModel mConfigModel;
    private PackageConfigEntity mConfigEntity;
    private final MutableLiveData<Long> mConfigInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mStatisticsCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCurrentCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageCheckCodeRequestParamBean> mCheckBoxCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageCheckCodeRequestParamBean> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mOnFullBoxLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<InWarehousePackageEntity> mShowBoxListLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<String> mRefreshCurrentBoxListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mTryUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();

    private boolean isBoxCode = true; //用于判断是否为父码
    private String mCurrentBoxCode;

    private List<InWarehousePackageEntity> mCodeList;//用于页面展示子码列表
    private long mConfigId;

    public InWarehousePackageViewModel(@NonNull Application application) {
        super(application);
        model = new InWarehousePackageModel();
        mConfigModel = new PackageConfigInfoModel();
    }

    public void setDataList(List<InWarehousePackageEntity> dataList) {
        mCodeList = dataList;
    }

    public void getConfigInfo(long configId) {
        mConfigId = configId;
        mConfigInfoLiveData.setValue(configId);
    }

    public long getConfigId() {
        return mConfigId;
    }

    public LiveData<Resource<PackageConfigEntity>> getConfigInfoLiveData() {
        return Transformations.switchMap(mConfigInfoLiveData, mConfigModel::getConfigInfo);
    }

    public void setConfigInfo(PackageConfigEntity entity) {
        mConfigEntity = entity;
    }

    public void updateStatisticsCount() {
        mStatisticsCountLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<Long>> getUpdateStatisticsCountLiveData() {
        return Transformations.switchMap(mStatisticsCountLiveData, model::getBoxCodeCount);
    }

    /**
     * 当前录入是否是箱码
     */
    public boolean isInputBoxCode() {
        if (isBoxCode) {
            return true;
        } else {
            if (TextUtils.isEmpty(mCurrentBoxCode)) {
                resetBoxCode();
            }
            return isBoxCode;
        }
    }

    public void checkBoxCode(String code) {
        isBoxCode = false;
        mCurrentBoxCode = code;

        InWarehousePackageEntity entity = new InWarehousePackageEntity();
        entity.setIsBoxCode(true);
        entity.setOuterCode(code);
        entity.getConfigEntity().setTarget(mConfigEntity);
        entity.setCodeLevelName(mConfigEntity.getFirstNumberName());
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.setIsFull(false);
        long id = model.putData(entity);
        if (id == -1) {
            ToastUtils.showToast("新增扫码数据失败");
            return;
        }
        //当前列表父码验证失败删除后 列表内子码更换新父码
        if (mCodeList.size() != 0) {
            model.updateCodeListByNewBoxCode(code, model.getCodeTypeId(code), mCodeList);
        }

        PackageCheckCodeRequestParamBean bean = new PackageCheckCodeRequestParamBean();
        bean.code = code;
        bean.productBatchId = mConfigEntity.getProductBatchId();
        bean.productId = mConfigEntity.getProductId();
        bean.packageLevel = mConfigEntity.getPackageLevel();
        mCheckBoxCodeLiveData.setValue(bean);

    }

    public LiveData<Resource<String>> getCheckBoxCodeLiveData() {
        return Transformations.switchMap(mCheckBoxCodeLiveData, model::checkBoxCode);
    }

    public void checkSonCode(String sonCode, InWarehousePackageAdapter adapter, CustomBaseRecyclerView recyclerView) {
        InWarehousePackageEntity entity = new InWarehousePackageEntity();
        entity.setIsBoxCode(false);
        entity.setOuterCode(sonCode);
        entity.getConfigEntity().setTarget(mConfigEntity);
        entity.setCodeLevelName(mConfigEntity.getLastNumberName());
        entity.setParentOuterCodeId(mCurrentBoxCode);
        entity.setParentOuterCodeTypeId(model.getCodeTypeId(mCurrentBoxCode));
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
        bean.boxCode = mCurrentBoxCode;
        bean.productBatchId = mConfigEntity.getProductBatchId();
        bean.productId = mConfigEntity.getProductId();
        bean.packageLevel = mConfigEntity.getPackageLevel();
        mCheckCodeLiveData.setValue(bean);

        if (mCodeList.size() == Integer.parseInt(mConfigEntity.getNumber())) {//当前子列表满箱后不等校验直接跳到下一箱
            onFullBox();
        }
        updateCurrentCount();
    }

    public LiveData<Resource<InWarehousePackageEntity>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, model::checkCode);
    }

    /**
     * 更新当前箱子码数量统计
     */
    private void updateCurrentCount() {
        mCurrentCountLiveData.setValue(null);
    }

    public LiveData<Long> getCurrentCountLiveData() {
        return mCurrentCountLiveData;
    }

    public boolean isCurrentBoxCode(String code) {
        return TextUtils.equals(code, mCurrentBoxCode);
    }

    /**
     * 更新父码状态 仅有状态变化 无增删
     * public static final int STATUS_CODE_SUCCESS = 1;
     * public static final int STATUS_CODE_FAIL = 2;
     *
     * @param code   父码或子码
     * @param status CodeBean.codeStatus
     */
    public void updateCodeStatus(String code, int status) {
        model.updateCodeStatus(code, status);
    }

    /**
     * 服务器校验失败时删除码
     *
     * @param code 父码
     */
    public void removeBoxCode(String code) {
        if (TextUtils.equals(code, mCurrentBoxCode)) {
            //如果是当前箱码保留子码 更换新箱码时子码更新为新箱码
            resetBoxCode();
            model.removeCode(code);
        } else {
            //如果是非当前箱码 则全部删除 因为删除箱码后也无法查出原有子码
            model.removeAllByBoxCode(code);
        }
    }

    public void removeCode(String code) {
        //删除子码时 父码满箱状态要更新
        model.removeCode(code);
    }

    /**
     * 箱装满后页面刷新
     */
    private void onFullBox() {
        model.updateFullBoxCode(mCurrentBoxCode, true);
        isBoxCode = true;
        mCurrentBoxCode = "";
        mOnFullBoxLiveData.setValue(null);
    }

    public LiveData<Long> getOnFullBoxLiveData() {
        return mOnFullBoxLiveData;
    }

    /* *************************************** 普通版 *************************************** */

    public LiveData<Resource<InWarehousePackageEntity>> getShowBoxListLiveData() {
        return Transformations.switchMap(mShowBoxListLiveData, model::showBoxList);
    }

    public void refreshCurrentBoxList() {
        mRefreshCurrentBoxListLiveData.setValue(mCurrentBoxCode);
    }

    public LiveData<Resource<List<InWarehousePackageEntity>>> getRefreshBoxListLiveData() {
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
     * 合并原有检查是否为箱码逻辑
     *
     * @param code 扫描的码
     * @return true or false
     */
    public boolean isRepeatCode(String code) {
        InWarehousePackageEntity entity = model.queryEntityByCode(code);
        if (entity != null) {
            if (entity.getIsBoxCode()) {
                if (TextUtils.equals(mCurrentBoxCode, code)) {
                    ToastUtils.showToast("您已经在当前" + mConfigEntity.getFirstNumberName() + "列表");
                    return true;
                }
                if (entity.getConfigEntity().getTargetId() != mConfigId) {
                    if (entity.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                        ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
                    } else {
                        ToastUtils.showToast(mConfigEntity.getFirstNumberName() + "已存在,请去待执行操作");
                    }
                    return true;
                }
                if (!entity.getIsFull()) {
                    isBoxCode = false;
                    mCurrentBoxCode = code;
                    mShowBoxListLiveData.setValue(entity);
                } else {
                    ToastUtils.showToast("该箱已满无需重新扫码");
                }
            } else {
                if (entity.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                    ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
                } else {
                    ToastUtils.showToast(code + "该码已在库存中!");
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 判断重复码是否是箱码
     */
    public boolean isRepeatBoxCode(String code) {
        return model.isRepeatBoxCode(code);
    }

    public PackageConfigEntity getConfigEntity() {
        return mConfigEntity;
    }

    public Spanned getCodeCountStatistics() {
        String firstNumberName = mConfigEntity.getFirstNumberName();
        int size = mCodeList.size();
        String lastNumberName = mConfigEntity.getLastNumberName();
        String source = "当前" + firstNumberName + "第<font color='#03A9F4'>" + size + "</font>" + lastNumberName;
        return Html.fromHtml(source);
    }

    public Spanned getBoxCodeCountStatistics(long boxCount) {
        String firstNumberName = mConfigEntity.getFirstNumberName();
        String source = "累计<font color='#03A9F4'>" + boxCount + "</font>" + firstNumberName;
        return Html.fromHtml(source);

    }

    public String getCurrentBoxCode() {
        return mCurrentBoxCode;
    }

    /**
     * 重置箱码为空 可扫箱码状态
     */
    public void resetBoxCode() {
        mCurrentBoxCode = "";
        isBoxCode = true;
    }

    public boolean hasWaitUpload() {
        return model.hasWaitUpload(mConfigId);
    }

    public String getFirstNumberName() {
        return mConfigEntity.getFirstNumberName();
    }

    public String getLastNumberName() {
        return mConfigEntity.getLastNumberName();
    }
}
