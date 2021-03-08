package ru.arcadudu.danatest_v030.test.testTranslate

import android.util.Log
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
    private var originPairListCount = 0
    private var backUpPairList: MutableList<Pair> = mutableListOf()


    fun obtainTestedPairSet(incomingPairSet: PairSet) {
        testedPairSet = incomingPairSet
        backUpPairSet = testedPairSet
        testedPairList = testedPairSet.getPairList()
        backUpPairList.addAll(testedPairList)
        Log.d(
            "restart",
            "Presenter: obtainTestedPairSet: backupList count = ${backUpPairList.count()}"
        )
        Log.d(
            "restart",
            "Presenter: obtainTestedPairSet: backupPairSet list within count = ${
                backUpPairSet.getPairList().count()
            }"
        )
        testedPairSetName = testedPairSet.name
        originPairListCount = testedPairList.count()
        viewState.setProgressMax(originPairListCount)
    }


    fun providePairSetList() {
        testedPairList.shuffle()
        viewState.initPairList(testedPairList)
    }

    fun onRestartButton() {
        viewState.showOnRestartDialog(pairSetName = testedPairSetName)
    }


    fun provideDataForToolbar() {
        viewState.updateToolbar(testedPairSetName, answeredPairCount, originPairListCount)
    }

    fun checkAnswerAndDismiss(inputAnswer: String, answerPosition: Int) {
        val checkPair = testedPairList[answerPosition]
        if (inputAnswer != checkPair.pairKey.toLowerCase(Locale.ROOT)) mistakeCount++
        answeredPairCount++
        testedPairList.removeAt(answerPosition)
        if (testedPairList.isEmpty()) {
            viewState.toResultFragment(backUpPairSet, mistakeCount)
        }

        viewState.apply {
            updateToolbar(testedPairSetName, answeredPairCount, originPairListCount)
            updateRecyclerOnRemoved(testedPairList, answerPosition)
            updateAnsweredProgress(answeredPairCount * 1000)
        }

    }

    fun getProgressMax() {
        viewState.setProgressMax(originPairListCount * 1000)
    }

    fun restartTranslateTest() {
        mistakeCount = 0
        answeredPairCount = 0
        testedPairList.apply {
            clear()
            addAll(backUpPairList)
            shuffle()
        }
        viewState.apply {
            updateToolbar(testedPairSetName, answeredPairCount, testedPairList.count())
            initPairList(testedPairList)
            updateAnsweredProgress(answeredPairCount)
        }

    }


}