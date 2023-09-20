package com.jgw.delingha.sql.entity;

import android.graphics.drawable.Drawable;

import androidx.databinding.Bindable;

import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;


/**
 * author : xsw
 * data : 2021/3/15
 * description : 入库订单产品信息
 */
@Entity
public class OrderStockOutProductInfoEntity extends BaseOrderProductEntity {

    @Id//注意：long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    private String taskId;//任务Id
    private String productName;//商品名称

    private String productCode;//商品编码

    private String productId;//商品ID

    private String outHouseProductId;//商品ID

    private String batchName;//批次名称

    private String batchId;//批次名称

    private String wareHouseId;//仓库id

    private String wareHouseName;//仓库名称

    private String wareHouseCode;//仓库编码

    private int planNumber;//计划数量

    private int scanCodeNumber;//实际扫码数量数量(已上传)

    private int currentInputSingleNumber;//当前手输数量
    private int tempInputSingleNumber;//临时手输数量上传成功后赋值
    private int currentSingleNumber;//当前扫码数量
    private String productRecordId;//产品记录id

    private ToOne<OrderStockOutEntity> orderStockOutEntity;

    @Backlink(to = "orderStockOutProductInfoEntity")
    private ToMany<OrderStockOutScanCodeEntity> codeList;

    private long createTime;

    private long indexId;

    public ToMany<OrderStockOutScanCodeEntity> getCodeList() {
        return codeList;
    }

    public void setCodeList(ToMany<OrderStockOutScanCodeEntity> codeList) {
        this.codeList = codeList;
    }

    public ToOne<OrderStockOutEntity> getOrderStockOutEntity() {
        return orderStockOutEntity;
    }

    public void setOrderStockOutEntity(ToOne<OrderStockOutEntity> orderStockOutEntity) {
        this.orderStockOutEntity = orderStockOutEntity;
    }

    public String getDataTime() {
        if (createTime == 0) {
            return "无添加时间";
        }
        return FormatUtils.formatTime(createTime);
    }

    public long getIndexId() {
        return this.indexId;
    }

    public void setIndexId(long indexId) {
        this.indexId = indexId;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return this.productCode;
    }

    @Override
    public String getProductBatchName() {
        return null;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public int getPlanNumber() {
        return this.planNumber;
    }

    public String getPlanNumberText() {
        return this.planNumber + "";
    }

    public void setPlanNumber(int planNumber) {
        this.planNumber = planNumber;
    }

    public int getScanCodeNumber() {
        return this.scanCodeNumber;
    }
    public String getScanCodeNumberText() {
        return this.scanCodeNumber+"";
    }

    public void setScanCodeNumber(int scanCodeNumber) {
        this.scanCodeNumber = scanCodeNumber;
    }

    public Drawable _getScanBackground() {
        return ResourcesUtils.getDrawable(R.drawable.radius96_blue);
    }

    @Bindable
    public String getCurrentNumber() {
        return getTotalScanCodeSingleNumber() + "";
    }

    @Override
    public List<? extends BaseOrderScanCodeEntity> getScanCodeList() {
        if (id == 0) {
            return new ArrayList<>();
        }
        codeList.reset();
        return codeList;
    }

    public String getWareHouseId() {
        return this.wareHouseId;
    }

    public void setWareHouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public String getWareHouseName() {
        return this.wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public String getWareHouseCode() {
        return this.wareHouseCode;
    }

    public void setWareHouseCode(String wareHouseCode) {
        this.wareHouseCode = wareHouseCode;
    }

    public String getOutHouseProductId() {
        return this.outHouseProductId;
    }

    public void setOutHouseProductId(String outHouseProductId) {
        this.outHouseProductId = outHouseProductId;
    }

    @Override
    public int getCurrentInputSingleNumber() {
        return currentInputSingleNumber;
    }

    public void setCurrentInputSingleNumber(int currentInputSingleNumber) {
        this.currentInputSingleNumber = currentInputSingleNumber;
    }

    public int getTempInputSingleNumber() {
        return tempInputSingleNumber;
    }

    public void setTempInputSingleNumber(int tempInputSingleNumber) {
        this.tempInputSingleNumber = tempInputSingleNumber;
    }

    public int getCurrentSingleNumber() {
        return currentSingleNumber;
    }

    public void setCurrentSingleNumber(int currentSingleNumber) {
        this.currentSingleNumber = currentSingleNumber;
    }

    public String getProductRecordId() {
        return productRecordId;
    }

    public void setProductRecordId(String productRecordId) {
        this.productRecordId = productRecordId;
    }

    public String getInputNumber() {
        return getCurrentInputSingleNumber() + "";
    }

    public void setFirstEnterNumber(long parseLong) {
        setCurrentSingleNumber((int) parseLong);
    }
    public int getFirstEnterNumber(){
       return getCurrentSingleNumber();
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
