package com.ggb.nirvanahappyclub.sql.entity;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * author : hwj
 * data : 2024年6月2日 19:36:10
 * description : 数据库 初级单词 单词表
 */
@Entity
public class BasicWordEntity extends BaseEntity {
    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;
    private String word;

    @Backlink(to = "wordEntity")
    private ToMany<BasicWordTranslationEntity> translations;
    @Backlink(to = "wordEntity")
    private ToMany<BasicWordPhraseEntity> phrases;

    public BasicWordEntity() {
    }

    public BasicWordEntity(long id, String word) {
        this.id = id;
        this.word = word;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ToMany<BasicWordTranslationEntity> getTranslations() {
        return translations;
    }

    public void setTranslations(ToMany<BasicWordTranslationEntity> translations) {
        this.translations = translations;
    }

    public ToMany<BasicWordPhraseEntity> getPhrases() {
        return phrases;
    }

    public void setPhrases(ToMany<BasicWordPhraseEntity> phrases) {
        this.phrases = phrases;
    }

    @Override
    public long getId() {
        return this.id;
    }
}
