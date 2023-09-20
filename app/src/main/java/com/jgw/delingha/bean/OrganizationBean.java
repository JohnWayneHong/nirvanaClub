package com.jgw.delingha.bean;

import java.util.List;

public class OrganizationBean {


    public List<ListBean> list;

    public static class ListBean {

        public String organizationCode;
        public String organizationFullName;
        public String organizationFullType;
        public String organizationId;
        public String logo;
    }
}
