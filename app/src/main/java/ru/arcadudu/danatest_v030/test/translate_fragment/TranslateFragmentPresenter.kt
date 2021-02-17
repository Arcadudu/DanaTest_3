package ru.arcadudu.danatest_v030.test.translate_fragment

import android.content.Intent
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet

@InjectViewState
class TranslateFragmentPresenter: MvpPresenter<TranslateFragmentView>() {

    private lateinit var testedPairSet: PairSet
    private lateinit var testedPairList: MutableList<Pair>
    private lateinit var testedPairSetName: String

    fun extractIncomingPairSet(incomingIntent: Intent?, INTENT_TAG:String) {
        testedPairSet = incomingIntent?.getSerializableExtra(INTENT_TAG) as PairSet
        testedPairList = testedPairSet.getPairList()
        testedPairSetName = testedPairSet.name
    }


}