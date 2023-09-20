package com.jgw.delingha.module.tools_table;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.LocalFunctionBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.custom_module.delingha.breed.ear_reset.EarResetListActivity;
import com.jgw.delingha.custom_module.delingha.breed.in.list.BreedInListActivity;
import com.jgw.delingha.custom_module.delingha.breed.out.list.BreedOutListActivity;
import com.jgw.delingha.custom_module.delingha.breed.task.list.BreedTaskListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.disinfect.list.DisinfectListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.energy.list.EnergyListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.exchange_pigsty.list.ExchangePigstyListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.feeding.list.FeedingListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.harmless.list.HarmlessListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.health_care.list.HealthCareListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.immunity.list.ImmunityListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.treatment.list.TreatmentListActivity;
import com.jgw.delingha.custom_module.delingha.daily_management.weight.list.WeightListActivity;
import com.jgw.delingha.custom_module.delingha.breed.enter_fence.list.EnterFenceListActivity;
import com.jgw.delingha.custom_module.delingha.order_stock_in.activity.OrderStockInListActivity;
import com.jgw.delingha.custom_module.delingha.order_stock_out.activity.OrderStockOutListActivity;
import com.jgw.delingha.custom_module.delingha.processing_approach.in.list.ProcessingApproachInListActivity;
import com.jgw.delingha.custom_module.delingha.slaughter.in.list.SlaughterInListActivity;
import com.jgw.delingha.custom_module.delingha.slaughter_weighing.list.SlaughterWeighingListActivity;
import com.jgw.delingha.custom_module.maogeping.packaging_association_custom.ui.PackagingAssociationCustomSettingActivity;
import com.jgw.delingha.custom_module.wanwei.stock_out.ui.WanWeiStockOutSettingActivity;
import com.jgw.delingha.custom_module.wanwei.stock_return.ui.WanWeiStockReturnSettingActivity;
import com.jgw.delingha.module.batch_management.ui.BatchManagementListActivity;
import com.jgw.delingha.module.disassemble.all.ui.DisassembleAllPDAActivity;
import com.jgw.delingha.module.disassemble.base.ui.DisassemblePDAActivity;
import com.jgw.delingha.module.exchange_goods.base.ui.ExchangeGoodsSettingActivity;
import com.jgw.delingha.module.exchange_goods.order.activity.OrderExchangeGoodsActivity;
import com.jgw.delingha.module.exchange_warehouse.ui.ExchangeWarehouseSettingActivity;
import com.jgw.delingha.module.fail_log.ui.FailLogMenuActivity;
import com.jgw.delingha.module.identification_replace.IdentificationReplaceActivity;
import com.jgw.delingha.module.inventory.activity.InventoryListActivity;
import com.jgw.delingha.module.label_edit.ui.LabelEditSettingActivity;
import com.jgw.delingha.module.logistics_statistics.exchange_goods_statistics.ExchangeGoodsStatisticsActivity;
import com.jgw.delingha.module.logistics_statistics.exchange_warehouse_statistics.ExchangeWarehouseStatisticsActivity;
import com.jgw.delingha.module.logistics_statistics.stock_in_statistics.StockInStatisticsActivity;
import com.jgw.delingha.module.logistics_statistics.stock_out_statistics.StockOutStatisticsActivity;
import com.jgw.delingha.module.logistics_statistics.stock_return_statistics.StockReturnStatisticsActivity;
import com.jgw.delingha.module.packaging.association.ui.PackagingAssociationSettingActivity;
import com.jgw.delingha.module.packaging.in_warehouse.ui.InWarehousePackageSettingActivity;
import com.jgw.delingha.module.packaging.statistics.activity.PackageStatisticsActivity;
import com.jgw.delingha.module.packaging.stock_in_packaged.ui.StockInPackagedSettingActivity;
import com.jgw.delingha.module.query.code_status.ui.CodeStatusQueryActivity;
import com.jgw.delingha.module.query.flow.ui.FlowQuerySettingActivity;
import com.jgw.delingha.module.query.related.ui.RelatedQueryPDAActivity;
import com.jgw.delingha.module.relate_to_nfc.activity.RelateToNFCActivity;
import com.jgw.delingha.module.stock_in.base.ui.StockInSettingActivity;
import com.jgw.delingha.module.stock_in.package_stock_in.ui.PackageStockInSettingActivity;
import com.jgw.delingha.module.stock_out.base.ui.StockOutSettingActivity;
import com.jgw.delingha.module.stock_out.order.activity.CommonOrderStockOutListActivity;
import com.jgw.delingha.module.stock_out.stock_out_fast.ui.StockOutFastSettingActivity;
import com.jgw.delingha.module.stock_return.ui.StockReturnSettingActivity;
import com.jgw.delingha.module.supplement_to_box.base.ui.SupplementToBoxActivity;
import com.jgw.delingha.module.supplement_to_box.produce.ui.ProduceSupplementToBoxActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.wait_upload_task.ui.WaitUploadMenuActivity;
import com.jgw.delingha.utils.ConstantUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XiongShaoWu
 * on 2019/10/11
 */
