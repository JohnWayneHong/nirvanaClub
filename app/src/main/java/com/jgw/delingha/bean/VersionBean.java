package com.jgw.delingha.bean;

/**
 * Created by XiongShaoWu
 * on 2019/10/28
 */
public class VersionBean {

    /**
     * lastestVersion : 1.0.0
     * lastestVersionCode : 2
     * forceUpdate : 0
     * updateDescription :
     * upgradeUrl : http://filetest.jgwcjm.com/6dbcc726838c417397af088f0e817967
     */

    public String lastestVersion;
    public int lastestVersionCode;
    public int forceUpdate;
    public String updateDescription;
    public String upgradeUrl;
    public int lastForcedVersion;//最近需要强更版本
    public boolean isCurrentVersion;//最近需要强更版本
}
