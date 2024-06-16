package com.ggb.nirvanahappyclub.bean;

import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity;
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

public class BasicWordBean {
    private String word;
    private List<BasicWordTranslationBean> translations;
    private List<BasicWordPhraseBean> phrases;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<BasicWordTranslationBean> getTranslations() {
        return translations;
    }

    public void setTranslations(List<BasicWordTranslationBean> translations) {
        this.translations = translations;
    }

    public List<BasicWordPhraseBean> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<BasicWordPhraseBean> phrases) {
        this.phrases = phrases;
    }
}