@SuppressWarnings("SpellCheckingInspection")
public class ToolsTableUtils {
    //包装管理
    public static final String BaoZhuangGuanLian = "package_connection";
    public static final String HunHeBaoZhuang = "package_connection_mix";
    public static final String BaoZhuangGuanLianMGP = "package_connection_mgp";
    public static final String ZhengZuChaiJie = "group_dismantling";
    public static final String DanGeChaiJie = "single_dismantling";
    public static final String BuMaRuXiang = "supplement_to_box";
    public static final String GuanLianChaXun = "connection_query";
    public static final String GuanLianChaXunGB = "connection_query_show_single";
    public static final String DaSanTaoBiao = "disassembleAll";
    public static final String ShengChanBuMa = "produce_supplement_to_box";
    public static final String BaoZhuangTongJi = "package_statistics";
    public static final String BiaoShiTiHuan = "identification_replace";
    public static final String GuanLianNFC = "relate_to_nfc";

    //物流
    public static final String RuKu = "StockIn";
    public static final String ShengChanRuKu = "StockInPackaged";
    public static final String BaoZhuangRuKu = "PackageStockIn";
    public static final String ChuKu = "StockOut";
    public static final String ZhiJieChuKu = "StockOutFast";
    public static final String TuiHuo = "StockReturn";
    public static final String LiuXiangChaXun = "FlowQuery";
    public static final String DiaoCang = "ExchangeWarehouse";
    public static final String DiaoHuo = "ExchangeGoods";
    public static final String ZaiKuGuanLian = "InWarehousePackage";
    public static final String BiaoShiJiuCuo = "LabelEdit";
    public static final String WuLiuPanDian = "LogisticsInventory";
    //德令哈使用定制订单出入库
//    public static final String DanJuRuKu = "OrderStockIn";
//    //单据出库(索契)
//    public static final String DanJuChuKu = "OrderStockOut";
    //通用单据出库
    public static final String TongYongDanJuChuKu = "CommonOrderStockOut";
    public static final String PiCiGuanLi = "BatchManagement";
    public static final String DingDanDiaoHuo = "OrderExchangeGoods";
    public static final String WanWeiChuKu = "WanWeiStockOut";
    public static final String WanWeiTuiHuo = "WanWeiStockReturn";
    public static final String MaZhuangTaiChaXun = "CodeStatusQuery";

    //消息管理
    public static final String DaiZhiXing = "work_todo_list";
    public static final String ShiBaiRiZhi = "failure_log";
    public static final String RenWuZhuangTai = "task_status_list";

    public static final String RuKuTongJi = "stock_in_statistics";
    public static final String ChuKuTongJi = "stock_out_statistics";
    public static final String TuiHuoTongJi = "stock_return_statistics";
    public static final String DiaoCangTongJi = "exchange_warehouse_statistics";
    public static final String DiaoHuoTongJi = "exchange_goods_statistics";

    //德令哈
    public static final String YangZhiJinChang = "dlh_breed_in";
    public static final String YangZhiLiChang = "dlh_breed_out";
    public static final String TuZaiJinChang = "dlh_slaughter_in";
    public static final String ZaiHouChengZhong = "dlh_slaughter_weighing";
    public static final String JiaGongJinChang = "dlh_processing_approach_in";

