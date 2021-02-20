package ru.arcadudu.danatest_v030.test.testTranslate

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import java.util.*

@InjectViewState
class TranslateFragmentPresenter : MvpPresenter<TranslateFragmentView>() {

    private lateinit var testedPairSet: PairSet
    private lateinit var backUpPairSet: PairSet
    private lateinit var testedPairList: MutableList<Pair>
    private lateinit var testedPairSetName: String

    private var mistakeCount = 0
    private var answeredPairCount = 0
    private var pairListOriginalCount = 0


    fun obtainTestedPairSet(incomingPairSet: PairSet) {
        testedPairSet = incomingPairSet
        backUpPairSet = testedPairSet

        testedPairList = testedPairSet.getPairList()
        testedPairSetName = testedPairSet.name
        pairListOriginalCount = testedPairList.count()
    }


    fun providePairSetList() {
        viewState.initPairList(testedPairList)
    }


    fun provideTestedPairSetName() {
        viewState.getPairSetName(testedPairSet.name)
    }


    fun provideDataForToolbar() {
        viewState.updateToolbar(testedPairSetName, answeredPairCount, pairListOriginalCount)
    }

    fun checkAnswerAndDismiss(answer: String, answerPosition: Int) {
        val checkPair = testedPairList[answerPosition]
        if (answer != checkPair.pairKey.toLowerCase(Locale.ROOT)) mistakeCount++
        answeredPairCount++
        testedPairList.removeAt(answerPosition)
        viewState.updateToolbar(testedPairSetName, answeredPairCount, pairListOriginalCount)
        viewState.updateRecyclerOnRemoved(testedPairList, answerPosition)
        viewState.updateAnsweredProgress(answeredPairCount*1000)
    }


}