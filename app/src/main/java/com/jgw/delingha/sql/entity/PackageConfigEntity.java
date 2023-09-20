package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import com.jgw.common_library.utils.FormatUtils;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;


@Entity
public class PackageConfigEntity extends BaseEntity implements ConfigEntity{

    @Id
    private long id;

    private long createTime;

    private String packageSpecification; //产品规格

    private String number;//当前所选的规格数量(零箱包装数量不能大于产品规格)

    private String productBatchId;// 批次Id

    private String productBatchName;// 批次Name

    private String productBatchCode;// 批次code

    private String productCode;// 产品Code

    private String productId;// 产品Id

    private String productName;// 产品名称

    private String firstNumberCode;//上级规格Code

    private String firstNumberName;//上级规格Name

    private int firstNumber;//上级规格

    private String lastNumberCode;//下级规格Code

    private String lastNumberName;//下级规格Name

    private String remark;//备注

    private String taskId;//任务id 用来标识一批码 在任务中合并为同一任务

    private String wareHouseCode;

    private String wareHouseId;

    private String wareHouseName;

    private String storeHouseId; //库位ID

    private String storeHouseName; //库位名称

    //包装场景类型 0- 是生产包装 1- 是仓储包装 2-混合包装 3-补码入箱
    private int packageSceneType;

    @Transient
    private boolean isSelect;

    @Transient
    private long indexId;

    private ToOne<UserEntity> userEntity;

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public long getIndexId() {
        return indexId;
    }

    public void setIndexId(long indexId) {
        this.indexId = indexId;
    }

    public ToOne<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(ToOne<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPackageSpecification() {
        return this.packageSpecification;
    }

    public void setPackageSpecification(String packageSpecification) {
        this.packageSpecification = packageSpecification;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProductBatchId() {
        return this.productBatchId;
    }

    public void setProductBatchId(String productBatchId) {
        this.productBatchId = productBatchId;
    }

    public String getProductBatchName() {
        return this.productBatchName;
    }

    public void setProductBatchName(String productBatchName) {
        this.productBatchName = productBatchName;
    }

    public String getProductCode() {
        return this.productCode;
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

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFirstNumberCode() {
        return this.firstNumberCode;
    }

    public void setFirstNumberCode(String firstNumberCode) {
        this.firstNumberCode = firstNumberCode;
    }

    public String getFirstNumberName() {
        return this.firstNumberName;
    }

    public void setFirstNumberName(String firstNumberName) {
        this.firstNumberName = firstNumberName;
    }

    public int getFirstNumber() {
        return this.firstNumber;
    }

    public void setFirstNumber(int firstNumber) {
        this.firstNumber = firstNumber;
    }

    public String getLastNumberCode() {
        return this.lastNumberCode;
    }

    public void setLastNumberCode(String lastNumberCode) {
        this.lastNumberCode = lastNumberCode;
    }

    public String getLastNumberName() {
        return this.lastNumberName;
    }

    public void setLastNumberName(String lastNumberName) {
        this.lastNumberName = lastNumberName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }


    public String getProductText() {
        if (!TextUtils.isEmpty(productCode) && !TextUtils.isEmpty(productName)) {
            return "(" + productCode + ")" + productName;
        } else if (!TextUtils.isEmpty(productName)) {
            return productName;
        } else {
            return null;
        }
    }

    /**
     * 获取规格显示名称
     * 使用number显示当前设置规格
     *
     * @return 例: 1箱10盒  1盒10个
     */
    public String getSpecificationText() {
        return firstNumber + firstNumberName + number + lastNumberName;
    }

    /**
     * 上级码单位名称
     *
     * @return 例:箱码 盒码
     */
    public String getBoxCodeName() {
        return firstNumberName + "码";
    }

    /**
     * 下级码单位名称
     *
     * @return 例: 盒码 个码
     */
    public String getCodeName() {
        return lastNumberName + "码";
    }

    public String getSpecification() {
        return "1 : " + number;
    }

    public int getPackageLevel() {
        int level = 0;
        try {
            String substring = firstNumberCode.substring(3, 4);
            level = Integer.parseInt(substring);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }

    public String getAssociationLevel() {
        if (TextUtils.isEmpty(firstNumberName) || TextUtils.isEmpty(lastNumberName)) {
            return "无";
        }
        return firstNumberName + " : " + lastNumberName;
    }

    public String getCreateTimeText() {
        if (createTime == 0) {
            return "无添加时间";
        }
        return FormatUtils.formatTime(createTime);
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getWareHouseCode() {
        return this.wareHouseCode;
    }

    public void setWareHouseCode(String wareHouseCode) {
        this.wareHouseCode = wareHouseCode;
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

    public String getStoreHouseId() {
        return this.storeHouseId;
    }

    public void setStoreHouseId(String storeHouseId) {
        this.storeHouseId = storeHouseId;
    }

    public String getStoreHouseName() {
        return this.storeHouseName;
    }

    public void setStoreHouseName(String storeHouseName) {
        this.storeHouseName = storeHouseName;
    }

    public String getProductBatchCode() {
        return this.productBatchCode;
    }

    public void setProductBatchCode(String productBatchCode) {
        this.productBatchCode = productBatchCode;
    }

    public int getPackageSceneType() {
        return packageSceneType;
    }

    public void setPackageSceneType(int packageSceneType) {
        this.packageSceneType = packageSceneType;
    }
}
