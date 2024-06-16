package com.ggb.nirvanahappyclub.sql.entity;

import androidx.databinding.Bindable;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * author : hwj
 * data : 2024年6月2日 19:36:10
 * description : 数据库 初级单词 词组表
 */
@Entity
public class BasicWordPhraseEntity extends BaseEntity {
    @Id
    public long id;

    public String phrase;
    public String translation;
    private ToOne<BasicWordEntity> wordEntity;

    public BasicWordPhraseEntity() {
        // Required no-arg constructor
    }

    public BasicWordPhraseEntity(String phrase) {
        this.phrase = phrase;
    }

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

    public ToOne<BasicWordEntity> getWordEntity() {
        return wordEntity;
    }

    public void setWordEntity(ToOne<BasicWordEntity> wordEntity) {
        this.wordEntity = wordEntity;
    }

    @Override
    public long getId() {
        return this.id;
    }
}
