package ru.arcadudu.danatest_v030.test.testShuffle

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface ShuffleFragmentView : MvpView {
}