package com.jgw.delingha.module.login.event;

import com.jgw.delingha.bean.OrgAndSysBean;

public class LoginEvent {

    public static class LoginSuccessEvent {

        public String token;
        public String mobile;
        public String password;

        public LoginSuccessEvent(String t, String m, String p) {
            token = t;
            mobile = m;
            password = p;
        }
    }

    public static class submitOrgSysSuccessEvent {

        public OrgAndSysBean orgAndSysBean;

        public submitOrgSysSuccessEvent(OrgAndSysBean bean) {
            orgAndSysBean = bean;
        }
    }
}
