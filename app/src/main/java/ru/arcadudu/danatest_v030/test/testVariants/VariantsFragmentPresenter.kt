package ru.arcadudu.danatest_v030.test.testVariants

import android.content.Context
import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.utils.PairsetListSPHandler
import java.util.*

@InjectViewState
class VariantsFragmentPresenter : MvpPresenter<VariantsFragmentView>() {

    private lateinit var testedPairset: Pairset
    private lateinit var backUpPairset: Pairset
    private lateinit var testedPairList: MutableList<Pair>
    private lateinit var testedPairSetName: String

    private lateinit var context: Context
    private lateinit var pairsetListSPHandler: PairsetListSPHandler

    private var useAllExistingPairsets = false

    private var backUpPairList: MutableList<Pair> = mutableListOf()

    private var mistakenPairAndAnswerList: MutableList<Pair> = mutableListOf()
    private var wrongAnswerList: MutableList<String> = mutableListOf()

    companion object {
        const val progressMultiplier = 1000
        const val positiveProgressDuration = 320
        const val restartProgressDuration = 640
        private var mistakeCount = 0
        private var answeredPairCount = 0
        private var hintUsedCount = 0
        private var originPairListCount = 0
    }

    fun captureContext(context: Context) {
        this.context = context
        pairsetListSPHandler = PairsetListSPHandler(context)
    }

    fun captureTestSettings(useAllExistingPairsets: Boolean) {
        this.useAllExistingPairsets = useAllExistingPairsets
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
        viewState.setProgressMax(originPairListCount * progressMultiplier)
    }

    fun onRestartButton() {
        viewState.showOnRestartDialog(
            pairsetName = testedPairSetName,
            pairsetPairCount = originPairListCount
        )
    }

    fun restartVariantsTest(shufflePairset: Boolean, useAllExistingPairsets: Boolean) {
        this.useAllExistingPairsets = useAllExistingPairsets
        mistakeCount = 0
        answeredPairCount = 0
        hintUsedCount = 0
        mistakenPairAndAnswerList.clear()
        wrongAnswerList.clear()
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

        if (useAllExistingPairsets) {
            val pairsetList = pairsetListSPHandler.loadSpPairsetList()
            for (pairset in pairsetList) {
                keySet.addAll(pairset.getPairListKeySet())
            }
        } else {
            for (pair in backUpPairList) {
                if (pair.pairKey != trueKey) {
                    keySet.add(pair.pairKey)
                }
            }
        }
        keySet.shuffle()
        val keySetCut = keySet.subList(0, 3)
        keySetCut.apply {
            add(0, trueKey)
            shuffle()
        }
        viewState.showVariants(keySetCut)
    }

    fun checkAnswerAndDismiss(chosenVariantKey: CharSequence, answerPosition: Int) {

        val checkPair = testedPairList[answerPosition]
        if (chosenVariantKey.toString() != checkPair.pairKey.toLowerCase(Locale.ROOT).trim()) {
            Log.d("check", "checkAnswerAndDismiss: mistake!")
            mistakeCount++
            wrongAnswerList.add(chosenVariantKey.toString())
            mistakenPairAndAnswerList.add(checkPair)
        }
        answeredPairCount++
        testedPairList.removeAt(answerPosition)

        if (testedPairList.isEmpty()) {
            viewState.showOnTestResultDialog()
        } else {

            viewState.updateRecyclerOnRemoved(testedPairList, answerPosition)
        }
        viewState.apply {
            updateCounterLine(testedPairSetName, answeredPairCount, originPairListCount)
            updateAnsweredProgress(
                answeredPairCount * progressMultiplier,
                positiveProgressDuration.toLong()
            )
        }

    }

    fun provideMistakenPairList(): MutableList<Pair> = mistakenPairAndAnswerList


    fun provideWrongAnswerList(): MutableList<String> = wrongAnswerList


    fun provideHintForCurrentPosition(currentSnapPosition: Int) {
        ++hintUsedCount
        val questPair = testedPairList[currentSnapPosition]
        viewState.getHintForCurrentPosition(questPair.pairKey)
    }

    fun getToolbarTitle() {
        viewState.setToolbarTitle(testedPairSetName)
    }

    fun provideMistakes(): Int = mistakeCount


    fun provideHintUseCount(): Int = hintUsedCount

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
        pairsetToEdit.setPairsetPassedVariantsTest(testPassed)

        //replacing previous pairset with new one
        pairsetListToEdit.apply {
            removeAt(pairsetToEditIndexInList)
            add(pairsetToEditIndexInList, pairsetToEdit)
        }

        //saving the result
        spHandler.saveSpPairsetList(pairsetListToEdit)
    }

}