package com.ggb.nirvanahappyclub.bean;

import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity;

import java.util.List;

public class BasicWordPhraseBean {
    private String phrase;
    private String translation;

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
