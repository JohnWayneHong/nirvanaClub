package com.ggb.nirvanahappyclub.sql.entity;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

/**
 * @author : hwj
 * @date : 2024/6/18
 * description : 四级词汇，头部词汇内容，单词表，词汇内容表
 */
@Entity
public class Cet4HeadWordContentWordContentEntity extends BaseEntity{

    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    private String usphone;
    private String ukphone;
    private String ukspeech;
    private String usspeech;

    @Backlink(to = "wordContentEntity")
    private ToMany<Cet4HeadWordContentWordContentExamEntity> examEntity;
    @Backlink(to = "wordContentEntity")
    private ToMany<Cet4HeadWordContentWordContentSentenceEntity> sentenceEntity;
    @Backlink(to = "wordContentEntity")
    private ToMany<Cet4HeadWordContentWordContentSynoEntity> synoEntity;
    @Backlink(to = "wordContentEntity")
    private ToMany<Cet4HeadWordContentWordContentPhraseEntity> phraseEntity;
    @Backlink(to = "wordContentEntity")
    private ToMany<Cet4HeadWordContentWordContentRelWordEntity> relWordEntity;
    @Backlink(to = "wordContentEntity")
    private ToMany<Cet4HeadWordContentWordContentTransEntity> transEntity;

    public void setId(long id) {
        this.id = id;
    }

    public String getUsphone() {
        return usphone;
    }

    public void setUsphone(String usphone) {
        this.usphone = usphone;
    }

    public String getUkphone() {
        return ukphone;
    }

    public void setUkphone(String ukphone) {
        this.ukphone = ukphone;
    }

    public String getUkspeech() {
        return ukspeech;
    }

    public void setUkspeech(String ukspeech) {
        this.ukspeech = ukspeech;
    }

    public String getUsspeech() {
        return usspeech;
    }

    public void setUsspeech(String usspeech) {
        this.usspeech = usspeech;
    }

    public ToMany<Cet4HeadWordContentWordContentExamEntity> getExamEntity() {
        return examEntity;
    }

    public void setExamEntity(ToMany<Cet4HeadWordContentWordContentExamEntity> examEntity) {
        this.examEntity = examEntity;
    }

    public ToMany<Cet4HeadWordContentWordContentSentenceEntity> getSentenceEntity() {
        return sentenceEntity;
    }

    public void setSentenceEntity(ToMany<Cet4HeadWordContentWordContentSentenceEntity> sentenceEntity) {
        this.sentenceEntity = sentenceEntity;
    }

    public ToMany<Cet4HeadWordContentWordContentSynoEntity> getSynoEntity() {
        return synoEntity;
    }

    public void setSynoEntity(ToMany<Cet4HeadWordContentWordContentSynoEntity> synoEntity) {
        this.synoEntity = synoEntity;
    }

    public ToMany<Cet4HeadWordContentWordContentPhraseEntity> getPhraseEntity() {
        return phraseEntity;
    }

    public void setPhraseEntity(ToMany<Cet4HeadWordContentWordContentPhraseEntity> phraseEntity) {
        this.phraseEntity = phraseEntity;
    }

    public ToMany<Cet4HeadWordContentWordContentRelWordEntity> getRelWordEntity() {
        return relWordEntity;
    }

    public void setRelWordEntity(ToMany<Cet4HeadWordContentWordContentRelWordEntity> relWordEntity) {
        this.relWordEntity = relWordEntity;
    }

    public ToMany<Cet4HeadWordContentWordContentTransEntity> getTransEntity() {
        return transEntity;
    }

    public void setTransEntity(ToMany<Cet4HeadWordContentWordContentTransEntity> transEntity) {
        this.transEntity = transEntity;
    }

    @Override
    public long getId() {
        return this.id;
    }
}
