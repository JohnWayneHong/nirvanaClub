package com.ggb.nirvanahappyclub.sql.entity;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

/**
 * author : hwj
 * data : 2024年6月2日 19:36:10
 * description : 数据库 初级单词 翻译表
 */
@Entity
public class BasicWordTranslationEntity extends BaseEntity {
    @Id
    public long id;
    public String translation;
    public String type;
    private ToOne <BasicWordEntity> wordEntity;

    public BasicWordTranslationEntity() {
        // Required no-arg constructor
    }

    public BasicWordTranslationEntity(String translation, String type) {
        this.translation = translation;
        this.type = type;
    }

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