    public static final String SiWeiGuanLi = "dlh_feed";
    public static final String ChengZhongGuanLi = "dlh_weight";
    public static final String NengHaoGuanLi = "dlh_energy";
    public static final String ZhuanLanGuanLi = "dlh_exchange_pigsty";
    public static final String RuLanJiLu = "dlh_enter_fence";
    public static final String ErHaoGuanLi = "dlh_ear_manager";
    public static final String BaoJianJiLu = "dlh_health_care";
    public static final String MianYiJiLu = "dlh_immunity_manager";
    public static final String ZhenLiaoJiLu = "dlh_treatment_manager";
    public static final String XiaoDuJiLu = "dlh_disinfect_manager";
    public static final String WuHaiHuaJiLu = "dlh_harmless_manager";
    public static final String YangZhiRenWu = "dlh_breed_task";
    public static final String DLHDanJuRuKu = "dlh_order_stock_in";
    public static final String DLHDanJuChuKu = "dlh_order_stock_out";


    private static ArrayList<LocalFunctionBean> localFunctionBeans;


    /**
     * 跳转对应功能
     *
     * @param context  当前Activity
     * @param jumpType 跳转类型
     */
    public static void jumpToolTableOptionDetails(@NonNull Context context,
                                                  @NonNull String jumpType) {
        //菜单已经过滤本地菜单不可能为null
        Class<?> clazz = getLocalClass(jumpType);
        if (!BaseActivity.isActivityNotFinished(context)) {
            ToastUtils.showToast("跳转异常!");
            return;
        }
        Intent intent = new Intent(context, clazz);
        putArgs(jumpType, intent);
        context.startActivity(intent);
        putEvent(context, jumpType);
    }

    private static Class<?> getLocalClass(@NonNull String jumpType) {
        Class<?> clazz = null;
        List<LocalFunctionBean> localFunctions = getLocalFunctions();
        for (LocalFunctionBean l : localFunctions) {
            if (TextUtils.equals(l.getAppAuthCode(), jumpType)) {
                clazz = l.getFunctionClass();
                break;
            }
        }
        return clazz;
    }

    private static void putArgs(@NonNull String jumpType, Intent intent) {
        switch (jumpType) {
            case ZhengZuChaiJie:
                intent.putExtra("isSingleDisassemble", false);
                break;
            case DanGeChaiJie:
                intent.putExtra("isSingleDisassemble", true);
                break;
            case GuanLianChaXun:
                intent.putExtra("isShowSingle", false);
                break;
            case GuanLianChaXunGB:
                intent.putExtra("isShowSingle", true);
                break;
            case HunHeBaoZhuang:
                intent.putExtra("isMix", true);
                break;
        }
    }

    private static void putEvent(@NonNull Context context, @NonNull String jumpType) {
        Map<String, Object> info = new HashMap<>();
        info.put("function_count", jumpType);
        info.put("org_count", jumpType + "_" + MMKVUtils.getString(ConstantUtil.ORGANIZATION_NAME));
        info.put("function_details", jumpType + "_" + MMKVUtils.getString(ConstantUtil.ORGANIZATION_NAME) + "_" + AppConfig.CURRENT_VERSION);
        MobclickAgent.onEventObject(context, "function_event", info);
    }

