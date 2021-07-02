package ru.arcadudu.danatest_v030.pairSetFragment

import android.content.Context
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.utils.PairsetListSPHandler
import java.text.SimpleDateFormat
import java.util.*


@InjectViewState
class PairsetFragmentPresenter : MvpPresenter<PairsetFragmentView>() {

    private var pairsetList: MutableList<Pairset> = mutableListOf()
    private var filteredList: MutableList<Pairset> = mutableListOf()
    private var removedPairsetTrashBin: MutableList<Pairset> = mutableListOf()
    private var lastRemovedPairsetPosition = 0

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
        pairsetList.apply {
            clear()
            addAll(pairsetListSPHandler.loadSpPairsetList())
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
        val filteredListIsEmpty = filteredList.count() == 0
        val chosenPairset =
            if (filteredListIsEmpty) pairsetList[swipePosition] else filteredList[swipePosition]

        when (chosenPairset.getPairList().count()) {
            0 -> viewState.showOnEmptyPairsetDialog(chosenPairset)
            else -> viewState.showStartTestDialog(chosenPairset)
        }

    }

    fun removePairsetAtPosition(position: Int) {
        lastRemovedPairsetPosition = position
        removedPairsetTrashBin.apply {
            clear()
            add(pairsetList[position])
        }
        pairsetList.removeAt(position)
        viewState.apply {
            updateRecyclerOnRemoved(pairsetList, position)
            updateViewOnEmptyPairsetList(pairsetList.count())
            showOnRemoveSnackbar(removedPairsetTrashBin[0].name)
        }

        pairsetListSPHandler.saveSpPairsetList(pairsetList)
    }

    fun restoreDeletedPairset() {
        pairsetList.add(lastRemovedPairsetPosition, removedPairsetTrashBin[0])
        viewState.apply {
            updateRecyclerOnRestored(pairsetList, lastRemovedPairsetPosition)
            updateViewOnEmptyPairsetList(pairsetList.count())
        }
        pairsetListSPHandler.saveSpPairsetList(pairsetList)
    }

    fun filter(text: String) {
        filteredList.clear()
        for (item in pairsetList) {
            if (item.name.toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT))
            ) filteredList.add(item)

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
            updateViewOnEmptyPairsetList(pairsetList.count())
        }
    }


    fun checkIfThereAnyPairsets() {
        viewState.updateViewOnEmptyPairsetList(pairsetList.count())
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