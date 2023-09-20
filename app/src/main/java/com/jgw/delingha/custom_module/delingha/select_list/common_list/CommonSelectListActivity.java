package com.jgw.delingha.custom_module.delingha.select_list.common_list;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class CommonSelectListActivity extends BaseSelectItemListActivity<CommonSelectListViewModel, ActivityCommonSelectListBinding> {
    /**
     * 母猪状态
     */
    public static final String SOW_PIG_STATUS = "sowpigstatus";
    /**
     * 母猪妊检结果
     */
    public static final String PREGNANCY_RESULT = "pregnancyresult";
    /**
     * 妊检方式
     */
    public static final String PREGNANCY_WAY = "pregnancyway";
    /**
     * 进场类型
     */
    public static final String IN_TYPE = "intype";
    /**
     * 养殖进场类型
     */
    public static final String BREED_IN_TYPE = "breedInType";
    /**
     * 养殖离场类型
     */
    public static final String BREED_OUT_TYPE = "breedOutType";
    /**
     * 养殖对象种类
     */
    public static final String ANIMALS_CLASSIFICATION = "animalsClassification";
    /**
     * 日常管理任务类型
     */
    public static final String BREED_TASK_TYPE = "daily";
    /**
     * 母猪生产事件
     */
    public static final String SOW_PRODUCTION_EVENT = "sowproductionevent";
    /**
     * 配种方式
     */
    public static final String SOW_BREEDING_METHOD = "sowbreedingmethod";
    /**
     * 肉猪状态
     */
    public static final String PORKER_PIG_STATUS = "porkerpigstatus";
    /**
     * 配方类型
     */
    public static final String FORMULA_TYPE = "formulatype";
    /**
     * 使用方式
     */
    public static final String USAGE_TYPE = "usagetype";
    /**
     * 转栏原因
     */
    public static final String ROLLOVER_REASON = "rolloverreason";
    /**
     * 母猪养殖方案状态
     */
    public static final String SOW_PIG_STATUS_PLAN = "sowpigstatusplan";
    /**
     * 标准养殖方案任务类型枚举
     */
    public static final String TASK_TYPE = "tasktype";
    /**
     * 呼吸疾病
     */
    public static final String RESPIRATORY_DISEASE = "899672d2ecc711e9b31c5254006b94c1";
    /**
     * 皮肤病
     */
    public static final String SKIN_DISEASE = "ceb60945ecc711e9b31c5254006b94c1";
    /**
     * 基地类型
     */
    public static final String BASE_TYPE = "basetype";
    /**
     * 消毒对象
     */
    public static final String DISINFECTION_OBJECT = "disinfectionobject";
    /**
     * 消毒方式
     */
    public static final String DISINFECTION_METHOD = "disinfectionmethod";
    /**
     * 发病程度
     */
    public static final String DISEASE_DEGREE = "diseasedegree";
    /**
     * 用药方式
     */
    public static final String MEDICATION = "medication";
    /**
     * 诊疗结果
     */
    public static final String DIAGNOSIS_TREATMENT_RESULTS = "diagnosistreatmentresults";
    /**
     * 处理或死亡原因
     */
    public static final String TREATMENT_OR_CAUSE_OF_DEATH = "treatmentorcauseofdeath";
    /**
     * 处理方式
     */
    public static final String PROCESSING_METHOD = "processingmethod";
    /**
     * 预防疾病
     */
    public static final String PREVENT_DISEASE = "preventdisease";
    /**
     * 免疫类型
     */
    public static final String TYPE_OF_IMMUNITY = "typeofimmunity";
    /**
     * 养殖场任务类型
     */
    public static final String FARM_TASK_TYPE = "farmtasktype";
    /**
     * 疾病种类和疾病名称
     */
    public static final String DISEASE = "disease";
    /**
     * 驱虫方式
     */
    public static final String INSECT_REPELLENT_METHOD = "Insectrepellentmethod";
    /**
     * 离场类型
     */
    public static final String LEAVE_TYPE = "leavetype";
    /**
     * 背膘测量方式
     */
    public static final String BACK_FAT_MEASUREMENT_METHOD = "backfatmeasurementmethod";
    /**
     * 离场状态
     */
    public static final String LEAVE_STATUS = "leavestatus";

    /**
     * 原料供应商枚举 只返回单挑原料供应商的分类id
     */
    public static final String SUPPLIERS = "suppliers";

    public static final String TAG = "CommonSelectListActivity";
    public static final String TAG_DATA = "CommonSelectListActivityData";

    @Override
    public String getExtraDataName() {
        return TAG_DATA;
    }

    @Override
    public String getExtraIDName() {
        return TAG;
    }

    @Override
    protected void initData() {
        String id = getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(id)) {
            setTitle(mViewModel.switchTitle(id));
            mViewModel.setId(id);
        }
        super.initData();
    }

    @Override
    public String getExtraName() {
        return null;
    }

    public static void start(int requestCode, Activity context, String id) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonSelectListActivity.class);
            intent.putExtra("id", id);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
