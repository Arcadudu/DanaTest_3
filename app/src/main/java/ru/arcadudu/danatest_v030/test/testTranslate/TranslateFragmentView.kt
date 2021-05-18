package ru.arcadudu.danatest_v030.test.testTranslate

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface TranslateFragmentView : MvpView {


    @StateStrategyType(value = SkipStrategy::class)
    fun showOnRestartDialog(pairSetName: String)

    fun updateCounterLine(
        testedPairSetName: String, pairListCount: Int, pairListOriginalCount: Int
    )

    fun initPairList(testedPairList: MutableList<Pair>)

    fun updateRecyclerOnRemoved(
        updatedPairList: MutableList<Pair>, answerPosition: Int
    )

    fun updateAnsweredProgress(answeredPairCount: Int, duration: Long)

    fun setToolbarTitle(pairsetTitleForToolbar:String)

    fun setProgressMax(originalPairListCount: Int)

    fun toResultFragment(backUpPairset: Pairset, mistakeCount: Int)

    @StateStrategyType(value = SkipStrategy::class)
    fun updateRecyclerOnRestart(testedPairList: MutableList<Pair>)

    fun getLayoutPosition(layoutPosition: Int)

    fun onAdapterItemClick()

    fun getHintForCurrentPosition(pairKey: String)


}