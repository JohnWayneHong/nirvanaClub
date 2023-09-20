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
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.module.scan_back.model.CommonPackageScanBackModel;
import com.jgw.delingha.sql.entity.BasePackageCodeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : J-T
 * @date : 2022/2/21 14:09
 * description : 含有上下级码的通用反扫类
 */
public class CommonPackageScanBackViewModel extends BaseViewModel {
    private long mConfigId;
    private int mType;
    private final CommonPackageScanBackModel model;
    public List<BasePackageCodeEntity> mList;
    private BasePackageCodeEntity lastBoxEntity;
    private boolean isNeedChangeBox = true;
    /**
     * 箱的组 从1开始 当获取的码不足一组时 换箱 组重置为1
     */
    private int currentPage = 1;

    private final MutableLiveData<Boolean> mCodeLisByConfigIdtLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Integer> mCalculateTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mClearDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mDeleteBoxCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<BasePackageCodeEntity> mDeleteChildCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mGetCodeInfoLiveData = new ValueKeeperLiveData<>();


    public CommonPackageScanBackViewModel(@NonNull Application application) {
        super(application);
        model = new CommonPackageScanBackModel();
    }

    public void setTypeAndConfigId(int type, long configId) {
        mType = type;
        mConfigId = configId;
    }

    public void setDataList(List<BasePackageCodeEntity> list) {
        mList = list;
    }

    /**
     * isNeedChangeBox 是否需要换箱
     * currentPage 当前箱的第几页
     * 首先判断是否要换箱  如果要换箱 当前箱的页数置1
     */
    public void loadNextPage() {
        //首先判断是否要换箱
        if (isNeedChangeBox) {
            currentPage = 1;
            loadMore(true);
        } else {
            currentPage++;
            loadMore(false);
        }
    }

    private void loadMore(boolean isNeedChangeBox) {
        mCodeLisByConfigIdtLiveData.setValue(isNeedChangeBox);
    }

    public LiveData<Resource<List<BasePackageCodeEntity>>> getCodeByConfigIdLiveData() {
        return Transformations.switchMap(mCodeLisByConfigIdtLiveData, input ->
                model.getNextBoxGroupCode(mConfigId, mType, lastBoxEntity == null ? 0 : lastBoxEntity.getId(), currentPage, input,lastBoxEntity == null));
    }

    /**
     * 获取一组箱码 如果获取第一页并且获取到的数据为空 说明数据库中已经没有数据了 return
     * 如果当前页数为1 则获取的数据来源是新的一箱 记录这个箱码作为 最后一箱的箱码
     * 如果为空 并且不是第一页 说明上一次已经获取完 本次没有拿到数据 则需要换箱 并且去加载下一页
     * 如果 获取到的数据不足一组 说明需要换箱
     */
    public void updateData(List<BasePackageCodeEntity> list) {
        if (list.isEmpty() && currentPage == 1) {
            return;
        }
        if (currentPage == 1) {
            setLastBox(list.get(0));
        }
        if (list.isEmpty()) {
            setNeedChangeBox(true);
            loadNextPage();
            return;
        }
        int extraBoxCount = currentPage == 1 ? 1 : 0;
        int size = CustomRecyclerAdapter.ITEM_PAGE_SIZE + extraBoxCount;
        //获取到的数据不足一组
        //需要通知ViewModel 换箱
        setNeedChangeBox(list.size() < size);
    }

    public void setLastBox(BasePackageCodeEntity entity) {
        this.lastBoxEntity = entity;
    }

    public void setNeedChangeBox(boolean needChangeBox) {
        isNeedChangeBox = needChangeBox;
    }


    public void calculationTotal() {
        mCalculateTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Map<String, Integer>>> getCalculateTotalLiveData() {
        return Transformations.switchMap(mCalculateTotalLiveData, input -> model.calculationTotalByConfigId(mConfigId, mType));
    }

    public void clearData() {
        mClearDataLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<String>> getClearDataLiveData() {
        return Transformations.switchMap(mClearDataLiveData, input -> model.deleteAllByConfigId(mConfigId, mType));
    }

    public void getCodeInfo(String code) {
        mGetCodeInfoLiveData.setValue(code);
    }

    public LiveData<Resource<BasePackageCodeEntity>> getCodeInfoLiveData() {
        return Transformations.switchMap(mGetCodeInfoLiveData, input -> model.checkBox(input, mType));
    }

    /**
     * 删除父码
     */
    public void deleteBoxCode(String boxCode) {
        mDeleteBoxCodeLiveData.setValue(boxCode);
    }

    public LiveData<Resource<String>> getDeleteBoxCodeLiveData() {
        return Transformations.switchMap(mDeleteBoxCodeLiveData, input -> model.getRemoveAllByBoxCodeLiveDate(input, mType));
    }

    public void deleteChildCode(BasePackageCodeEntity childCode) {
        if (lastBoxEntity == null) {
            ToastUtils.showToast("箱码状态异常,删除失败,请尝试重新进入页面");
            return;
        }
        mDeleteChildCodeLiveData.setValue(childCode);
    }

    public LiveData<Resource<Map<String, BasePackageCodeEntity>>> getDeleteChildCodeLiveData() {
        return Transformations.switchMap(mDeleteChildCodeLiveData, input -> {
            int count = currentPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            return model.deleteChildCodeAndAddOne(TextUtils.equals(input.getParentCode(), lastBoxEntity.getCode()), input, count, mType);
        });
    }


    public void checkNeedChangeBoxByDeleteCode(String code) {
        BasePackageCodeEntity currentBox = lastBoxEntity;
        if (TextUtils.equals(code, currentBox.getCode())) {
            setNeedChangeBox(true);
        }
    }

    public ArrayList<BasePackageCodeEntity> getDeleteCodeListByBoxCode(String code) {
        ArrayList<BasePackageCodeEntity> tempList = new ArrayList<>();
        for (BasePackageCodeEntity e : mList) {
            if (TextUtils.equals(e.getParentCode(), code) || TextUtils.equals(e.getCode(), code)) {
                tempList.add(e);
            }
        }
        return tempList;
    }
}
