package com.ggb.nirvanahappyclub.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * @author : hwj
 * @date : 2024/6/18
 * description : 四级词汇，头部词汇内容，单词表，词汇内容,翻译表
 */
@Entity
public class Cet4HeadWordContentWordContentTransEntity extends BaseEntity{

    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    private String tranCn;
    private String descOther;
    private String pos;
    private String descCn;
    private String tranOther;

    private ToOne<Cet4HeadWordContentWordContentEntity> wordContentEntity;
    public void setId(long id) {
        this.id = id;
    }

    public String getTranCn() {
        return tranCn;
    }

    public void setTranCn(String tranCn) {
        this.tranCn = tranCn;
    }

    public String getDescOther() {
        return descOther;
    }

    public void setDescOther(String descOther) {
        this.descOther = descOther;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDescCn() {
        return descCn;
    }

    public void setDescCn(String descCn) {
        this.descCn = descCn;
    }

    public String getTranOther() {
        return tranOther;
    }

    public void setTranOther(String tranOther) {
        this.tranOther = tranOther;
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
