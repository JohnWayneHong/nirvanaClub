package com.ggb.nirvanahappyclub.bean;

import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity;

import java.util.List;

public class BasicWordTranslationBean {
    private String translation;
    private String type;

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
