package com.ggb.nirvanahappyclub.bean;

import java.util.List;

/**
 * @author : hwj
 * @date : 2024/6/18
 * description : 四级Json词汇解析Bean
 */
public class Cet4WordContentBean {

    private String wordHead;
    private String wordId;
    private Cet4InsideContentBean content;

    public String getWordHead() {
        return wordHead;
    }

    public void setWordHead(String wordHead) {
        this.wordHead = wordHead;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public Cet4InsideContentBean getContent() {
        return content;
    }

    public void setContent(Cet4InsideContentBean content) {
        this.content = content;
    }

    public static class Cet4InsideContentBean {
        private List<Cet4ContentExamBean> exam;
        private Cet4ContentSentenceBean sentence;
        private String usphone;
        private Cet4ContentSynoBean syno;
        private String ukphone;
        private String ukspeech;
        private Cet4ContentPhraseBean phrase;
        private Cet4ContentRelWordBean relWord;
        private String usspeech;
        private List<Cet4ContentTransBean> trans;

        public List<Cet4ContentExamBean> getExam() {
            return exam;
        }

        public void setExam(List<Cet4ContentExamBean> exam) {
            this.exam = exam;
        }

        public Cet4ContentSentenceBean getSentence() {
            return sentence;
        }

        public void setSentence(Cet4ContentSentenceBean sentence) {
            this.sentence = sentence;
        }

        public String getUsphone() {
            return usphone;
        }

        public void setUsphone(String usphone) {
            this.usphone = usphone;
        }

        public Cet4ContentSynoBean getSyno() {
            return syno;
        }

        public void setSyno(Cet4ContentSynoBean syno) {
            this.syno = syno;
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

        public Cet4ContentPhraseBean getPhrase() {
            return phrase;
        }

        public void setPhrase(Cet4ContentPhraseBean phrase) {
            this.phrase = phrase;
        }

        public Cet4ContentRelWordBean getRelWord() {
            return relWord;
        }

        public void setRelWord(Cet4ContentRelWordBean relWord) {
            this.relWord = relWord;
        }

        public String getUsspeech() {
            return usspeech;
        }

        public void setUsspeech(String usspeech) {
            this.usspeech = usspeech;
        }

        public List<Cet4ContentTransBean> getTrans() {
            return trans;
        }

        public void setTrans(List<Cet4ContentTransBean> trans) {
            this.trans = trans;
        }

        public static class Cet4ContentSentenceBean {
            private List<Cet4ContentSentencesBean> sentences;
            private String desc;

            public List<Cet4ContentSentencesBean> getSentences() {
                return sentences;
            }

            public void setSentences(List<Cet4ContentSentencesBean> sentences) {
                this.sentences = sentences;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public static class Cet4ContentSentencesBean {
                private String sContent;
                private String sCn;

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
            }
        }

        public static class Cet4ContentSynoBean {
            private List<Cet4ContentSynosBean> synos;
            private String desc;

            public List<Cet4ContentSynosBean> getSynos() {
                return synos;
            }

            public void setSynos(List<Cet4ContentSynosBean> synos) {
                this.synos = synos;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public static class Cet4ContentSynosBean {
                private String pos;
                private String tran;
                private List<Cet4ContentHwdsBean> hwds;

                public String getPos() {
                    return pos;
                }

                public void setPos(String pos) {
                    this.pos = pos;
                }

                public String getTran() {
                    return tran;
                }

                public void setTran(String tran) {
                    this.tran = tran;
                }

                public List<Cet4ContentHwdsBean> getHwds() {
                    return hwds;
                }

                public void setHwds(List<Cet4ContentHwdsBean> hwds) {
                    this.hwds = hwds;
                }

                public static class Cet4ContentHwdsBean {
                    private String w;

                    public String getW() {
                        return w;
                    }

                    public void setW(String w) {
                        this.w = w;
                    }
                }
            }
        }

        public static class Cet4ContentPhraseBean {
            private List<Cet4ContentPhrasesBean> phrases;
            private String desc;

            public List<Cet4ContentPhrasesBean> getPhrases() {
                return phrases;
            }

            public void setPhrases(List<Cet4ContentPhrasesBean> phrases) {
                this.phrases = phrases;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public static class Cet4ContentPhrasesBean {
                private String pContent;
                private String pCn;

                public String getPContent() {
                    return pContent;
                }

                public void setPContent(String pContent) {
                    this.pContent = pContent;
                }

                public String getPCn() {
                    return pCn;
                }

                public void setPCn(String pCn) {
                    this.pCn = pCn;
                }
            }
        }

        public static class Cet4ContentRelWordBean {
            private List<Cet4ContentRelsBean> rels;
            private String desc;

            public List<Cet4ContentRelsBean> getRels() {
                return rels;
            }

            public void setRels(List<Cet4ContentRelsBean> rels) {
                this.rels = rels;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public static class Cet4ContentRelsBean {
                private String pos;
                private List<Cet4ContentWordsBean> words;

                public String getPos() {
                    return pos;
                }

                public void setPos(String pos) {
                    this.pos = pos;
                }

                public List<Cet4ContentWordsBean> getWords() {
                    return words;
                }

                public void setWords(List<Cet4ContentWordsBean> words) {
                    this.words = words;
                }

                public static class Cet4ContentWordsBean {
                    private String hwd;
                    private String tran;

                    public String getHwd() {
                        return hwd;
                    }

                    public void setHwd(String hwd) {
                        this.hwd = hwd;
                    }

                    public String getTran() {
                        return tran;
                    }

                    public void setTran(String tran) {
                        this.tran = tran;
                    }
                }
            }
        }

        public static class Cet4ContentExamBean {
            private String question;
            private Cet4ContentAnswerBean answer;
            private int examType;
            private List<Cet4ContentChoicesBean> choices;

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public Cet4ContentAnswerBean getAnswer() {
                return answer;
            }

            public void setAnswer(Cet4ContentAnswerBean answer) {
                this.answer = answer;
            }

            public int getExamType() {
                return examType;
            }

            public void setExamType(int examType) {
                this.examType = examType;
            }

            public List<Cet4ContentChoicesBean> getChoices() {
                return choices;
            }

            public void setChoices(List<Cet4ContentChoicesBean> choices) {
                this.choices = choices;
            }

            public static class Cet4ContentAnswerBean {
                private String explain;
                private int rightIndex;

                public String getExplain() {
                    return explain;
                }

                public void setExplain(String explain) {
                    this.explain = explain;
                }

                public int getRightIndex() {
                    return rightIndex;
                }

                public void setRightIndex(int rightIndex) {
                    this.rightIndex = rightIndex;
                }
            }

            public static class Cet4ContentChoicesBean {
                private int choiceIndex;
                private String choice;

                public int getChoiceIndex() {
                    return choiceIndex;
                }

                public void setChoiceIndex(int choiceIndex) {
                    this.choiceIndex = choiceIndex;
                }

                public String getChoice() {
                    return choice;
                }

                public void setChoice(String choice) {
                    this.choice = choice;
                }
            }
        }

        public static class Cet4ContentTransBean {
            private String tranCn;
            private String descOther;
            private String pos;
            private String descCn;
            private String tranOther;

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
        }
    }
}
