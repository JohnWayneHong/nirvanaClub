package com.jgw.delingha.bean;

public class OrgSystemBean {
    private String mOrganizationId;
    private String mSystemId;

    public OrgSystemBean(String organizationId, String systemId) {
        this.mOrganizationId = organizationId;
        this.mSystemId = systemId;
    }

    public String getmOrganizationId() {
        return mOrganizationId;
    }

    public String getmSystemId() {
        return mSystemId;
    }
}
