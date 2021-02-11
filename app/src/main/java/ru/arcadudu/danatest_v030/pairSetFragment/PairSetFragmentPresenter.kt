package ru.arcadudu.danatest_v030.pairSetFragment

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.PairSet

@InjectViewState
class PairSetFragmentPresenter : MvpPresenter<PairSetFragmentView>() {

    private lateinit var pairSetList: MutableList<PairSet>


    fun providePairSetList() {
        pairSetList = mutableListOf()

        //before database
        var pairSetCount = 0
        repeat(20) {
            pairSetCount++
            pairSetList.add(
                PairSet(
                    name = "Набор #$pairSetCount",
                    description = "Данный набор под номером $pairSetCount содержит какие-то слова"
                )
            )
        }
        pairSetList.add(
            0,
            PairSet(
                isFavorites = true,
                name = "Избранный набор",
                description = "Сюда попадают избранные Вами пары из других наборов"
            )
        )

        //

        viewState.retrievePairSetList(pairSetList)
    }

    //private fun packWordSetList(targetPairSetList: MutableList<PairSet>) {
    //        targetPairSetList.clear()
    //        val dummyDescription = getString(R.string.dummy_text)
    //        var wordSetNameCount = 0
    //        repeat(20) {
    //            wordSetNameCount++
    //            targetPairSetList.add(
    //                PairSet(
    //                    name = "WordSet $wordSetNameCount",
    //                    description = dummyDescription
    //                )
    //            )
    //        }
    //
    //        favoritePairSet = PairSet(
    //            isFavorites = true,
    //            name = "Избранный набор",
    //            description = "Сюда попадают избранные Вами пары из других наборов"
    //        )
    //        targetPairSetList.add(0, favoritePairSet)
    //
    //        targetPairSetList.add(1, getTimeWordSet())
    //    }
}