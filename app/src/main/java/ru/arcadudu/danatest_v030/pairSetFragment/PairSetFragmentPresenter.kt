package ru.arcadudu.danatest_v030.pairSetFragment

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.utils.getTimeWordSet
import java.util.*

@InjectViewState
class PairSetFragmentPresenter : MvpPresenter<PairSetFragmentView>() {

    private lateinit var pairSetList: MutableList<PairSet>

    init {
        initiatePairSetList()
    }

    private fun initiatePairSetList() {
        pairSetList = mutableListOf()

        //no database stub
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
        pairSetList.add(0, getTimeWordSet())
    }

    fun providePairSetList() {
        viewState.retrievePairSetList(pairSetList)
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(pairSetList, fromPosition, toPosition)
        viewState.updateRecyclerOnSwap(pairSetList, fromPosition, toPosition)

    }

    fun onSwipedLeft(swipePosition: Int) {
        val chosenPairSet = pairSetList[swipePosition]
        viewState.showRemovePairSetDialog(
            chosenPairSet.name,
            chosenPairSet.description,
            position = swipePosition
        )
    }

    fun removePairSetAtPosition(position: Int) {
        pairSetList.removeAt(position)
        viewState.updateRecyclerOnRemoved(pairSetList, position)
    }

    fun filter(text: String) {
        val filteredList: MutableList<PairSet> = mutableListOf()
        for (item in pairSetList) {
            if (item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                item.description.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        viewState.obtainFilteredList(filteredList)
    }

    fun onAddNewPairSet() {
        viewState.showAddNewPairSetDialog()
    }

    fun addNewPairSet(inputPairSetName: String, inputPairSetDetails: String) {
        pairSetList.add(
            index = 1,
            element = PairSet(name = inputPairSetName, description = inputPairSetDetails)
        )
        viewState.updateRecyclerOnAdded(pairSetList)
    }


}