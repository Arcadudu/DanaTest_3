package ru.arcadudu.danatest_v030.pairSetFragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.PairSet

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface PairSetFragmentView : MvpView {
    fun retrievePairSetList(pairSetList: MutableList<PairSet>)
}