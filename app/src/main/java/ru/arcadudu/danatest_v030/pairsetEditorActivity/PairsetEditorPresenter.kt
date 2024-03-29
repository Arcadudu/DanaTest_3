package ru.arcadudu.danatest_v030.pairsetEditorActivity

import android.content.Context
import android.content.Intent
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.utils.PairsetListSPHandler
import ru.arcadudu.danatest_v030.utils.getCreationDate
import java.util.*

@InjectViewState
class PairsetEditorPresenter : MvpPresenter<PairsetEditorView>() {

    private lateinit var currentPairset: Pairset
    private lateinit var currentPairList: MutableList<Pair>
    private var removedPairTrashBin: MutableList<Pair> = mutableListOf()
    private var lastRemovedPairPosition = 0

    private var currentPairsetIndex = 0
    private var currentPairsetID = 0
    private lateinit var pairsetTitle: String
    private lateinit var pairsetActualDate: String


    private lateinit var context: Context
    private lateinit var spHandler: PairsetListSPHandler

    fun captureContext(context: Context) {
        this.context = context
    }

    fun getCurrentPairsetColor(): String = currentPairset.pairsetColor

    fun extractIncomingPairset(
        incomingIntent: Intent,
        pairset_id_tag: String
    ) {
        spHandler = PairsetListSPHandler(context)
        currentPairsetID = incomingIntent.getIntExtra(pairset_id_tag, 0)
        val loadedPairsetList = spHandler.loadSpPairsetList()
        for (pairset in loadedPairsetList) {
            if (pairset.pairsetId == currentPairsetID) {
                currentPairset = pairset
                currentPairsetIndex = loadedPairsetList.indexOf(pairset)
                pairsetTitle = pairset.name
                pairsetActualDate = pairset.date
                currentPairList = pairset.getPairList()
            }
        }
        viewState.setOnEmptyStub(currentPairset.getPairList().count())
    }

    //
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

    fun changePairsetName(newPairsetName: String, pairsetColorValue: String) {
        spHandler = PairsetListSPHandler(context)
        val pairsetListToEdit = spHandler.loadSpPairsetList()
        pairsetListToEdit[currentPairsetIndex].apply {
            name = newPairsetName
            date = getCreationDate()
            pairsetColor = pairsetColorValue
        }
        spHandler.saveSpPairsetList(pairsetListToEdit)
        pairsetTitle = newPairsetName
        viewState.getDataForToolbar(
            pairsetListToEdit[currentPairsetIndex].name,
            pairsetListToEdit[currentPairsetIndex].date
        )
    }


    fun checkIfPairsetIsEmpty() {
        viewState.setOnEmptyStub(currentPairList.count())
    }

    fun checkIfPairsetIsEmptyBoolean(): Boolean {
        return currentPairset.getPairList().count() == 0
    }

    fun provideDataForToolbar() {
        viewState.getDataForToolbar(pairsetTitle, pairsetActualDate)
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
        lastRemovedPairPosition = removePosition
        removedPairTrashBin.apply {
            clear()
            add(currentPairList[removePosition])
        }
        currentPairList.removeAt(removePosition)
        applyPairsetChangesIntoPairsetList(currentPairList)
        viewState.apply {
            updateRecyclerOnRemoved(currentPairList, removePosition)
            updateViewOnEmptyPairList(currentPairList.count())
            showOnRemovePairSnackbar(removedPairTrashBin[0])
        }
    }


    fun onToolbarClick() {
        viewState.showEditPairsetNameDialog(currentPairsetName = pairsetTitle)
    }

    fun sortPairlist(sortIndex: Int) {
        when (sortIndex) {
            0 -> currentPairList.sortBy { it.pairKey }
            1 -> currentPairList.sortByDescending { it.pairKey }
            2 -> currentPairList.sortBy { it.pairValue }
            3 -> currentPairList.sortByDescending { it.pairValue }
        }
        applyPairsetChangesIntoPairsetList(currentPairList)
        viewState.updateViewOnSortedPairlist(currentPairList, sortIndex)
    }

    fun restoreDeletedPair() {
        currentPairList.add(lastRemovedPairPosition, removedPairTrashBin[0])
        viewState.apply {
            updateRecyclerOnRestored(currentPairList, lastRemovedPairPosition)
            updateViewOnEmptyPairList(currentPairList.count())
        }
        applyPairsetChangesIntoPairsetList(currentPairList)
    }

    fun removePassedTestRewardsOnAddedPair() {
        val pairsetListToEdit = spHandler.loadSpPairsetList()
        val pairsetToBeUnrewarded = pairsetListToEdit[currentPairsetIndex]
        if (pairsetToBeUnrewarded.translateTestPassed ||
            pairsetToBeUnrewarded.variantsTestPassed ||
            pairsetToBeUnrewarded.shuffleTestPassed
        ) {
            viewState.showOnRemoveRewardsSnackBar(pairsetTitle)
        }
        pairsetToBeUnrewarded.apply {
            setPairsetPassedVariantsTest(false)
            setPairsetPassedTranslateTest(false)
            setPairsetPassedShuffleTest(false)
        }
        pairsetListToEdit.apply {
            removeAt(currentPairsetIndex)
            add(currentPairsetIndex, pairsetToBeUnrewarded)
        }
        spHandler.saveSpPairsetList(pairsetListToEdit)

    }

    fun checkPairsetHasRewards(): Boolean {
        return (currentPairset.shuffleTestPassed || currentPairset.variantsTestPassed || currentPairset.translateTestPassed)
    }


}