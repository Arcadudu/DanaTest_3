package ru.arcadudu.danatest_v030.testTranslateActivity

import android.content.Intent
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet

@InjectViewState
class TranslatePresenter : MvpPresenter<TranslateActivityView>() {

    private lateinit var testedPairSet: PairSet
    private lateinit var testedPairList: MutableList<Pair>
    private lateinit var testedPairSetName: String


    fun extractIncomingPairSet(incomingIntent: Intent, INTENT_TAG: String) {
        testedPairSet = incomingIntent.getSerializableExtra(INTENT_TAG) as PairSet
        testedPairList = testedPairSet.getPairList()
        testedPairSetName = testedPairSet.name
    }
}