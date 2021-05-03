package ru.arcadudu.danatest_v030.pairsetEditorActivity

import android.content.Context
import android.content.Intent
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.utils.PairsetListSPHandler
import ru.arcadudu.danatest_v030.utils.getCreationDate
import java.util.*

@InjectViewState
class PairsetEditorPresenter : MvpPresenter<PairsetEditorView>() {

    private lateinit var currentPairSet: PairSet
    private lateinit var currentPairList: MutableList<Pair>
    private var currentPairsetIndex = 0
    private lateinit var pairSetTitle: String
    private lateinit var pairSetDetails: String

    private lateinit var context: Context
    private lateinit var spHandler: PairsetListSPHandler

    fun captureContext(context: Context) {
        this.context = context
    }

    private fun applyPairsetChangesIntoPairsetList(editedPairList: MutableList<Pair>) {
        spHandler = PairsetListSPHandler(context)
        val pairsetListToEdit = spHandler.loadSpPairsetList()
        val pairsetToEdit = pairsetListToEdit[currentPairsetIndex]
        pairsetToEdit.setNewPairList(editedPairList)
        pairsetToEdit.date = getCreationDate()
        pairsetListToEdit.apply {
            removeAt(currentPairsetIndex)
            add(currentPairsetIndex, pairsetToEdit)
        }
        spHandler.saveSpPairsetList(pairsetListToEdit)
        viewState.getDataForToolbar(pairsetToEdit.name, pairsetToEdit.date)
    }

    fun changePairsetName(newPairsetName: String) {
        spHandler = PairsetListSPHandler(context)
        val pairsetListToEdit = spHandler.loadSpPairsetList()
        pairsetListToEdit[currentPairsetIndex].apply {
            name = newPairsetName
            date = getCreationDate()
        }
        spHandler.saveSpPairsetList(pairsetListToEdit)
        pairSetTitle = newPairsetName
        viewState.getDataForToolbar(
            pairsetListToEdit[currentPairsetIndex].name,
            pairsetListToEdit[currentPairsetIndex].date
        )
    }

    fun extractIncomingPairset(
        incomingIntent: Intent,
        pairset_index_tag: String
    ) {
        spHandler = PairsetListSPHandler(context)
        currentPairsetIndex = incomingIntent.getIntExtra(pairset_index_tag, 0)
        currentPairSet = spHandler.loadSpPairsetList()[currentPairsetIndex]

        pairSetTitle = currentPairSet.name
        pairSetDetails = currentPairSet.date

        currentPairList = currentPairSet.getPairList()
    }


    fun checkIfPairsetIsEmpty() {
        viewState.setOnEmptyStub(currentPairList.count())
    }

    fun deliverPairsetForTest() {
        viewState.obtainPairsetForTest(currentPairSet)
    }

    fun provideDataForToolbar() {
        viewState.getDataForToolbar(pairSetTitle, pairSetDetails)
    }

    fun providePairList() {
        viewState.initPairList(currentPairList)
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(currentPairList, fromPosition, toPosition)
        applyPairsetChangesIntoPairsetList(currentPairList)
        viewState.updateRecyclerOnSwap(currentPairList, fromPosition, toPosition)
    }

    fun saveEditedPair(newPairKey: String, newPairValue: String, position: Int) {
        currentPairList.removeAt(position)
        viewState.updateRecyclerOnRemoved(currentPairList, removePosition = position)
        currentPairList.add(position, Pair(newPairKey, newPairValue))
        applyPairsetChangesIntoPairsetList(currentPairList)
        viewState.updateRecyclerOnEditedPair(currentPairList, position)
    }

    fun onAddNewPair() {
        viewState.showAddNewPairDialog()
    }

    fun addNewPair(inputKey: String, inputValue: String) {
        currentPairList.add(index = 0, element = Pair(inputKey, inputValue))
        applyPairsetChangesIntoPairsetList(currentPairList)
        viewState.apply {
            updateRecyclerOnAdded(currentPairList)
            setOnEmptyStub(currentPairList.count())
        }
    }

    fun filter(text: String) {
        val filteredList: MutableList<Pair> = mutableListOf()
        for (item in currentPairList) {
            if (item.pairKey.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                item.pairValue.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        viewState.obtainFilteredList(filteredList)
    }

    fun onSwipedLeft(swipePosition: Int) {
        val chosenPair = currentPairList[swipePosition]
        viewState.showRemovePairDialog(
            chosenPair.pairKey,
            chosenPair.pairValue,
            position = swipePosition
        )
    }

    fun removePairAtPosition(removePosition: Int) {
        currentPairList.removeAt(removePosition)
        applyPairsetChangesIntoPairsetList(currentPairList)
        viewState.apply {
            updateRecyclerOnRemoved(currentPairList, removePosition)
            setOnEmptyStub(currentPairList.count())
        }
    }

    fun onToolbarClick() {
        viewState.showEditPairsetName(currentPairsetName = pairSetTitle)
    }

}