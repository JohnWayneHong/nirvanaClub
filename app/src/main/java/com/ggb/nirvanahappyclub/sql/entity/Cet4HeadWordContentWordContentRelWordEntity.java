package com.ggb.nirvanahappyclub.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * @author : hwj
 * @date : 2024/6/18
 * description : 四级词汇，头部词汇内容，单词表，词汇内容,同根表
 */
@Entity
public class Cet4HeadWordContentWordContentRelWordEntity extends BaseEntity{

    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    private String pos;
    //TODO 暂用第一个显示
    private String wordsHwd;
    private String wordsTran;

    private ToOne<Cet4HeadWordContentWordContentEntity> wordContentEntity;

    public void setId(long id) {
        this.id = id;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getWordsHwd() {
        return wordsHwd;
    }

    public void setWordsHwd(String wordsHwd) {
        this.wordsHwd = wordsHwd;
    }

    public String getWordsTran() {
        return wordsTran;
    }

    public void setWordsTran(String wordsTran) {
        this.wordsTran = wordsTran;
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
