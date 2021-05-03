package ru.arcadudu.danatest_v030.pairSetFragment

import android.content.Context
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.utils.PairsetListSPHandler
import java.text.SimpleDateFormat
import java.util.*


@InjectViewState
class PairSetFragmentPresenter : MvpPresenter<PairSetFragmentView>() {

    private lateinit var pairSetList: MutableList<PairSet>

    private lateinit var pairsetListSPHandler: PairsetListSPHandler
    private lateinit var context: Context


    fun captureContext(context: Context) {
        this.context = context
        pairsetListSPHandler = PairsetListSPHandler(context)
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
        pairSetList.apply {
            clear()
            addAll(pairsetListSPHandler.loadSpPairsetList())
        }
        pairsetListSPHandler.saveSpPairsetList(pairSetList)
    }

    fun providePairSetList() {
        viewState.retrievePairSetList(pairSetList)
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(pairSetList, fromPosition, toPosition)
        viewState.updateRecyclerOnSwap(pairSetList, fromPosition, toPosition)
        pairsetListSPHandler.saveSpPairsetList(pairSetList)
    }

    fun onSwipedLeft(swipePosition: Int) {
        val chosenPairSet = pairSetList[swipePosition]
        viewState.showRemovePairSetDialog(
            chosenPairSet.name,
            chosenPairSet.date,
            position = swipePosition
        )
    }

    fun onSwipedRight(swipePosition: Int) {
        val chosenPairset = pairSetList[swipePosition]
        if (chosenPairset.getPairList().count() != 0) {
            viewState.showStartTestDialog(chosenPairset)
        } else {
            viewState.showOnEmptyPairSetDialog(chosenPairset)
        }
    }

    fun removePairSetAtPosition(position: Int) {
        pairSetList.removeAt(position)
        viewState.apply {
            updateRecyclerOnRemoved(pairSetList, position)
            setOnEmptyStub(pairSetList.count())
        }

        pairsetListSPHandler.saveSpPairsetList(pairSetList)
    }

    fun filter(text: String) {
        val filteredList: MutableList<PairSet> = mutableListOf()
        for (item in pairSetList) {
            if (item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        viewState.obtainFilteredList(filteredList)
    }

    fun addNewPairSet(inputPairSetName: String) {
        val dateOfAdding =
            SimpleDateFormat("dd MMMM yyyy kk:mm", Locale.getDefault()).format(Date()).toString()

        pairSetList.add(
            index = 0,
            element = PairSet(name = inputPairSetName, date = dateOfAdding)
        )
        pairsetListSPHandler.saveSpPairsetList(pairSetList)
        viewState.apply {
            updateRecyclerOnAdded(pairSetList)
            setOnEmptyStub(pairSetList.count())
        }
    }

    fun checkIfThereAnyPairsets() {
        viewState.setOnEmptyStub(pairSetList.count())
    }

}