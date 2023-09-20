package com.jgw.delingha.module.tools_table.model;

import static com.jgw.common_library.utils.ResourcesUtils.getString;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.LocalFunctionBean;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.bean.ToolsTableHeaderBean;
import com.jgw.delingha.module.tools_table.ToolsTableUtils;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.sql.ObjectBoxUtils;
import com.jgw.delingha.sql.entity.BasePackageCodeEntity;
import com.jgw.delingha.sql.entity.InWarehousePackageEntity;
import com.jgw.delingha.sql.entity.PackageStockInEntity;
import com.jgw.delingha.sql.entity.PackagingAssociationCustomEntity;
import com.jgw.delingha.sql.entity.PackagingAssociationEntity;
import com.jgw.delingha.sql.operator.BasePackageCodeOperator;
import com.jgw.delingha.sql.operator.InWarehousePackageOperator;
import com.jgw.delingha.sql.operator.PackageStockInOperator;
import com.jgw.delingha.sql.operator.PackagingAssociationCustomOperator;
import com.jgw.delingha.sql.operator.PackagingAssociationOperator;
import com.jgw.delingha.utils.ConstantUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XiongShaoWu
 * on 2019/11/19
 */
public class ToolsTableModel {

    public ToolsTableModel() {
    }

    public LiveData<Resource<ToolsTableHeaderBean>> getToolsTableHeaderData() {
        MutableLiveData<Resource<ToolsTableHeaderBean>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(new Function<String, ToolsTableHeaderBean>() {
                    @Override
                    public ToolsTableHeaderBean apply(@NotNull String s) throws Exception {
                        ToolsTableHeaderBean toolsTableHeaderBean = new ToolsTableHeaderBean();
                        toolsTableHeaderBean.appName = getString(R.string.supercode);
                        toolsTableHeaderBean.mobile = MMKVUtils.getString(ConstantUtil.USER_MOBILE);
                        toolsTableHeaderBean.systemName = MMKVUtils.getString(ConstantUtil.SYSTEM_NAME);
                        toolsTableHeaderBean.companyName = MMKVUtils.getString(ConstantUtil.ORGANIZATION_NAME);
                        //toolsTableHeaderBean.companyIcon = MMKVUtils.getString(MMKVUtils.ORGANIZATION_ICON);
                        toolsTableHeaderBean.companyNomalIcon = R.drawable.company_nomal_icon;
                        return toolsTableHeaderBean;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<ToolsTableHeaderBean>() {
                    @Override
                    public void onNext(@NotNull ToolsTableHeaderBean toolsTableHeaderBean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, toolsTableHeaderBean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.NETWORK_ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public void clearEmptyBoxCode() {
        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Class<?>>>) s -> Observable.fromIterable(ObjectBoxUtils.getPackageEntity()))
                .map((Function<Class<?>, BasePackageCodeOperator<?>>) this::switchPackageOperator)
                .map(operator -> {
                    operator.clearEmptyBox();
                    return operator.getClass().getName();
                }).subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        LogUtils.showLog("clear empty box=" + s);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        LogUtils.showLog("clear empty box onComplete");
                    }
                });
    }

    private BasePackageCodeOperator<?> switchPackageOperator(Class<?> aClass) {
        BasePackageCodeOperator<? extends BasePackageCodeEntity> operator = null;
        if (InWarehousePackageEntity.class.isAssignableFrom(aClass)) {
            operator = new InWarehousePackageOperator();
        } else if (PackageStockInEntity.class.isAssignableFrom(aClass)) {
            operator = new PackageStockInOperator();
        } else if (PackagingAssociationCustomEntity.class.isAssignableFrom(aClass)) {
            operator = new PackagingAssociationCustomOperator();
        } else if (PackagingAssociationEntity.class.isAssignableFrom(aClass)) {
            operator = new PackagingAssociationOperator();
        }
        return operator;
    }

    public LiveData<Resource<List<ToolsOptionsBean>>> getToolsTableListData() {
        MutableLiveData<Resource<List<ToolsOptionsBean>>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .getAllMenu()
                .compose(HttpUtils.applyMainSchedulers())
                .map(toolsOptionsBean -> {
                    JsonObject jsonObject = JsonUtils.parseObject(toolsOptionsBean);
                    List<ToolsOptionsBean> list = jsonObject.getJsonArray("list").toJavaList(ToolsOptionsBean.class);
                    return list;
                })
                .startWith(getMenuCacheObservable())
                .distinctUntilChanged()
                .map(list -> {
                    ArrayList<ToolsOptionsBean.MobileFunsBean> localMenu = new ArrayList<>();

                    for (int i = 0; i < list.size(); i++) {
                        ToolsOptionsBean toolsOptionsBean = list.get(i);
                        ArrayList<ToolsOptionsBean.MobileFunsBean> tempList = new ArrayList<>();
                        for (int j = 0; j < toolsOptionsBean.mobileFuns.size(); j++) {
                            ToolsOptionsBean.MobileFunsBean toolsOptionsDetailsBean = toolsOptionsBean.mobileFuns.get(j);
                            List<LocalFunctionBean> localFunctions = ToolsTableUtils.getLocalFunctions();
                            boolean hasLocalFunction = false;
                            for (LocalFunctionBean l : localFunctions) {
                                if (TextUtils.equals(toolsOptionsDetailsBean.appAuthCode, l.getAppAuthCode())) {
                                    hasLocalFunction = true;
                                    break;
                                }
                            }
                            if (!hasLocalFunction) {
                                continue;
                            }
                            //菜单权限码在本地不存在就不显示
                            tempList.add(toolsOptionsDetailsBean);
                            localMenu.add(toolsOptionsDetailsBean);
                        }
                        toolsOptionsBean.mobileFuns = tempList;
                    }
                    MMKVUtils.save(ConstantUtil.LOCAL_MENU, JsonUtils.toJsonString(localMenu));
                    MMKVUtils.save(ConstantUtil.HOME_MENU, JsonUtils.toJsonString(list));
                    return list;
                })
                .subscribe(new CustomObserver<List<ToolsOptionsBean>>() {

                    @Override
                    public void onNext(@NonNull List<ToolsOptionsBean> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                        if (!NetUtils.iConnected()) {
                            return;
                        }
                        super.onError(e);
                    }
                });
        return liveData;
    }

    private Observable<List<ToolsOptionsBean>> getMenuCacheObservable() {
        String json = MMKVUtils.getString(ConstantUtil.HOME_MENU);
        return Observable.just(json)
                .map(s -> {
                    List<ToolsOptionsBean> toolsOptionsBeans = JsonUtils.parseArray(s, ToolsOptionsBean.class);
                    List<ToolsOptionsBean> list = new ArrayList<>();
                    return toolsOptionsBeans == null ? list : toolsOptionsBeans;
                })
                .subscribeOn(Schedulers.io());
    }
}
