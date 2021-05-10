package ru.arcadudu.danatest_v030.test.testVariants

import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset

@InjectViewState
class VariantsFragmentPresenter : MvpPresenter<VariantsFragmentView>() {

    private lateinit var testedPairset: Pairset
    private lateinit var backUpPairset: Pairset
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

    fun obtainTestedPairSet(incomingPairset: Pairset) {
        testedPairset = incomingPairset
        backUpPairset = testedPairset
        testedPairList = testedPairset.getPairList()
        backUpPairList.addAll(testedPairList)
        testedPairSetName = testedPairset.name
        originPairListCount = testedPairList.count()
        viewState.setProgressMax(originPairListCount)
    }

    fun getProgressMax() {
        viewState.setProgressMax(originPairListCount * Companion.progressMultiplier)
    }

    fun onRestartButton() {
        viewState.showOnRestartDialog(pairsetName = testedPairSetName)
    }

    fun restartVariantsTest(shufflePairset: Boolean) {
        mistakeCount = 0
        answeredPairCount = 0
        testedPairList.apply {
            clear()
            addAll(backUpPairList)
            if (shufflePairset) shuffle()
        }
        viewState.apply {
            updateCounterLine(testedPairSetName, answeredPairCount, testedPairList.count())
            initPairList(testedPairList)
            updateAnsweredProgress(answeredPairCount, restartProgressDuration.toLong())
        }
    }

    fun provideShuffledPairList() {
        testedPairList.shuffle()
        viewState.initPairList(testedPairList)
    }

    fun provideOrderedPairList() {
        viewState.initPairList(testedPairList)
    }

    fun getVariantsForCurrentPosition(position: Int) {
        val trueKey = testedPairList[position].pairKey
        val keySet: MutableList<String> = mutableListOf()
        for (pair in backUpPairList) {
            if (pair.pairKey != trueKey) {
                keySet.add(pair.pairKey)
            }
        }
        val keySetCut = keySet.subList(0, 3)
        keySetCut.apply {
            add(0, trueKey)
            shuffle()
        }
        viewState.showVariants(keySetCut)
    }

    fun checkAnswerAndDismiss(chosenVariantKey: CharSequence, answerPosition: Int) {
        val checkPair = testedPairList[answerPosition]
        if (chosenVariantKey.toString() != checkPair.pairKey) {
            Log.d("check", "checkAnswerAndDismiss: mistake!")
            mistakeCount++
        } else {
            Log.d("check", "checkAnswerAndDismiss: correct!")
        }
        answeredPairCount++
        testedPairList.removeAt(answerPosition)
        if (testedPairList.isEmpty()) {
            viewState.toResultFragment(backUpPairset, mistakeCount)
        } else {
            viewState.apply {
                updateCounterLine(testedPairSetName, answeredPairCount, originPairListCount)
                updateRecyclerOnRemoved(testedPairList, answerPosition)
                updateAnsweredProgress(
                    answeredPairCount * Companion.progressMultiplier,
                    positiveProgressDuration.toLong()
                )
            }
        }
    }
}