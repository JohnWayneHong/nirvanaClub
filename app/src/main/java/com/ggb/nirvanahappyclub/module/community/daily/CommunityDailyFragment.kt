package com.ggb.nirvanahappyclub.module.community.daily

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.utils.LogUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.BasicWordBean
import com.ggb.nirvanahappyclub.bean.UserShowBean
import com.ggb.nirvanahappyclub.bean.WordLoaderBean
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityDailyBinding
import com.ggb.nirvanahappyclub.sql.entity.BasicWordEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity
import com.ggb.nirvanahappyclub.sql.entity.UserEntity
import com.ggb.nirvanahappyclub.sql.operator.BasicPhraseOperator
import com.ggb.nirvanahappyclub.sql.operator.BasicTranslationOperator
import com.ggb.nirvanahappyclub.sql.operator.BasicWordOperator
import com.tamsiree.rxkit.view.RxToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlin.random.Random


class CommunityDailyFragment : BaseFragment<CommunityDailyViewModel, FragmentCommunityDailyBinding>(),OnItemSingleClickListener{

    private var tempId : Long = -1

    private var insertProcess = 0

    private var subscriber: CustomObserver<WordLoaderBean>? = null

    override fun initView() {
        val userShowBean = UserShowBean()

        mBindingView.data = userShowBean
        mBindingView.wordLoaderBean = WordLoaderBean()
    }

    override fun initFragmentData() {

        val userEntity = UserEntity(0, "119", "乖乖帮")
        mViewModel.getCommunityDailyData(userEntity)
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityDailyDataLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()

                    tempId = resource.data
                    RxToast.success("返回的数据是"+resource.data.toString())
                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }

        mViewModel.getCommunityDailyDataInfoLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    val userEntity = resource!!.data

                    mBindingView.data!!.setMobile(userEntity.phone)
                    mBindingView.data!!.setUserName(userEntity.companyId)

