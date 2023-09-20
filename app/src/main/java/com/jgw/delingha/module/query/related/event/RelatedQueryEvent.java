package com.jgw.delingha.module.query.related.event;

import com.jgw.delingha.bean.CodeRelationInfoResultBean;

public class RelatedQueryEvent {

    public static class ScanCodeSuccessEvent {

        public CodeRelationInfoResultBean bean;

        public ScanCodeSuccessEvent(CodeRelationInfoResultBean b) {
            bean = b;
        }
    }
}
