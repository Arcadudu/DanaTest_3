package ru.arcadudu.danatest_v030.pairSetFragment

import android.content.Context
import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.utils.PairsetListSPHandler
import java.text.SimpleDateFormat
import java.util.*


@InjectViewState
class PairsetFragmentPresenter : MvpPresenter<PairsetFragmentView>() {

    private lateinit var pairsetList: MutableList<Pairset>

    private lateinit var pairsetListSPHandler: PairsetListSPHandler
    private lateinit var context: Context


    fun captureContext(context: Context) {
        this.context = context
        pairsetListSPHandler = PairsetListSPHandler(context)
    }

    fun providePairsetListCount() {
        val message = context.resources.getQuantityString(
            R.plurals.plurals_2,
            pairsetList.count(),
            pairsetList.count()
        )
        viewState.updateToolbarInfo(message)
    }

    fun initiatePairsetList() {
        pairsetList = mutableListOf()
        pairsetList.apply {
            clear()
            addAll(pairsetListSPHandler.loadSpPairsetList())
        }
        pairsetList.forEach {
            Log.d("pairset", it.name + "\n")
        }
        pairsetListSPHandler.saveSpPairsetList(pairsetList)

    }

    fun providePairsetList() {
        viewState.retrievePairsetList(pairsetList)
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(pairsetList, fromPosition, toPosition)
        pairsetListSPHandler.saveSpPairsetList(pairsetList)
        viewState.updateRecyclerOnSwap(pairsetList, fromPosition, toPosition)
    }

    fun onSwipedLeft(swipePosition: Int) {
        val chosenPairSet = pairsetList[swipePosition]
        viewState.showRemovePairsetDialog(
            chosenPairSet.name,
            chosenPairSet.date,
            position = swipePosition,
            pairCount = chosenPairSet.getPairList().count()
        )
    }

    fun onSwipedRight(swipePosition: Int) {
        val chosenPairset = pairsetList[swipePosition]
        if (chosenPairset.getPairList().count() != 0) {
            viewState.showStartTestDialog(chosenPairset)
        } else {
            viewState.showOnEmptyPairsetDialog(chosenPairset)
        }
    }

    fun removePairsetAtPosition(position: Int) {
        pairsetList.removeAt(position)
        viewState.apply {
            updateRecyclerOnRemoved(pairsetList, position)
            setOnEmptyStub(pairsetList.count())
        }

        pairsetListSPHandler.saveSpPairsetList(pairsetList)
    }

    fun filter(text: String) {
        val filteredList: MutableList<Pairset> = mutableListOf()
        for (item in pairsetList) {
            if (item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        viewState.obtainFilteredList(filteredList)
    }

    fun addNewPairset(inputPairSetName: String) {
        val dateOfAdding =
            SimpleDateFormat("dd MMMM yyyy kk:mm", Locale.getDefault()).format(Date()).toString()

        pairsetList.add(
            index = 0,
            element = Pairset(name = inputPairSetName, date = dateOfAdding)
        )
        pairsetListSPHandler.saveSpPairsetList(pairsetList)
        viewState.apply {
            updateRecyclerOnAdded(pairsetList)
            setOnEmptyStub(pairsetList.count())
        }
    }

    fun checkIfThereAnyPairsets() {
        viewState.setOnEmptyStub(pairsetList.count())
    }


    fun sortPairsetList(sortId: Int) {
        when (sortId) {
            0 -> pairsetList.sortBy { it.name }
            1 -> pairsetList.sortByDescending { it.name }
            2 -> pairsetList.sortBy { it.getPairList().count() }
            3 -> pairsetList.sortByDescending { it.getPairList().count() }
            4 -> pairsetList.sortByDescending { it.date }
            5 -> pairsetList.sortBy { it.date }
        }
        pairsetListSPHandler.saveSpPairsetList(pairsetList)
        viewState.updateFragmentOnSorted(pairsetList)

    }

}