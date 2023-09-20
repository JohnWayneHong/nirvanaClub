package com.jgw.delingha.custom_module.delingha.breed.out.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.BreedOutDetailsBean;
import com.jgw.delingha.bean.BreedOutListBean;
import com.jgw.delingha.bean.CommonOrderStockOutDetailsBean;
import com.jgw.delingha.bean.CommonOrderStockOutListBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.module.order_task.TaskModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.BaseOrderEntity;
import com.jgw.delingha.sql.entity.BaseOrderScanCodeEntity;
import com.jgw.delingha.sql.entity.OrderStockOutEntity;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;
import com.jgw.delingha.sql.entity.OrderStockOutScanCodeEntity;
import com.jgw.delingha.sql.operator.OrderStockOutOperator;
import com.jgw.delingha.sql.operator.OrderStockOutProductOperator;
import com.jgw.delingha.sql.operator.OrderStockOutScanCodeOperator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.objectbox.relation.ToMany;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class BreedOutListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOrderStockOutListLiveData;
    private ValueKeeperLiveData<Resource<BreedOutDetailsBean>> mBreedOutDetailsLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteBreedOutLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedOutList(String search, int page) {
        if (mOrderStockOutListLiveData == null) {
            mOrderStockOutListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getBreedOutList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(BreedOutListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

    public LiveData<Resource<BreedOutDetailsBean>> getBreedOutDetails(String breedOutRecId) {
        if (mBreedOutDetailsLiveData == null) {
            mBreedOutDetailsLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getBreedOutDetails(breedOutRecId)
                .compose(HttpUtils.applyIOSchedulers())
                .subscribe(new CustomObserver<BreedOutDetailsBean>(mBreedOutDetailsLiveData) {});
        return mBreedOutDetailsLiveData;
    }

    public LiveData<Resource<String>> deleteBreedOut(String breedOutRecId) {
        if (mDeleteBreedOutLiveData == null) {
            mDeleteBreedOutLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .deleteBreedOut(breedOutRecId)
                .compose(HttpUtils.applyIOSchedulers())
                .subscribe(new CustomObserver<String>(mDeleteBreedOutLiveData) {});
        return mDeleteBreedOutLiveData;
    }
}
