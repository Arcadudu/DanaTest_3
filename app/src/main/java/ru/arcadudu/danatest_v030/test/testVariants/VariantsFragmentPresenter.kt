package ru.arcadudu.danatest_v030.test.testVariants

import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet

@InjectViewState
class VariantsFragmentPresenter : MvpPresenter<VariantsFragmentView>() {

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
        originPairListCount = testedPairList.count()
        viewState.setProgressMax(originPairListCount)
    }

    fun getProgressMax() {
        viewState.setProgressMax(originPairListCount * Companion.progressMultiplier)
    }

    fun onRestartButton() {
        viewState.showOnRestartDialog(pairsetName = testedPairSetName)
    }

    fun restartTranslateTest(shufflePairset: Boolean) {
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

    fun getVariantsForCurrentPosition(position: Int): MutableList<String> {
        val trueKey = testedPairList[position].pairKey
        Log.d("rrr", "getVariantsForCurrentPosition: trueKey = $trueKey ")
        val keySet = testedPairSet.getPairListKeySet().filter { it != trueKey }.shuffled()
        val keySetCut = keySet.subList(0,3) as MutableList<String>
        keySetCut.apply {
            add(0, trueKey)
            shuffle()
        }
        Log.d("rrr", "getVariantsForCurrentPosition: keySetCut = $keySetCut ")
        return keySetCut
    }
}