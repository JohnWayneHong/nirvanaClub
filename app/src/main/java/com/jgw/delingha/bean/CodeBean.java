package com.jgw.delingha.bean;

import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.UserEntity;

import io.objectbox.relation.ToOne;

/**
 * 本类现在预定为操作记录表 后期建立数据库后 码数据相关拆分至码表
 */
public class CodeBean extends BaseCodeEntity {

    public static final int STATUS_CODE_VERIFYING = -1;
    public static final int STATUS_CODE_SUCCESS = 1;
    public static final int STATUS_CODE_FAIL = 2;
    //outerCodeId就是码 服务端定义为此名字 所以同步保持一致
    public String outerCodeId;
    public int codeStatus;  // 本地定义:-1 正在验证  1 成功  2 失败

    public CodeBean() {
    }

    public CodeBean(String code) {
        outerCodeId = code;
    }

    /**
     * 码数据相关 后期移至码表
     * <p>
     * firstNumberUnitCode : 018101
     * firstNumberUnitName : 个
     * outerCodeId : 9000000261573909
     * productClassifyId : 05b87370e9954743a3050198f074661e
     * productClassifyName : 农业
     * productCode : CP000064
     * productId : a1ae60af393b4730af25d4e115f9a3b8
     * productName : 20191126
     * secondNumberUnitCode : 018201
     * secondNumberUnitName : 盒
     * status : 200
     * sweepLevel : 1
     * thirdNumberUnitCode : 018301
     * thirdNumberUnitName : 箱
     */

    public String firstNumberUnitCode;
    public String firstNumberUnitName;
    public int firstNumberUnitCount;
    public int secondNumberUnitCount;
    public int thirdNumberUnitCount;
    public String productClassifyId;
    public String productClassifyName;
    public String productCode;
    public String productId;
    public String productName;
    public String secondNumberUnitCode;
    public String secondNumberUnitName;
    public int sweepLevel;
    public String thirdNumberUnitCode;
    public String thirdNumberUnitName;
    public String errorMsg;
    public int singleNumber;

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getCode() {
        return outerCodeId;
    }

    @Override
    public void setCode(String code) {
        outerCodeId=code;
    }

    @Override
    public void setCodeStatus(int codeStatus) {
        this.codeStatus = codeStatus;
    }

    @Override
    public void setCodeLevel(int level) {
        sweepLevel=level;
    }

    @Override
    public int getCodeLevel() {
        return sweepLevel;
    }

    @Override
    public void setCodeLevelName(String unit) {

    }

    @Override
    public String getCodeLevelName() {
        return null;
    }

    @Override
    public void setSingleNumber(int number) {
        singleNumber=number;
    }

    @Override
    public int getCodeStatus() {
        return codeStatus;
    }

    public int getSingleNumber() {
        return singleNumber;
    }

    @Override
    public ToOne<UserEntity> getUserEntity() {
        return null;
    }
}