    public static List<LocalFunctionBean> getLocalFunctions() {
        if (localFunctionBeans != null) {
            return localFunctionBeans;
        }
        localFunctionBeans = new ArrayList<>();
        LocalFunctionBean bean = new LocalFunctionBean();
        bean.setAppAuthCode(RuKu);
        bean.setFunctionClass(StockInSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ShengChanRuKu);
        bean.setFunctionClass(StockInPackagedSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BaoZhuangRuKu);
        bean.setFunctionClass(PackageStockInSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ChuKu);
        bean.setFunctionClass(StockOutSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ZhiJieChuKu);
        bean.setFunctionClass(StockOutFastSettingActivity.class);
        localFunctionBeans.add(bean);

//        bean = new LocalFunctionBean();
//        bean.setAppAuthCode(ZhiJieChuKuWuJiaoYan);
//        bean.setFunctionClass(StockOutFastUncheckSettingActivity.class);
//        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(TuiHuo);
        bean.setFunctionClass(StockReturnSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(LiuXiangChaXun);
        bean.setFunctionClass(FlowQuerySettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DiaoCang);
        bean.setFunctionClass(ExchangeWarehouseSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DiaoHuo);
        bean.setFunctionClass(ExchangeGoodsSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(RenWuZhuangTai);
        bean.setFunctionClass(TaskListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ShiBaiRiZhi);
        bean.setFunctionClass(FailLogMenuActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BaoZhuangGuanLian);
        bean.setFunctionClass(PackagingAssociationSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(HunHeBaoZhuang);
        bean.setFunctionClass(PackagingAssociationSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ZhengZuChaiJie);
        bean.setFunctionClass(DisassemblePDAActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DanGeChaiJie);
        bean.setFunctionClass(DisassemblePDAActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BuMaRuXiang);
        bean.setFunctionClass(SupplementToBoxActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(GuanLianChaXun);
        bean.setFunctionClass(RelatedQueryPDAActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(GuanLianChaXunGB);
        bean.setFunctionClass(RelatedQueryPDAActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DaiZhiXing);
        bean.setFunctionClass(WaitUploadMenuActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ZaiKuGuanLian);
        bean.setFunctionClass(InWarehousePackageSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BiaoShiJiuCuo);
        bean.setFunctionClass(LabelEditSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(WuLiuPanDian);
        bean.setFunctionClass(InventoryListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(TongYongDanJuChuKu);
        bean.setFunctionClass(CommonOrderStockOutListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BaoZhuangGuanLianMGP);
        bean.setFunctionClass(PackagingAssociationCustomSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(PiCiGuanLi);
        bean.setFunctionClass(BatchManagementListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DaSanTaoBiao);
        bean.setFunctionClass(DisassembleAllPDAActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ShengChanBuMa);
        bean.setFunctionClass(ProduceSupplementToBoxActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BaoZhuangTongJi);
        bean.setFunctionClass(PackageStatisticsActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DingDanDiaoHuo);
        bean.setFunctionClass(OrderExchangeGoodsActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(WanWeiChuKu);
        bean.setFunctionClass(WanWeiStockOutSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(WanWeiTuiHuo);
        bean.setFunctionClass(WanWeiStockReturnSettingActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(RuKuTongJi);
        bean.setFunctionClass(StockInStatisticsActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ChuKuTongJi);
        bean.setFunctionClass(StockOutStatisticsActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(TuiHuoTongJi);
        bean.setFunctionClass(StockReturnStatisticsActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DiaoCangTongJi);
        bean.setFunctionClass(ExchangeWarehouseStatisticsActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DiaoHuoTongJi);
        bean.setFunctionClass(ExchangeGoodsStatisticsActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(GuanLianNFC);
        bean.setFunctionClass(RelateToNFCActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(MaZhuangTaiChaXun);
        bean.setFunctionClass(CodeStatusQueryActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BiaoShiTiHuan);
        bean.setFunctionClass(IdentificationReplaceActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(YangZhiJinChang);
        bean.setFunctionClass(BreedInListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(YangZhiLiChang);
        bean.setFunctionClass(BreedOutListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(TuZaiJinChang);
        bean.setFunctionClass(SlaughterInListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(JiaGongJinChang);
        bean.setFunctionClass(ProcessingApproachInListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(RuLanJiLu);
        bean.setFunctionClass(EnterFenceListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ErHaoGuanLi);
        bean.setFunctionClass(EarResetListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(SiWeiGuanLi);
        bean.setFunctionClass(FeedingListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ChengZhongGuanLi);
        bean.setFunctionClass(WeightListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(NengHaoGuanLi);
        bean.setFunctionClass(EnergyListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ZhuanLanGuanLi);
        bean.setFunctionClass(ExchangePigstyListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ZaiHouChengZhong);
        bean.setFunctionClass(SlaughterWeighingListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(BaoJianJiLu);
        bean.setFunctionClass(HealthCareListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(MianYiJiLu);
        bean.setFunctionClass(ImmunityListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(ZhenLiaoJiLu);
        bean.setFunctionClass(TreatmentListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(XiaoDuJiLu);
        bean.setFunctionClass(DisinfectListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(WuHaiHuaJiLu);
        bean.setFunctionClass(HarmlessListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(YangZhiRenWu);
        bean.setFunctionClass(BreedTaskListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DLHDanJuRuKu);
        bean.setFunctionClass(OrderStockInListActivity.class);
        localFunctionBeans.add(bean);

        bean = new LocalFunctionBean();
        bean.setAppAuthCode(DLHDanJuChuKu);
        bean.setFunctionClass(OrderStockOutListActivity.class);
        localFunctionBeans.add(bean);


        return localFunctionBeans;
    }

}
