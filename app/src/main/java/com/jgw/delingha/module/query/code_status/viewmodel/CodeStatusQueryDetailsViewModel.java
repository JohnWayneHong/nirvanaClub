package com.jgw.delingha.module.query.code_status.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.CodeStatusQueryDetailsLabelBean;
import com.jgw.delingha.bean.CodeStatusQueryInfoItemBean;
import com.jgw.delingha.module.query.code_status.model.CodeStatusQueryDetailModel;
import com.jgw.delingha.module.query.code_status.ui.CodeStatusQueryDetailsActivity;

import java.util.List;

/**
 * @author : J-T
 * @date : 2022/7/21 11:19
 * description : 扫码状态查询ViewModel
 */
public class CodeStatusQueryDetailsViewModel extends BaseViewModel {
    private final CodeStatusQueryDetailModel mModel;
    private String code;

    private final MutableLiveData<Integer> codeInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Integer> labelLiveData = new ValueKeeperLiveData<>();

    public CodeStatusQueryDetailsViewModel(@NonNull Application application) {
        super(application);
        mModel = new CodeStatusQueryDetailModel();
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void initLabel() {
        labelLiveData.setValue(null);
    }

    public LiveData<Resource<List<CodeStatusQueryDetailsLabelBean>>> getLabelListLiveData() {
        return Transformations.switchMap(labelLiveData, input -> mModel.getLabelList());
    }

    /**
     * position 0-4 对应 入库 出库 调仓 调货 退货
     */
    public void getCodeStatusInfo(int position) {
        codeInfoLiveData.postValue(position);
    }

    public LiveData<Resource<List<CodeStatusQueryInfoItemBean>>> getCodeInfoLiveData() {
        return Transformations.switchMap(codeInfoLiveData, input -> {
            String key;
            switch (input) {
                case CodeStatusQueryDetailsActivity.TYPE_STOCK_IN:
                    key = CodeStatusQueryDetailModel.STOCK_IN_LIST;
                    break;
                case CodeStatusQueryDetailsActivity.TYPE_STOCK_OUT:
                    key = CodeStatusQueryDetailModel.STOCK_OUT_LIST;
                    break;
                case CodeStatusQueryDetailsActivity.TYPE_EXCHANGE_WAREHOUSE:
                    key = CodeStatusQueryDetailModel.CHANGE_WAREHOUSE_LIST;
                    break;
                case CodeStatusQueryDetailsActivity.TYPE_EXCHANGE_GOODS:
                    key = CodeStatusQueryDetailModel.CHANGE_GOODS_LIST;
                    break;
                case CodeStatusQueryDetailsActivity.TYPE_STOCK_RETURN:
                    key = CodeStatusQueryDetailModel.STOCK_RETURN_LIST;
                    break;
                default:
                    key = CodeStatusQueryDetailModel.STOCK_IN_LIST;
            }
            return mModel.getInfo(code, key);
        });
    }
}
