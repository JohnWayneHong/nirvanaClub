package com.jgw.delingha.module.exchange_goods.order.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.bean.OrderExchangeGoodsDetailsBean;
import com.jgw.delingha.bean.OrderExchangeGoodsResultBean;
import com.jgw.delingha.module.exchange_goods.order.model.OrderExchangeGoodsDetailsModel;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderExchangeGoodsDetailsViewModel extends BaseViewModel {

    private final OrderExchangeGoodsDetailsModel model;
    private List<OrderExchangeGoodsDetailsBean.ProductsBean> mList;

    private final MutableLiveData<String> orderCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> uploadLiveData = new MutableLiveData<>();
    private String mHouseList;

    public OrderExchangeGoodsDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new OrderExchangeGoodsDetailsModel();
    }

    public void setHouseList(String mHouseList) {
        this.mHouseList = mHouseList;
    }

    public void setList(List<OrderExchangeGoodsDetailsBean.ProductsBean> dataList) {
        mList=dataList;
    }

    public LiveData<OrderExchangeGoodsDetailsBean> getOrderDetails() {
        return Transformations.switchMap(orderCodeLiveData, new Function<String, LiveData<OrderExchangeGoodsDetailsBean>>() {
            @Override
            public LiveData<OrderExchangeGoodsDetailsBean> apply(String input) {
                return model.getOrderDetails(input);
            }
        });
    }

    public LiveData<Resource<OrderExchangeGoodsResultBean>> getUploadLiveData() {
        return Transformations.switchMap(uploadLiveData, new Function<String, LiveData<Resource<OrderExchangeGoodsResultBean>>>() {
            @Override
            public LiveData<Resource<OrderExchangeGoodsResultBean>> apply(String input) {
                return model.postOrderDetails(input, mList);
            }
        });
    }

    public void setOrderCode(String data) {
        orderCodeLiveData.setValue(data);

    }

    public void updateOrderDetailsItem(int index, String json,CustomRecyclerAdapter<OrderExchangeGoodsDetailsBean.ProductsBean> adapter) {
        if (index == -1) {
            return;
        }
        List<OrderExchangeGoodsCodeBean> codeBeans = JsonUtils.parseArray(json, OrderExchangeGoodsCodeBean.class);
        if (codeBeans != null) {
            OrderExchangeGoodsDetailsBean.ProductsBean  productsBean = adapter.getContentItemData(index);
            productsBean.codeList.clear();
            productsBean.codeList.addAll(codeBeans);
            productsBean.scanSingleCodeNumber = getSingleCodeCount(codeBeans);
        }
        adapter.notifyItemChanged(index);
    }

    private int getSingleCodeCount(List<OrderExchangeGoodsCodeBean> list) {
        int size = 0;
        for (int j = 0; j < list.size(); j++) {
            OrderExchangeGoodsCodeBean temp = list.get(j);
            size += temp.singleCodeNumber;
        }
        return size;
    }

    public int getAllSingleCodeCount() {
        int size = 0;
        for (int i = 0; i < mList.size(); i++) {
            List<OrderExchangeGoodsCodeBean> list = mList.get(i).codeList;
            for (int j = 0; j < list.size(); j++) {
                OrderExchangeGoodsCodeBean temp = list.get(j);
                size += temp.singleCodeNumber;
            }
        }

        return size;
    }

    public List<OrderExchangeGoodsDetailsBean.ProductsBean> getList() {
        return mList;
    }

    public void upload() {
        if (getAllSingleCodeCount() < 1) {
            ToastUtils.showToast("请先扫码");
            return;
        }
        uploadLiveData.setValue(mHouseList);
    }

}
