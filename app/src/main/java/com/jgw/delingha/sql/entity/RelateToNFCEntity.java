package com.jgw.delingha.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;


/**
 * @author : J-T
 * @date : 2022/6/8 16:32
 * description : 关联NFC entity
 */
@Entity
public class RelateToNFCEntity extends BaseCodeEntity {
    @Id
    private long id;

    private String QRCode;

    private String NFCCode;

    private ToOne<UserEntity> userEntity;

    public long getId() {
        return this.id;
    }

    @Override
    public String getCode() {
        return getQRCode();
    }

    @Override
    public void setCode(String code) {
        setQRCode(code);
    }

    @Override
    public int getCodeStatus() {
        return 0;
    }

    @Override
    public void setCodeStatus(int status) {

    }

    @Override
    public void setCodeLevel(int level) {

    }

    @Override
    public int getCodeLevel() {
        return 0;
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

    }

    @Override
    public int getSingleNumber() {
        return 0;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQRCode() {
        return this.QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getNFCCode() {
        return this.NFCCode;
    }

    public void setNFCCode(String NFCCode) {
        this.NFCCode = NFCCode;
    }

    public ToOne<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(ToOne<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }
}
