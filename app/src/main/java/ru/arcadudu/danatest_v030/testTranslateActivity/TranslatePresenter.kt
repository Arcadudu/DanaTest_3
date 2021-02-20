package ru.arcadudu.danatest_v030.testTranslateActivity

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet

@InjectViewState
class TranslatePresenter : MvpPresenter<TranslateActivityView>() {

    private lateinit var testedPairSet: PairSet
    private lateinit var testedPairList: MutableList<Pair>
    private lateinit var testedPairSetName: String


    fun obtainTestedPairSet(pairSet: PairSet) {
        testedPairSet = pairSet
        testedPairList = pairSet.getPairList()
    }


}