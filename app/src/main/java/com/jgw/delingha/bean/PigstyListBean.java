package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class PigstyListBean implements SelectItemSupport {

    private String massIfId;
    private String massId;
    private String massIfName;
    private Object massArea;
    private String areaUnit;
    private Object areaUnitName;
    private String baseId;
    private String baseName;
    private String principal;
    private String principalName;
    private Object contactsTel;
    private String organizationFullName;
    private Object qrCode;
    private String sortName;
    private String sortId;
    private int disableFlag;
    private String disableFlagName;
    private String latlog;
    private String baseAddress;
    private String partitionName;
    private String associationTypeId;
    private int associationType;
    private Object capacity;

    public String getMassIfId() {
        return massIfId;
    }

    public void setMassIfId(String massIfId) {
        this.massIfId = massIfId;
    }

    public String getMassId() {
        return massId;
    }

    public void setMassId(String massId) {
        this.massId = massId;
    }

    public String getMassIfName() {
        return massIfName;
    }

    public void setMassIfName(String massIfName) {
        this.massIfName = massIfName;
    }

    public Object getMassArea() {
        return massArea;
    }

    public void setMassArea(Object massArea) {
        this.massArea = massArea;
    }

    public String getAreaUnit() {
        return areaUnit;
    }

    public void setAreaUnit(String areaUnit) {
        this.areaUnit = areaUnit;
    }

    public Object getAreaUnitName() {
        return areaUnitName;
    }

    public void setAreaUnitName(Object areaUnitName) {
        this.areaUnitName = areaUnitName;
    }

    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public Object getContactsTel() {
        return contactsTel;
    }

    public void setContactsTel(Object contactsTel) {
        this.contactsTel = contactsTel;
    }

    public String getOrganizationFullName() {
        return organizationFullName;
    }

    public void setOrganizationFullName(String organizationFullName) {
        this.organizationFullName = organizationFullName;
    }

    public Object getQrCode() {
        return qrCode;
    }

    public void setQrCode(Object qrCode) {
        this.qrCode = qrCode;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public int getDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(int disableFlag) {
        this.disableFlag = disableFlag;
    }

    public String getDisableFlagName() {
        return disableFlagName;
    }

    public void setDisableFlagName(String disableFlagName) {
        this.disableFlagName = disableFlagName;
    }

    public String getLatlog() {
        return latlog;
    }

    public void setLatlog(String latlog) {
        this.latlog = latlog;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    public String getAssociationTypeId() {
        return associationTypeId;
    }

    public void setAssociationTypeId(String associationTypeId) {
        this.associationTypeId = associationTypeId;
    }

    public int getAssociationType() {
        return associationType;
    }

    public void setAssociationType(int associationType) {
        this.associationType = associationType;
    }

    public Object getCapacity() {
        return capacity;
    }

    public void setCapacity(Object capacity) {
        this.capacity = capacity;
    }

    @Override
    public String getShowName() {
        return getMassIfName();
    }

    @Override
    public String getStringItemId() {
        return getMassId();
    }

    @Override
    public long getItemId() {
        return 0;
    }
}
