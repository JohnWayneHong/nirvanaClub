package com.jgw.delingha.module.logistics_statistics.statistics_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.bean.StatisticsResultBean;

import java.util.List;


public class StatisticsListViewModel extends BaseViewModel {

    private final StatisticsListModel model;
    private final MutableLiveData<StatisticsParamsBean> mListLiveData = new ValueKeeperLiveData<>();
    private int mType;
    private StatisticsParamsBean mData;
    private List<StatisticsResultBean> mList;
    private int mCurrentPage = 1;

    public StatisticsListViewModel(@NonNull Application application) {
        super(application);
        model = new StatisticsListModel();

    }

    public void setType(int type) {
        mType = type;
    }

    public void setDate(StatisticsParamsBean data) {
        mData = data;
    }


    public void setDataList(List<StatisticsResultBean> dataList) {
        mList = dataList;
    }

    public void loadList() {
        mListLiveData.setValue(mData);
    }

    public LiveData<Resource<List<StatisticsResultBean>>> getListLiveData() {
        return Transformations.switchMap(mListLiveData, new Function<StatisticsParamsBean, LiveData<Resource<List<StatisticsResultBean>>>>() {
            @Override
            public LiveData<Resource<List<StatisticsResultBean>>> apply(StatisticsParamsBean input) {
                switch (mType) {
                    case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_IN:
                        return model.getStatisticalStockInList(input, mCurrentPage);
                    case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_OUT:
                        return model.getStatisticalStockOutList(input, mCurrentPage);
                    case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_RETURN:
                        return model.getStatisticalStockReturnList(input, mCurrentPage);
                    case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_WAREHOUSE:
                        return model.getStatisticalExchangeWarehouseList(input, mCurrentPage);
                    case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_GOODS:
                        return model.getStatisticalExchangeGoodsList(input, mCurrentPage);
                }
                return null;
            }
        });
    }

    public String getTitle(int type) {
        String title = "";
        switch (type) {
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_IN:
                title = "入库统计";
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_OUT:
                title = "出库统计";
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_RETURN:
                title = "退货统计";
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_WAREHOUSE:
                title = "调仓统计";
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_GOODS:
                title = "调货统计";
                break;
        }
        return title;
    }


    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        loadList();
    }

    public void onRefresh() {
        mCurrentPage = 1;
        loadList();
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}
