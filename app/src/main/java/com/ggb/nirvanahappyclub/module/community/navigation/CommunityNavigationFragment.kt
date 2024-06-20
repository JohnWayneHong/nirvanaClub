package com.ggb.nirvanahappyclub.module.community.navigation

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.utils.LogUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.BasicWordBean
import com.ggb.nirvanahappyclub.bean.Cet4WordContentBean
import com.ggb.nirvanahappyclub.bean.WordLoaderBean
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityAndroidBinding
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityNavigationBinding
import com.ggb.nirvanahappyclub.module.community.daily.adapter.WordSearchDataAdapter
import com.ggb.nirvanahappyclub.sql.entity.BasicWordEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordContentEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordContentExamEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordContentPhraseEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordContentRelWordEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordContentSentenceEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordContentSynoEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordContentTransEntity
import com.ggb.nirvanahappyclub.sql.entity.Cet4HeadWordContentWordEntity
import com.ggb.nirvanahappyclub.sql.operator.BasicCet4ExamOperator
import com.ggb.nirvanahappyclub.sql.operator.BasicCet4SentenceOperator
import com.ggb.nirvanahappyclub.sql.operator.BasicCet4WordOperator
import com.ggb.nirvanahappyclub.sql.operator.BasicPhraseOperator
import com.ggb.nirvanahappyclub.sql.operator.BasicWordOperator
import com.ggb.nirvanahappyclub.utils.ConvertJsonFileUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class CommunityNavigationFragment : BaseFragment<CommunityNavigationViewModel, FragmentCommunityNavigationBinding>(),OnItemSingleClickListener{

    private var wAdapter : WordSearchDataAdapter?=null



    private var searchWordList: MutableList<Any> = ArrayList()

    override fun initView() {

    }

    override fun initFragmentData() {
        wAdapter = WordSearchDataAdapter()
        mBindingView.rvCommunityNavWordData.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        mBindingView.rvCommunityNavWordData.adapter = wAdapter
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityTitleLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()

                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }
    }

    override fun initListener() {
        ClickUtils.register(this)
            .addOnClickListener()

            .addView(mBindingView.tvCommunityNavWordDeleteAll)
            .addView(mBindingView.tvCommunityNavWordInputJson)
            .addView(mBindingView.tvCommunityNavWordSearch)
            .addView(mBindingView.tvCommunityNavWordTest)
            .submit()

    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        if (id == R.id.tv_community_daily_word_test) {

        } else if (id == R.id.tv_community_daily_word_search) {
            if (mBindingView.etCommunityNavWordInput.text.isNotEmpty()) {


            }
        }else if (id == R.id.tv_community_nav_word_input_json) {
            val jsonArray = ConvertJsonFileUtils.readJsonFromAssets(context,"CET4luan_2.json")
            LogUtils.xswShowLog("解析成功-长度-"+jsonArray.size())

            if (jsonArray != null) {
                val wordJavaList = jsonArray.toJavaList(Cet4WordContentBean::class.java)

                val wordList = ArrayList<Cet4HeadWordContentWordEntity>()
                val contentList = ArrayList<Cet4HeadWordContentWordContentEntity>()
                val examEntityList = ArrayList<Cet4HeadWordContentWordContentExamEntity>()
                val sentenceEntityList = ArrayList<Cet4HeadWordContentWordContentSentenceEntity>()
                val synoEntityList = ArrayList<Cet4HeadWordContentWordContentSynoEntity>()
                val phraseEntityList = ArrayList<Cet4HeadWordContentWordContentPhraseEntity>()
                val relWordEntityList = ArrayList<Cet4HeadWordContentWordContentRelWordEntity>()
                val transEntityList = ArrayList<Cet4HeadWordContentWordContentTransEntity>()
                
                
                for (cet4WordContentBean in wordJavaList) {
                    insertWordEntityList(cet4WordContentBean,wordList,contentList,examEntityList,sentenceEntityList,synoEntityList,phraseEntityList,relWordEntityList,transEntityList)
                }


                val putData1 = mBasicCet4WordOperator.putData(wordList)
                val putData2 = mBasicCet4ExamOperator.putData(examEntityList)
                val putData3 = mBasicCet4SentenceOperator.putData(sentenceEntityList)


                LogUtils.xswShowLog(putData1.toString() + "wordList-插入后返回")
                LogUtils.xswShowLog(putData2.toString() + "examEntityList-插入后返回")
                LogUtils.xswShowLog(putData3.toString() + "sentenceEntityList-插入后返回")


            }

        }else if (id == R.id.tv_community_daily_word_delete_all) {

        }
    }

    private fun insertWordEntityList(
        cet4WordContentBean: Cet4WordContentBean,
        wordList:ArrayList<Cet4HeadWordContentWordEntity>,
        contentList: ArrayList<Cet4HeadWordContentWordContentEntity>,
        examEntityList: ArrayList<Cet4HeadWordContentWordContentExamEntity>,
        sentenceEntityList: ArrayList<Cet4HeadWordContentWordContentSentenceEntity>,
        synoEntityList: ArrayList<Cet4HeadWordContentWordContentSynoEntity>,
        phraseEntityList: ArrayList<Cet4HeadWordContentWordContentPhraseEntity>,
        relWordEntityList: ArrayList<Cet4HeadWordContentWordContentRelWordEntity>,
        transEntityList: ArrayList<Cet4HeadWordContentWordContentTransEntity>
    ) {

        val wordEntity = Cet4HeadWordContentWordEntity()
        wordEntity.wordHead = cet4WordContentBean.wordHead
        wordEntity.wordId = cet4WordContentBean.wordId

        val wordContentEntity = Cet4HeadWordContentWordContentEntity()
        wordContentEntity.usphone = cet4WordContentBean.content.usphone
        wordContentEntity.ukphone = cet4WordContentBean.content.ukphone
        wordContentEntity.ukspeech = cet4WordContentBean.content.ukspeech
        wordContentEntity.usspeech = cet4WordContentBean.content.usspeech

        val tempExamEntityList: MutableList<Cet4HeadWordContentWordContentExamEntity> = ArrayList()
        cet4WordContentBean.content.exam.forEach {
            val tempExam = Cet4HeadWordContentWordContentExamEntity()
            tempExam.question = it.question
            tempExam.explain = it.answer.explain
            tempExam.rightIndex = it.answer.rightIndex
            tempExam.examType = it.examType

            tempExamEntityList.add(tempExam)
        }

        wordContentEntity.examEntity.addAll(tempExamEntityList)

        val tempSentenceEntityList: MutableList<Cet4HeadWordContentWordContentSentenceEntity> = ArrayList()
        cet4WordContentBean.content.sentence.sentences.forEach {
            val tempSentence = Cet4HeadWordContentWordContentSentenceEntity()
            tempSentence.sContent = it.sContent
            tempSentence.sCn = it.sCn


            tempSentenceEntityList.add(tempSentence)
        }

        wordContentEntity.sentenceEntity.addAll(tempSentenceEntityList)



        wordEntity.wordContentEntity.target = wordContentEntity

        wordList.add(wordEntity)
        examEntityList.addAll(wordContentEntity.examEntity)
        sentenceEntityList.addAll(wordContentEntity.sentenceEntity)
    }

    override fun onItemClick(view: View?, groupPosition: Int, subPosition: Int) {
        super.onItemClick(view, groupPosition, subPosition)

    }

    companion object {
        fun newInstance(): CommunityNavigationFragment {
            val fragment = CommunityNavigationFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    val mBasicCet4WordOperator: BasicCet4WordOperator = BasicCet4WordOperator()
    val mBasicCet4ExamOperator: BasicCet4ExamOperator = BasicCet4ExamOperator()
    val mBasicCet4SentenceOperator: BasicCet4SentenceOperator = BasicCet4SentenceOperator()

}