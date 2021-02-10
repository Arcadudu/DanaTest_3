package ru.arcadudu.danatest_v030.wordSetEditorActivity

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface WordSetEditorView : MvpView {

    fun obtainDataForToolbar(wordSetTitle: String, wordSetDescription: String)

    fun initPairList(currentPairList: MutableList<Pair>)

    fun obtainFilteredList(filteredList: MutableList<Pair>)

    fun updateRecyclerOnRemoved(updatedPairList: MutableList<Pair>, removePosition: Int)

    /*addPosition always = 1*/
    fun updateRecyclerOnAdded(updatedPairList: MutableList<Pair>)

    fun updateRecyclerOnEditedPair(updatedPairList:MutableList<Pair>, position:Int)

    fun showBtnClearAll(isStringEmpty: Boolean)

    fun showRemovePairDialog(chosenPairKey: String, chosenPairValue: String, position: Int)

    fun showEditPairDialog(position: Int, pairKey: String, pairValue: String)

    fun showAddNewPairDialog()

    fun updateRecyclerOnSwap(updatedPairList: MutableList<Pair>, fromPosition: Int, toPosition: Int)

    fun obtainWordSetForTest(currentWordSet: WordSet)


}