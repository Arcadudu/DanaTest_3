package ru.arcadudu.danatest_v030.pairSetFragment

import android.content.Context
import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.utils.getDummyPairSet
import ru.arcadudu.danatest_v030.utils.getTimePairSet
import java.util.*

@InjectViewState
class PairSetFragmentPresenter : MvpPresenter<PairSetFragmentView>() {

    private lateinit var pairSetList: MutableList<PairSet>
    private lateinit var context: Context


    fun captureContext(context: Context) {
        this.context = context
    }

    fun providePairSetListCount() {
        val message = context.resources.getQuantityString(
            R.plurals.plurals_2,
            pairSetList.count(),
            pairSetList.count()
        )
        viewState.updateToolbarInfo(message)
    }

    fun initiatePairSetList() {
        pairSetList = mutableListOf()

        //no database stub
        var pairSetCount = 0
        repeat(5) {
            pairSetCount++
            pairSetList.add(
                PairSet(
                    name = "Набор #$pairSetCount",
                    details = "Набор под номером $pairSetCount "
                )
            )
        }
        pairSetList.add(0, getTimePairSet())
        pairSetList.add(0, getDummyPairSet())
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
            chosenPairSet.details,
            position = swipePosition
        )
    }

    fun onSwipedRight(swipePosition: Int) {
        val chosenPairset = pairSetList[swipePosition]
        if(chosenPairset.getPairList().count() != 0){
            viewState.showStartTestDialog(chosenPairset)
        }else{
            viewState.showOnEmptyPairSetDialog(chosenPairset)
        }

    }

    fun removePairSetAtPosition(position: Int) {
        pairSetList.removeAt(position)
        viewState.updateRecyclerOnRemoved(pairSetList, position)
    }

    fun filter(text: String) {
        val filteredList: MutableList<PairSet> = mutableListOf()
        for (item in pairSetList) {
            if (item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                item.details.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        viewState.obtainFilteredList(filteredList)
    }

    fun onAddNewPairSet() {
        for(i in 0..30){
            val plural = context.resources.getQuantityString(R.plurals.plurals_2, i, i)
            Log.d("plural", "onAddNewPairSet: i = $i // plural = $plural")
        }
        viewState.showAddNewPairSetDialog()
    }

    fun addNewPairSet(inputPairSetName: String, inputPairSetDetails: String) {
        pairSetList.add(
            index = 0,
            element = PairSet(name = inputPairSetName, details = inputPairSetDetails)
        )
        viewState.updateRecyclerOnAdded(pairSetList)
    }



}