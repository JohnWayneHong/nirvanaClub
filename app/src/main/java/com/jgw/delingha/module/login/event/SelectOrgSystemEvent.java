package com.jgw.delingha.module.login.event;

import com.jgw.delingha.bean.OrganizationBean;

import java.util.List;

public class SelectOrgSystemEvent {

    public static class getOrganizationListEvent {

        public List<OrganizationBean> organizationList;

        public getOrganizationListEvent(List<OrganizationBean> list) {
            organizationList = list;
        }
    }

    public static class submitOrgSysSuccessEvent {

//        public OrgAndSysBean orgAndSysBean;

        public submitOrgSysSuccessEvent() {
        }
    }
}
