package com.jgw.delingha.module.wait_upload_task.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.bean.WaitUploadFunctionBean;
import com.jgw.delingha.bean.WaitUploadMenuBean;
import com.jgw.delingha.custom_module.maogeping.packaging_association_custom.ui.PackagingAssociationCustomWaitUploadListActivity;
import com.jgw.delingha.custom_module.wanwei.stock_out.ui.WanWeiStockOutWaitUploadListActivity;
import com.jgw.delingha.custom_module.wanwei.stock_return.ui.WanWeiStockReturnWaitUploadListActivity;
import com.jgw.delingha.module.disassemble.base.ui.DisassembleWaitUploadListActivity;
import com.jgw.delingha.module.exchange_goods.base.ui.ExchangeGoodsWaitUploadActivity;
import com.jgw.delingha.module.exchange_warehouse.ui.ExchangeWarehouseWaitUploadActivity;
import com.jgw.delingha.module.label_edit.ui.LabelEditWaitUploadListActivity;
import com.jgw.delingha.module.packaging.association.ui.PackagingAssociationWaitUploadListActivity;
import com.jgw.delingha.module.packaging.in_warehouse.ui.InWarehousePackageWaitUploadListActivity;
import com.jgw.delingha.module.packaging.stock_in_packaged.ui.StockInPackagedWaitUploadListActivity;
import com.jgw.delingha.module.stock_in.base.ui.StockInWaitUploadListActivity;
import com.jgw.delingha.module.stock_in.package_stock_in.ui.PackageStockInWaitUploadActivity;
import com.jgw.delingha.module.stock_out.base.ui.StockOutWaitUploadListActivity;
import com.jgw.delingha.module.stock_out.order.activity.CommonStockOutWaitUploadListActivity;
import com.jgw.delingha.module.stock_out.stock_out_fast.ui.StockOutFastWaitUploadListActivity;
import com.jgw.delingha.module.stock_return.ui.StockReturnWaitUploadListActivity;
import com.jgw.delingha.module.tools_table.ToolsTableUtils;
import com.jgw.delingha.sql.operator.BaseOperator;
import com.jgw.delingha.sql.operator.ExchangeGoodsOperator;
import com.jgw.delingha.sql.operator.ExchangeWarehouseOperator;
import com.jgw.delingha.sql.operator.GroupDisassembleOperator;
import com.jgw.delingha.sql.operator.InWarehousePackageOperator;
import com.jgw.delingha.sql.operator.LabelEditOperator;
import com.jgw.delingha.sql.operator.OrderStockOutOperator;
import com.jgw.delingha.sql.operator.PackageStockInOperator;
import com.jgw.delingha.sql.operator.PackagingAssociationCustomOperator;
import com.jgw.delingha.sql.operator.PackagingAssociationOperator;
import com.jgw.delingha.sql.operator.SingleDisassembleOperator;
import com.jgw.delingha.sql.operator.StockInOperator;
import com.jgw.delingha.sql.operator.StockInPackagedOperator;
import com.jgw.delingha.sql.operator.StockOutFastOperator;
import com.jgw.delingha.sql.operator.StockOutOperator;
import com.jgw.delingha.sql.operator.StockReturnOperator;
import com.jgw.delingha.sql.operator.WanWeiStockOutOperator;
import com.jgw.delingha.sql.operator.WanWeiStockReturnOperator;
import com.jgw.delingha.utils.CodeTypeUtils;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WaitUploadMenuModel {

    private List<WaitUploadFunctionBean> functionBeans;

    public LiveData<Resource<List<WaitUploadMenuBean>>> getListData() {
        ValueKeeperLiveData<Resource<List<WaitUploadMenuBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(s -> initListData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<WaitUploadMenuBean>>() {
                    @Override
                    public void onNext(List<WaitUploadMenuBean> pendingTaskMenuBeans) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, pendingTaskMenuBeans, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });

        return liveData;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private List<WaitUploadMenuBean> initListData() {

        String json = MMKVUtils.getString(ConstantUtil.LOCAL_MENU);
        List<ToolsOptionsBean.MobileFunsBean> localMenu = JsonUtils.parseArray(json, ToolsOptionsBean.MobileFunsBean.class);

        //noinspection ConstantConditions
        List<WaitUploadMenuBean> list = getTaskMenuList(localMenu);
        return list;
    }

    private List<WaitUploadMenuBean> getTaskMenuList(List<ToolsOptionsBean.MobileFunsBean> localMenu) {
        List<WaitUploadMenuBean> list = new ArrayList<>();
        for (int i = 0; i < localMenu.size(); i++) {
            ToolsOptionsBean.MobileFunsBean bean = localMenu.get(i);
            List<WaitUploadFunctionBean> functionBeans = getHasWaitUploadFunctions();
            WaitUploadFunctionBean functionBean = null;
            for (WaitUploadFunctionBean w : functionBeans) {
                if (TextUtils.equals(w.getAppAuthCode(), bean.appAuthCode)) {
                    functionBean = w;
                    break;
                }
            }
            if (functionBean == null) {
                continue;
            }
            boolean isEmpty;
            BaseOperator<?> operator = functionBean.getOperator();
            if (TextUtils.equals(functionBean.getAppAuthCode(), ToolsTableUtils.HunHeBaoZhuang)
                    && (operator instanceof PackagingAssociationOperator)) {
                isEmpty = ((PackagingAssociationOperator) operator).queryCountByType(CodeTypeUtils.MixPackageType) == 0;
            } else if (TextUtils.equals(functionBean.getAppAuthCode(), ToolsTableUtils.BaoZhuangGuanLian)
                    && (operator instanceof PackagingAssociationOperator)) {
                isEmpty = ((PackagingAssociationOperator) operator).queryCountByType(CodeTypeUtils.PackageAssociationType) == 0;
            } else {
                isEmpty = functionBean.getOperator().queryCount() == 0;
            }
            WaitUploadMenuBean waitUploadMenuBean = new WaitUploadMenuBean();
            waitUploadMenuBean.setEmpty(isEmpty);
            waitUploadMenuBean.setAppAuthCode(functionBean.getAppAuthCode());
            waitUploadMenuBean.setFunctionClass(functionBean.getFunctionClass());
            waitUploadMenuBean.setIcon(bean.getIconUrl(60));
            waitUploadMenuBean.setTitle(bean.funName);
            list.add(waitUploadMenuBean);
        }
        return list;
    }

    /**
     * 本地已支持的待上传功能对象
     *
     * @return list
     */
    private List<WaitUploadFunctionBean> getHasWaitUploadFunctions() {
        if (functionBeans != null) {
            return functionBeans;
        }
        functionBeans = new ArrayList<>();
        WaitUploadFunctionBean bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.RuKu);
        bean.setFunctionClass(StockInWaitUploadListActivity.class);
        bean.setOperator(new StockInOperator());
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.ShengChanRuKu);
        bean.setOperator(new StockInPackagedOperator());
        bean.setFunctionClass(StockInPackagedWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.ChuKu);
        bean.setOperator(new StockOutOperator());
        bean.setFunctionClass(StockOutWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.ZhiJieChuKu);
        bean.setOperator(new StockOutFastOperator());
        bean.setFunctionClass(StockOutFastWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.TuiHuo);
        bean.setOperator(new StockReturnOperator());
        bean.setFunctionClass(StockReturnWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.DiaoCang);
        bean.setOperator(new ExchangeWarehouseOperator());
        bean.setFunctionClass(ExchangeWarehouseWaitUploadActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.DiaoHuo);
        bean.setOperator(new ExchangeGoodsOperator());
        bean.setFunctionClass(ExchangeGoodsWaitUploadActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.DanGeChaiJie);
        bean.setOperator(new SingleDisassembleOperator());
        bean.setFunctionClass(DisassembleWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.ZhengZuChaiJie);
        bean.setOperator(new GroupDisassembleOperator());
        bean.setFunctionClass(DisassembleWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.BaoZhuangGuanLian);
        bean.setOperator(new PackagingAssociationOperator());
        bean.setFunctionClass(PackagingAssociationWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.HunHeBaoZhuang);
        bean.setOperator(new PackagingAssociationOperator());
        bean.setFunctionClass(PackagingAssociationWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.BaoZhuangRuKu);
        bean.setOperator(new PackageStockInOperator());
        bean.setFunctionClass(PackageStockInWaitUploadActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.BiaoShiJiuCuo);
        bean.setOperator(new LabelEditOperator());
        bean.setFunctionClass(LabelEditWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.ZaiKuGuanLian);
        bean.setOperator(new InWarehousePackageOperator());
        bean.setFunctionClass(InWarehousePackageWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.BaoZhuangGuanLianMGP);
        bean.setOperator(new PackagingAssociationCustomOperator());
        bean.setFunctionClass(PackagingAssociationCustomWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.WanWeiChuKu);
        bean.setOperator(new WanWeiStockOutOperator());
        bean.setFunctionClass(WanWeiStockOutWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.WanWeiTuiHuo);
        bean.setOperator(new WanWeiStockReturnOperator());
        bean.setFunctionClass(WanWeiStockReturnWaitUploadListActivity.class);
        functionBeans.add(bean);

        bean = new WaitUploadFunctionBean();
        bean.setAppAuthCode(ToolsTableUtils.TongYongDanJuChuKu);
        bean.setOperator(new OrderStockOutOperator());
        bean.setFunctionClass(CommonStockOutWaitUploadListActivity.class);
        functionBeans.add(bean);

        return functionBeans;
    }


    public boolean hasExistWaitUploadData() {
        List<WaitUploadFunctionBean> hasWaitUploadFunctions = getHasWaitUploadFunctions();
        for (WaitUploadFunctionBean w : hasWaitUploadFunctions) {
            long count = w.getOperator().queryCount();
            if (count > 0) {
                return true;
            }
        }
        return false;
    }
}
