package ru.arcadudu.danatest_v030.test.testVariants

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface VariantsFragmentView : MvpView {
    fun setProgressMax(originPairListCount: Int)
    fun showOnRestartDialog(pairsetName: String)
    fun updateCounterLine(testedPairSetName: String, pairListCount: Int, pairListOriginalCount: Int)
    fun initPairList(testedPairList: MutableList<Pair>)
    fun updateAnsweredProgress(answeredPairCount: Int, duration: Long)
    fun onAdapterItemClick()


}