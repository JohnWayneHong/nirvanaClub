package com.jgw.delingha.module.fail_log.model;

import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_EXCHANGE_GOODS;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_EXCHANGE_WAREHOUSE;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_IN_WAREHOUSE_PACKAGE;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_LABEL_EDIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_PACKAGE_STOCK_IN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_PACKAGING_ASSOCIATION;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_RELATE_TO_NFC;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_GROUP_SPLIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_IN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_OUT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_RETURN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_SINGLE_SPLIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FailLogMenuBean;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.module.tools_table.ToolsTableUtils;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FailLogMenuModel {

    public LiveData<Resource<List<FailLogMenuBean>>> getFainMenuBeanListData() {
        final MutableLiveData<Resource<List<FailLogMenuBean>>> resourceMutableLiveData = new ValueKeeperLiveData<>();
        Observable.just(1)
                .map(integer -> initListData())
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<FailLogMenuBean>>() {
                    @Override
                    public void onNext(List<FailLogMenuBean> failLogMenuBeans) {
                        resourceMutableLiveData.postValue(new Resource<>(Resource.SUCCESS, failLogMenuBeans, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        resourceMutableLiveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        resourceMutableLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }
                });
        return resourceMutableLiveData;
    }

    private List<FailLogMenuBean> initListData() {

        List<FailLogMenuBean> list = new ArrayList<>();

        String json = MMKVUtils.getString(ConstantUtil.LOCAL_MENU);
        List<ToolsOptionsBean.MobileFunsBean> localMenu = JsonUtils.parseArray(json, ToolsOptionsBean.MobileFunsBean.class);

        for (int i = 0; i < localMenu.size(); i++) {

            ToolsOptionsBean.MobileFunsBean bean = localMenu.get(i);
            int typeId = 0;
            int titleRes = 0;
            switch (bean.appAuthCode) {
                case ToolsTableUtils.RuKu:
                case ToolsTableUtils.ShengChanRuKu:
                    titleRes = R.string.fail_log_stock_in;
                    typeId = TYPE_TASK_STOCK_IN;
                    break;
                case ToolsTableUtils.ChuKu:
                case ToolsTableUtils.ZhiJieChuKu:
                    titleRes = R.string.fail_log_stock_out;
                    typeId = TYPE_TASK_STOCK_OUT;
                    break;
                case ToolsTableUtils.TuiHuo:
                    titleRes = R.string.fail_log_stock_return;
                    typeId = TYPE_TASK_STOCK_RETURN;
                    break;
                case ToolsTableUtils.DiaoCang:
                    titleRes = R.string.fail_log_exchange_warehouse;
                    typeId = TYPE_TASK_EXCHANGE_WAREHOUSE;
                    break;
                case ToolsTableUtils.DiaoHuo:
                    titleRes = R.string.fail_log_exchange_goods;
                    typeId = TYPE_TASK_EXCHANGE_GOODS;
                    break;
                case ToolsTableUtils.DanGeChaiJie:
                    titleRes = R.string.fail_log_single_split;
                    typeId = TYPE_TASK_STOCK_SINGLE_SPLIT;
                    break;
                case ToolsTableUtils.ZhengZuChaiJie:
                    titleRes = R.string.fail_log_group_split;
                    typeId = TYPE_TASK_STOCK_GROUP_SPLIT;
                    break;
                case ToolsTableUtils.BuMaRuXiang:
                    titleRes = R.string.fail_log_supplement_to_box;
                    typeId = TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX;
                    break;
                case ToolsTableUtils.BaoZhuangGuanLian:
                    titleRes = R.string.fail_log_packaging_association;
                    typeId = TYPE_TASK_PACKAGING_ASSOCIATION;
                    break;
                case ToolsTableUtils.BaoZhuangRuKu:
                    titleRes = R.string.fail_log_package_stock_in;
                    typeId = TYPE_TASK_PACKAGE_STOCK_IN;
                    break;
                case ToolsTableUtils.BiaoShiJiuCuo:
                    titleRes = R.string.fail_log_label_edit;
                    typeId = TYPE_TASK_LABEL_EDIT;
                    break;
                case ToolsTableUtils.ZaiKuGuanLian:
                    titleRes = R.string.fail_log_in_warehouse_package;
                    typeId = TYPE_TASK_IN_WAREHOUSE_PACKAGE;
                    break;
                case ToolsTableUtils.GuanLianNFC:
                    titleRes = R.string.fail_log_relate_to_nfc;
                    typeId = TYPE_TASK_RELATE_TO_NFC;
                    break;
            }
            if (titleRes == 0) {
                continue;
            }
            String image = bean.getIconUrl(60);
            boolean contains = false;
            for (int j = 0; j < list.size(); j++) {
                FailLogMenuBean failLogMenuBean = list.get(j);
                if (failLogMenuBean.titleRes == titleRes) {
                    contains = true;
                }
            }
            if (contains) {
                continue;
            }
            list.add(new FailLogMenuBean(titleRes, image, typeId));
        }
        return list;
    }

}
