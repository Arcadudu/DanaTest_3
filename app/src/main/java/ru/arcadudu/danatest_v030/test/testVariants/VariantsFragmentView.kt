package ru.arcadudu.danatest_v030.test.testVariants

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface VariantsFragmentView : MvpView {

    fun setProgressMax(originPairListCount: Int)

    @StateStrategyType(value = SkipStrategy::class)
    fun showOnRestartDialog(pairsetName: String)

    fun initPairList(testedPairList: MutableList<Pair>)

    fun onAdapterItemClick()

    fun showVariants(keySetCut: MutableList<String>)

    fun updateCounterLine(testedPairSetName: String, pairListCount: Int, pairListOriginalCount: Int)

    fun updateAnsweredProgress(answeredPairCount: Int, duration: Long)

    fun updateRecyclerOnRemoved(updatedPairList: MutableList<Pair>, answerPosition: Int)

    fun toResultFragment(backUpPairset: Pairset, mistakeCount: Int)

    fun getHintForCurrentPosition(pairKey: String)


}