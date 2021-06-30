package ru.arcadudu.danatest_v030.test.testTranslate

import android.content.Context
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.utils.PairsetListSPHandler
import java.util.*

@InjectViewState
class TranslateFragmentPresenter : MvpPresenter<TranslateFragmentView>() {

    private lateinit var testedPairset: Pairset
    private lateinit var backUpPairset: Pairset
    private lateinit var testedPairlist: MutableList<Pair>
    private lateinit var testedPairsetName: String

    private var mistakenPairList: MutableList<Pair> = mutableListOf()
    private var wrongAnswerList: MutableList<String> = mutableListOf()

    private var backUpPairlist: MutableList<Pair> = mutableListOf()

    companion object {
        const val progressMultiplier = 1000
        const val positiveProgressDuration = 320
        const val restartProgressDuration = 640
        private var mistakeCount = 0
        private var hintUsedCount = 0
        private var answeredPairCount = 0
        private var originalPairlistCount = 0
    }

    fun obtainTestedPairSet(incomingPairset: Pairset) {
        testedPairset = incomingPairset
        backUpPairset = testedPairset
        testedPairlist = testedPairset.getPairList()
        backUpPairlist.addAll(testedPairlist)
        testedPairsetName = testedPairset.name
        originalPairlistCount = testedPairlist.count()
        viewState.setProgressMax(originalPairlistCount)
    }

    fun provideShuffledPairList() {
        testedPairlist.shuffle()
        viewState.initPairList(testedPairlist)
    }

    fun provideOrderedPairList() {
        viewState.initPairList(testedPairlist)
    }

    fun onRestartButton() {
        viewState.showOnRestartDialog(pairSetName = testedPairsetName)
    }

    fun provideDataForToolbar() {
        viewState.updateCounterLine(testedPairsetName, answeredPairCount, originalPairlistCount)
    }

    fun provideMistakes(): Int = mistakeCount

    fun checkAnswerAndDismiss(inputAnswer: String, answerPosition: Int) {
        val checkPair = testedPairlist[answerPosition]
        if (inputAnswer != checkPair.pairKey.toLowerCase(Locale.ROOT)) {
            mistakeCount++
            wrongAnswerList.add(inputAnswer)
            mistakenPairList.add(checkPair)
        }
        answeredPairCount++
        testedPairlist.removeAt(answerPosition)

        if (testedPairlist.isEmpty()) {
            viewState.showOnTestResultDialog()
        } else {
            viewState.updateRecyclerOnRemoved(testedPairlist, answerPosition)
        }

        viewState.apply {
            updateCounterLine(testedPairsetName, answeredPairCount, originalPairlistCount)
            updateAnsweredProgress(
                answeredPairCount * Companion.progressMultiplier,
                positiveProgressDuration.toLong()
            )
        }
    }

    fun applyTestPassReward(mistakesTotal:Int, hintsUsed:Int, fragmentContext: Context) {
        val spHandler = PairsetListSPHandler(fragmentContext)

        //loading full pairsetList from memory
        val pairsetListToEdit = spHandler.loadSpPairsetList()

        val testedPairsetId = testedPairset.pairsetId

        //searching for required pairset which we want to edit as passed
        val pairsetToEdit = pairsetListToEdit.first { it.pairsetId == testedPairsetId }
        //defining it's index in full pairsetList
        val pairsetToEditIndexInList = pairsetListToEdit.indexOf(pairsetToEdit)

        //setting this pairset as test passed
        val testPassed = mistakesTotal==0 && hintUsedCount ==0
        pairsetToEdit.setPairsetPassedTranslateTest(testPassed)

        //replacing previous pairset with new one
        pairsetListToEdit.apply {
            removeAt(pairsetToEditIndexInList)
            add(pairsetToEditIndexInList, pairsetToEdit)
        }

        //saving the result
        spHandler.saveSpPairsetList(pairsetListToEdit)
    }

    fun getProgressMax() {
        viewState.setProgressMax(originalPairlistCount * Companion.progressMultiplier)
    }

    fun restartTranslateTest(shufflePairset: Boolean) {
        mistakeCount = 0
        answeredPairCount = 0
        hintUsedCount = 0
        mistakenPairList.clear()
        wrongAnswerList.clear()
        testedPairlist.apply {
            clear()
            addAll(backUpPairlist)
            if (shufflePairset)
                shuffle()
        }
        viewState.apply {
            updateCounterLine(testedPairsetName, answeredPairCount, testedPairlist.count())
            initPairList(testedPairlist)
            updateAnsweredProgress(answeredPairCount, restartProgressDuration.toLong())
        }

    }

    fun onTestStop() {
        mistakeCount = 0
        answeredPairCount = 0
        originalPairlistCount = 0
    }

    fun getToolbarTitle() {
        viewState.setToolbarTitle(testedPairsetName)
    }

    fun provideHintForCurrentPosition(currentSnapPosition: Int) {
        ++hintUsedCount
        val questPair = testedPairlist[currentSnapPosition]
        viewState.getHintForCurrentPosition(questPair.pairKey)
    }

    fun provideMistakenPairList(): MutableList<Pair> = mistakenPairList

    fun provideWrongAnswerList(): MutableList<String> = wrongAnswerList

    fun provideHintUseCount(): Int = hintUsedCount

}


