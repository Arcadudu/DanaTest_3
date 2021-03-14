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


    private var backUpPairList: MutableList<Pair> = mutableListOf()


    companion object {
        const val progressMultiplier = 1000
        const val positiveProgressDuration = 320
        const val restartProgressDuration = 640
        private var mistakeCount = 0
        private var answeredPairCount = 0
        private var originPairListCount = 0
    }


    fun obtainTestedPairSet(incomingPairSet: PairSet) {
        testedPairSet = incomingPairSet
        backUpPairSet = testedPairSet
        testedPairList = testedPairSet.getPairList()
        backUpPairList.addAll(testedPairList)
        testedPairSetName = testedPairSet.name
        originPairListCount = testedPairList.count()
        viewState.setProgressMax(originPairListCount)
    }


    fun provideShuffledPairList() {
        testedPairList.shuffle()
        viewState.initPairList(testedPairList)
    }


    fun onRestartButton() {
        viewState.showOnRestartDialog(pairSetName = testedPairSetName)
    }


    fun provideDataForToolbar() {
        viewState.updateCounterLine(testedPairSetName, answeredPairCount, originPairListCount)
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
            updateCounterLine(testedPairSetName, answeredPairCount, originPairListCount)
            updateRecyclerOnRemoved(testedPairList, answerPosition)
            updateAnsweredProgress(
                answeredPairCount * Companion.progressMultiplier,
                positiveProgressDuration.toLong()
            )
        }

    }

    fun getProgressMax() {
        viewState.setProgressMax(originPairListCount * Companion.progressMultiplier)
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
            updateCounterLine(testedPairSetName, answeredPairCount, testedPairList.count())
            initPairList(testedPairList)
            updateAnsweredProgress(answeredPairCount, restartProgressDuration.toLong())
        }

    }


}