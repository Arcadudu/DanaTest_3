package ru.arcadudu.danatest_v030.test.testTranslate

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface TranslateFragmentView : MvpView {


    fun getPairSetName(pairSetName: String)

    fun updateToolbar(
        testedPairSetName: String, pairListCount: Int, pairListOriginalCount: Int
    )

    fun initPairList(testedPairList: MutableList<Pair>)

    fun updateRecyclerOnRemoved(
        updatedPairList: MutableList<Pair>, answerPosition: Int
    )

    fun updateAnsweredProgress(answeredPairCount: Int)
    fun setProgressMax(originalPairListCount: Int)
    fun toResultFragment(backUpPairSet: PairSet, mistakeCount: Int)


}