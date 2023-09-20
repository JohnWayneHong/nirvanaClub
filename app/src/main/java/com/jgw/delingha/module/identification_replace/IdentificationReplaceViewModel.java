package com.jgw.delingha.module.identification_replace;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.IdentificationReplaceBean;

/**
 * @author : J-T
 * @date : 2022/8/2 10:22
 * description :标识替换ViewModel
 */
public class IdentificationReplaceViewModel extends BaseViewModel {
    private final IdentificationReplaceModel mModel;
    private final IdentificationReplaceBean data;

    private final MutableLiveData<Integer> checkCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> identificationReplaceLiveData = new ValueKeeperLiveData<>();

    public IdentificationReplaceViewModel(@NonNull Application application) {
        super(application);
        mModel = new IdentificationReplaceModel();
        data = new IdentificationReplaceBean();
    }

    public IdentificationReplaceBean getData() {
        return data;
    }

    public void checkCode(String code, int type) {
        if (type == IdentificationReplaceActivity.POSITION_SOURCE) {
            //原身份码
            if (TextUtils.equals(code, data.getTargetCode())) {
                ToastUtils.showToast("(旧)物流码与(新)物流码不能相同!");
                return;
            }
            inputSourceCode(code);
        } else if (type == IdentificationReplaceActivity.POSITION_TARGET) {
            //新身份码
            if (TextUtils.equals(code, data.getSourceCode())) {
                ToastUtils.showToast("(旧)物流码与(新)物流码不能相同!");
                return;
            }
            inputTargetCode(code);
        }
        checkCode(type);

    }

    public void inputSourceCode(String c) {
        data.setSourceCode(c);
    }

    public void inputTargetCode(String c) {
        data.setTargetCode(c);
    }

    private void checkCode(int type) {
        checkCodeLiveData.setValue(type);
    }

    public void uploadIdentificationReplace() {
        identificationReplaceLiveData.setValue(null);
    }

    public LiveData<Resource<Integer>> getCheckCodeLiveData() {
        return Transformations.switchMap(checkCodeLiveData, input -> {
            if (input == IdentificationReplaceActivity.POSITION_SOURCE) {
                return mModel.checkCode(data.getSourceCode(), 1);
            } else {
                return mModel.checkCode(data.getTargetCode(), 2);
            }
        });
    }


    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(identificationReplaceLiveData,
                input -> mModel.upload(data.getSourceCode(), data.getTargetCode()));
    }


}
