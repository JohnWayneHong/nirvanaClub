package com.jgw.delingha.bean;

import java.io.Serializable;

public class OrgAndSysBean {

    /**
     * accountId : 2ea32afce4644167bd052cb0131b1b31
     * mobileId : 13000000006
     * userName : 零六
     * mailbox : null
     * weChatId : null
     * alipayId : null
     * loginState : 10000000
     * registDate : 2019-04-15 11:40:35
     * latelyLoginDate : 2019-06-21 18:39:06
     * vaildTime : 2100-01-01 00:00:00
     * disableStatus : null
     * isActivated : 1
     * token : 9fc322d7dd2d4fe1a06ed3a29cab4ab9
     * organizationCache : {"organizationId":"18355c8780094ca1b786464fb652f471","organizationFullName":"天门天升畜禽有限责任公司","organizationAbbr":null,"organizationFullType":"有限责任公司(自然人投资或控股)","supOrganizationId":null,"busRegistrationNumber":"4290062201740","socUnifiedCreditCode":"914290067905543063","organizationCode":"790554306","taxpayerCode":null,"companyType":"003002","companyTypeName":"企业","oneIndustryCode":null,"oneIndustryName":null,"twoIndustryCode":null,"twoIndustryName":null,"threeIndustryCode":null,"threeIndustryName":null,"dateOfApproval":"2006-06-23","registeredCapital":"0","scopeOfOperation":"畜禽养殖","street":null,"detailAddress":"天门市小板镇罗湖村","jingWeiDu":null,"contactsMan":"邓荣丹","contactsMobile":"18107252988","managePassword":null,"organizationAuditstatus":"1","activateStatus":"1","businessId":null,"isMergeCert":null,"createTime":"2019-04-15 11:53:29","userId":"c227b286b54e4b8f986e52a78e75e50c","province":null,"city":null,"county":null,"provinceName":null,"cityName":null,"countyName":null,"townShipCode":null,"townShipName":null,"logo":"8998262fd69341e29ca5bb62aa9ec9a8","seniorFunFlag":"1100000000000000","organizationIntroduction":"123","industry":null,"business":null,"fromTime":"2005-12-28 00:00:00","toTime":null,"legalPersonPhone":"18107252988","legalPerson":"邓荣丹","sysCache":{"sysId":"f2d635127b9f43718d0aee2054706fc1","sysName":"新系统","createUser":"2ea32afce4644167bd052cb0131b1b31","sysKey":null,"supSysId":null,"inOutFlag":null,"appType":null,"sysUrl":"http://system.one.kf315.net","iconUrlId":null,"disableFlag":1,"sysManagerUrl":null,"description":null,"sysCode":null,"applicationId":"143","applicationName":"新系统的应用","createByAccount":"18268932378_8757"},"lastSysCache":null,"supOrganizationCache":null}
     * lastOrganizationCache : null
     * sysCache : null
     * lastSysCache : null
     * isAdminLogin : false
     * logo : b02ccdb02d41431aa599c9e619ae13f7
     */

    public String accountId;
    public String userId;
    public String mobileId;
    public String userName;
    public String mailbox;
    public String weChatId;
    public String alipayId;
    public String loginState;
    public String registDate;
    public String latelyLoginDate;
    public String vaildTime;
    public String disableStatus;
    public int isActivated;
    public String token;
    public String lastOrganizationCache;
    public String sysCache;
    public String lastSysCache;
    public boolean isAdminLogin;
    public String logo;

    public OrganizationCacheBean organizationCache;

    public static class OrganizationCacheBean implements Serializable {
        /**
         * organizationId : 93b92cb1b3164e42b80250c0c9772b6e
         * organizationFullName : 普洱祖祥高山茶园有限公司
         * organizationAbbr : 祖祥高山
         * organizationFullType : 有限责任公司(自然人独资)
         * supOrganizationId : 4d8cfd9fc8d540eb84d2756a8d681c1d
         * busRegistrationNumber : 532700100002368
         * socUnifiedCreditCode : 915308007873659268
         * organizationCode : 787365926
         * taxpayerCode : 915308007873659268
         * companyType : 003002
         * companyTypeName : 企业
         * oneIndustryCode : 5
         * oneIndustryName : 农、林、牧、渔业
         * twoIndustryCode : 501
         * twoIndustryName : 农业服务业
         * threeIndustryCode : 51
         * threeIndustryName : 农业服务业
         * dateOfApproval : 2022-07-21 00:00:00
         * registeredCapital : 3000万人民币
         * scopeOfOperation : 茶叶的种植、生产、销售，进出口贸易；茶叶相关制品（调味茶、代用茶）的生产、销售、进出口贸易。（依法须经批准的项目，经相关部门批准后方可开展经营活动）
         * street :
         * detailAddress : 云南省普洱市思茅区南屏镇整碗村
         * jingWeiDu :
         * contactsMan : 董祖祥
         * contactsMobile : 0879-2155058
         * managePassword :
         * organizationAuditstatus : 0
         * activateStatus : 1
         * businessId :
         * isMergeCert : 0
         * createTime : 2006-04-25 00:00:00
         * userId : 5da65aee43bc41f683af6ce859685375
         * province : 530000
         * city : 530800
         * county : 530802
         * provinceName : 云南省
         * cityName : 普洱市
         * countyName : 思茅区
         * townShipCode : 530802101000
         * townShipName : 思茅镇
         * logo : https://img5.tianyancha.com/logo/lll/d1becc189fe26d47a52a0ce3fecb84fe.png@!f_200x200
         * seniorFunFlag : 1101110000000000
         * organizationIntroduction :
         * industry : null
         * business : 2006-04-25 00:00:00 ~ 未公示
         * fromTime : 2006-04-25 00:00:00
         * toTime : 未公示
         * legalPersonPhone : 0879-2155058
         * legalPerson : 董祖祥
         * orgLevel : null
         * organizationType :
         * orgUsageType : 0
         * sysCache : {"sysId":"e99f6dae75324e19aefef1aa96b5adfe","sysName":"思茅区茶企服务管理系统","createUser":"admit","appType":"","sysUrl":"http://simao-tea.kf315.net/sys/enterprise","iconUrlId":"","disableFlag":1,"description":"","applicationId":null,"applicationName":null,"createByAccount":null,"title":"思茅茶企服务系统","userRoleIds":null}
         * lastSysCache : null
         * supOrganizationCache : null
         * employeeCache : {"employeeId":"7eb6cd462e3d407182c83f70f5bcef11","employeeName":"温存11","userId":"cd6b7c45174a486580374231e91b1c9d","departmentId":"77132aa7325f43708112f5d94bfccb79","departmentName":"普洱祖祥高山茶园"}
         */
        public EmployeeCacheBean employeeCache;

        public static class EmployeeCacheBean implements Serializable {
            /**
             * employeeId : 7eb6cd462e3d407182c83f70f5bcef11
             * employeeName : 温存11
             * userId : cd6b7c45174a486580374231e91b1c9d
             * departmentId : 77132aa7325f43708112f5d94bfccb79
             * departmentName : 普洱祖祥高山茶园
             */
            public String employeeName;
            public String employeeId;

        }
    }
}
