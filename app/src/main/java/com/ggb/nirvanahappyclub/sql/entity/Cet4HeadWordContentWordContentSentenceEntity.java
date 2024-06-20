package com.ggb.nirvanahappyclub.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * @author : hwj
 * @date : 2024/6/18
 * description : 四级词汇，头部词汇内容，单词表，词汇内容,例句表
 */
@Entity
public class Cet4HeadWordContentWordContentSentenceEntity extends BaseEntity{

    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    private String sContent;
    private String sCn;
    private ToOne<Cet4HeadWordContentWordContentEntity> wordContentEntity;

    public void setId(long id) {
        this.id = id;
    }

    public String getSContent() {
        return sContent;
    }

    public void setSContent(String sContent) {
        this.sContent = sContent;
    }

    public String getSCn() {
        return sCn;
    }

    public void setSCn(String sCn) {
        this.sCn = sCn;
    }

    public ToOne<Cet4HeadWordContentWordContentEntity> getWordContentEntity() {
        return wordContentEntity;
    }

    public void setWordContentEntity(ToOne<Cet4HeadWordContentWordContentEntity> wordContentEntity) {
        this.wordContentEntity = wordContentEntity;
    }

    @Override
    public long getId() {
        return this.id;
    }
}
