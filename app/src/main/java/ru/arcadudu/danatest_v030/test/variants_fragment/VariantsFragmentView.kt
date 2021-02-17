package ru.arcadudu.danatest_v030.test.variants_fragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface VariantsFragmentView : MvpView {
}