                    LogUtils.xswShowLog(resource.data.companyId)
                }
                Resource.ERROR -> {

                    dismissLoadingDialog()
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addOnClickListener()
            .addView(mBindingView.tvCommunityDailySearch)
            .addView(mBindingView.tvCommunityDailyInsert)
            .addView(mBindingView.tvCommunityDailyWordInsert)
            .addView(mBindingView.tvCommunityDailyWordDeleteAll)
            .addView(mBindingView.tvCommunityDailyWordInputJson)
            .addView(mBindingView.tvCommunityDailyWordSearch)
            .addView(mBindingView.tvCommunityDailyWordTest)
            .submit()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        if (id == R.id.tv_community_daily_search) {
            mViewModel.getCommunityDailyDataInfo(tempId)
        }else if (id == R.id.tv_community_daily_insert) {
            val userEntity = generateRandomUserEntity()
            mViewModel.getCommunityDailyData(userEntity)
        }else if (id == R.id.tv_community_daily_word_test) {
            var queryAllBasicWordPhraseEntity = mBasicPhraseOperator.queryAllBasicWordPhraseEntity()
            LogUtils.xswShowLog("当前词组表共有"+queryAllBasicWordPhraseEntity.size)
        }

        else if (id == R.id.tv_community_daily_word_search) {

            if (mBindingView.etCommunityDailyWordInput.text.isNotEmpty()) {
                val queryBasicWordData =
                    mBasicWordOperator.queryBasicWordData(mBindingView.etCommunityDailyWordInput.text.toString())

                LogUtils.xswShowLog("一共找到单词是--》"+queryBasicWordData.size)

                queryBasicWordData.forEach {
                    LogUtils.xswShowLog("单词是--》"+it.word)

                    for (translationEntity in it.translations) {
                        LogUtils.xswShowLog("翻译是--》"+translationEntity.translation)
                        LogUtils.xswShowLog("词组词性--》"+translationEntity.type)
                    }

                    for (phraseEntity in it.phrases) {
                        LogUtils.xswShowLog("词组是--》"+phraseEntity.phrase)
                        LogUtils.xswShowLog("词组翻译--》"+phraseEntity.translation)

                    }
                }

            }
        } else if (id == R.id.tv_community_daily_word_insert) {


        }else if (id == R.id.tv_community_daily_word_input_json) {

            val jsonArray = com.ggb.nirvanahappyclub.utils.JsonUtils.readJsonFromAssets(context,"cet4.json")
            LogUtils.xswShowLog("当前解析后的数组为--"+jsonArray.size())
            mBindingView.wordLoaderBean?.totalProcess = jsonArray.size().toString()

            if (jsonArray != null) {
                val wordJavaList = jsonArray.toJavaList(BasicWordBean::class.java)

                //TODO 打算一起插入wordList，translationList， phraseList
                val wordEntityList = ArrayList<BasicWordEntity>()
                val translationEntityList = ArrayList<BasicWordTranslationEntity>()
                val phraseEntityList = ArrayList<BasicWordPhraseEntity>()

                for (basicWordBean in wordJavaList) {
                    insertWordEntityList(basicWordBean,wordEntityList,translationEntityList,phraseEntityList)
                }

                val putData = mBasicWordOperator.putData(wordEntityList)
                val putData2 = mBasicTranslationOperator.putData(translationEntityList)
                val putData3 = mBasicPhraseOperator.putData(phraseEntityList)

                LogUtils.xswShowLog(putData.toString() + "word-插入后返回")
                LogUtils.xswShowLog(putData2.toString() + "translations-插入后返回")
                LogUtils.xswShowLog(putData3.toString() + "phrases-插入后返回")

                val loaderBean = WordLoaderBean()
                loaderBean.currentProcess = wordJavaList.size.toString()
                loaderBean.totalProcess = wordJavaList.size.toString()
                loaderBean.isComplete = true
                subscriber?.let {
                    Observable.just<WordLoaderBean>(loaderBean).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(it)
                }
            }
        }else if (id == R.id.tv_community_daily_word_delete_all) {
            mBasicWordOperator.deleteAll()
            mBasicPhraseOperator.deleteAll()
            mBasicTranslationOperator.deleteAll()
        }
    }

    private fun insertWordEntityList(
        entity: BasicWordBean,
        wordEntityList: java.util.ArrayList<BasicWordEntity>,
        translationEntityList: java.util.ArrayList<BasicWordTranslationEntity>,
        phraseEntityList: java.util.ArrayList<BasicWordPhraseEntity>
    ) {
        val wordEntity = BasicWordEntity()
        wordEntity.word = entity.word

        val translationEntitiesList: MutableList<BasicWordTranslationEntity> = ArrayList<BasicWordTranslationEntity>()

        if (!entity.translations.isNullOrEmpty()) {
            for (translationBean in entity.translations) {
                val wordTranslationEntity = BasicWordTranslationEntity()
                wordTranslationEntity.translation = translationBean.translation
                wordTranslationEntity.type = translationBean.type
                wordTranslationEntity.wordEntity.target = wordEntity

                translationEntitiesList.add(wordTranslationEntity)
            }
        }

        wordEntity.translations.addAll(translationEntitiesList)


        val phraseEntitiesList: MutableList<BasicWordPhraseEntity> = ArrayList<BasicWordPhraseEntity>()

        if (!entity.phrases.isNullOrEmpty()) {
            for (phraseBean in entity.phrases) {
                val wordPhraseEntity = BasicWordPhraseEntity()
                wordPhraseEntity.phrase = phraseBean.phrase
                wordPhraseEntity.translation = phraseBean.translation
                wordPhraseEntity.wordEntity.target = wordEntity

                phraseEntitiesList.add(wordPhraseEntity)
            }
        }

        wordEntity.phrases.addAll(phraseEntitiesList)


        wordEntityList.add(wordEntity)
        translationEntityList.addAll(wordEntity.translations)
        phraseEntityList.addAll(wordEntity.phrases)
    }

    private fun insertWordEntityList(entity: BasicWordBean,allSize:Int) {
        val wordEntity = BasicWordEntity()
        wordEntity.word = entity.word

        val translationEntitiesList: MutableList<BasicWordTranslationEntity> = ArrayList<BasicWordTranslationEntity>()

        if (!entity.translations.isNullOrEmpty()) {
            for (translationBean in entity.translations) {
                val wordTranslationEntity = BasicWordTranslationEntity()
                wordTranslationEntity.translation = translationBean.translation
                wordTranslationEntity.type = translationBean.type
                wordTranslationEntity.wordEntity.target = wordEntity

                translationEntitiesList.add(wordTranslationEntity)
            }
        }

        wordEntity.translations.addAll(translationEntitiesList)


        val phraseEntitiesList: MutableList<BasicWordPhraseEntity> = ArrayList<BasicWordPhraseEntity>()

        if (!entity.phrases.isNullOrEmpty()) {
            for (phraseBean in entity.phrases) {
                val wordPhraseEntity = BasicWordPhraseEntity()
                wordPhraseEntity.phrase = phraseBean.phrase
                wordPhraseEntity.translation = phraseBean.translation
                wordPhraseEntity.wordEntity.target = wordEntity

                phraseEntitiesList.add(wordPhraseEntity)
            }
        }

        wordEntity.phrases.addAll(phraseEntitiesList)


        val putData = mBasicWordOperator.putData(wordEntity)
        val putData2 = mBasicTranslationOperator.putData(wordEntity.translations)
        val putData3 = mBasicPhraseOperator.putData(wordEntity.phrases)

        LogUtils.xswShowLog(putData.toString() + "word-插入后返回")
        LogUtils.xswShowLog(putData2.toString() + "translations-插入后返回")
        LogUtils.xswShowLog(putData3.toString() + "phrases-插入后返回")


        insertProcess++
        val loaderBean = WordLoaderBean()
        loaderBean.currentProcess = insertProcess.toString()
        loaderBean.totalProcess = allSize.toString()

        subscriber?.let {
            Observable.just<WordLoaderBean>(loaderBean).observeOn(AndroidSchedulers.mainThread())
                .subscribe(it)
        }

//        mBindingView.wordLoaderBean?.currentProcess = insertProcess.toString()

    }

    private fun generateRandomUserEntity(): UserEntity {

        val randomUserId = Random.nextInt(1000000).toString() // 生成一个 6 位数的随机字符串
        val names = listOf(
            "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hannah",
            "Isaac", "Julia", "Kevin", "Linda", "Mike", "Nancy", "Olivia", "Peter",
            "Quinn", "Rachel", "Sam", "Tracy", "Ursula", "Victor", "Wendy", "Xavier",
            "Yvonne", "Zach"
        ) // 更多可能的名字
        val randomName = names.random() // 从列表中随机选择一个名字
        val userEntity = UserEntity()
        userEntity.phone = randomUserId
        userEntity.companyId = randomName

        return userEntity
    }

    override fun onItemClick(view: View?, groupPosition: Int, subPosition: Int) {
        super.onItemClick(view, groupPosition, subPosition)

    }

    companion object {
        fun newInstance(): CommunityDailyFragment {
            val fragment = CommunityDailyFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    val mBasicWordOperator:BasicWordOperator = BasicWordOperator();
    val mBasicPhraseOperator:BasicPhraseOperator = BasicPhraseOperator();
    val mBasicTranslationOperator: BasicTranslationOperator = BasicTranslationOperator();

    val testJson:String = "{\n" +
            "  \"word\": \"ability\",\n" +
            "  \"translations\": [\n" +
            "    {\n" +
            "      \"translation\": \"能力，能耐；才能\",\n" +
            "      \"type\": \"n\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"phrases\": [\n" +
            "    {\"phrase\": \"innovation ability\", \"translation\": \"创新能力\"}, \n" +
            "    {\"phrase\": \"ability for\", \"translation\": \"在…的能力\"}, \n" +
            "    {\"phrase\": \"learning ability\", \"translation\": \"学习能力\"}, \n" +
            "    {\"phrase\": \"practical ability\", \"translation\": \"实践能力；实际能力\"}, \n" +
            "    {\"phrase\": \"technical ability\", \"translation\": \"技术能力\"}, \n" +
            "    {\"phrase\": \"reading ability\", \"translation\": \"阅读能力\"}, \n" +
            "    {\"phrase\": \"management ability\", \"translation\": \"管理能力\"}, \n" +
            "    {\"phrase\": \"writing ability\", \"translation\": \"写作能力；书写能力\"}, \n" +
            "    {\"phrase\": \"working ability\", \"translation\": \"工作能力，加工能力\"}, \n" +
            "    {\"phrase\": \"physical ability\", \"translation\": \"体能，体质能力；身体能力\"}, \n" +
            "    {\"phrase\": \"cognitive ability\", \"translation\": \"认知能力\"}, \n" +
            "    {\"phrase\": \"service ability\", \"translation\": \"工作能力\"}, \n" +
            "    {\"phrase\": \"ability to pay\", \"translation\": \"支付能力\"}, \n" +
            "    {\"phrase\": \"combining ability\", \"translation\": \"配合力\"}, \n" +
            "    {\"phrase\": \"develop ability\", \"translation\": \"发挥才能\"}, \n" +
            "    {\"phrase\": \"executive ability\", \"translation\": \"执行力；行政能力\"}, \n" +
            "    {\"phrase\": \"natural ability\", \"translation\": \"本能\"}, \n" +
            "    {\"phrase\": \"administrative ability\", \"translation\": \"行政能力；经营才能\"}, \n" +
            "    {\"phrase\": \"unique ability\", \"translation\": \"独有能力\"}, \n" +
            "    {\"phrase\": \"adaptive ability\", \"translation\": \"自适应能力\"}\n" +
            "  ]\n" +
            "}"

    val testJson2:String = "{\n" +
            "    \"word\": \"challenge\",\n" +
            "    \"translations\": [\n" +
            "      {\n" +
            "        \"translation\": \"挑战；邀请比赛；艰巨的任务；怀疑，质问\",\n" +
            "        \"type\": \"n\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"translation\": \" 反对， 公然反抗； 向…挑战； 对…质疑\",\n" +
            "        \"type\": \"vt\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"phrases\": [\n" +
            "      {\n" +
            "        \"phrase\": \"meet the challenge\",\n" +
            "        \"translation\": \"迎接挑战；满足要求\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"phrase\": \"rise to the challenge\",\n" +
            "        \"translation\": \"接受挑战，奋起应付挑战\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"phrase\": \"take up the challenge\",\n" +
            "        \"translation\": \"应战\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"phrase\": \"challenge cup\",\n" +
            "        \"translation\": \"挑战杯；优胜杯；奖杯\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }"
}