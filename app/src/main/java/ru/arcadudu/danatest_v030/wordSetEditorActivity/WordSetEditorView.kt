package ru.arcadudu.danatest_v030.wordSetEditorActivity

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface WordSetEditorView : MvpView {

    fun obtainFilteredList(filteredList: MutableList<Pair>)

    fun updatePairList(updatedPairList: MutableList<Pair>)

    fun updateRecyclerOnRemoved(updatedPairList: MutableList<Pair>, removePosition:Int)

    /*addPosition always = 1*/
    fun updateRecyclerOnAdded(updatedPairList:MutableList<Pair>)

    fun showBtnClearAll(isStringEmpty: Boolean)

    fun showRemovePairDialog(chosenPairKey: String, chosenPairValue: String, position: Int)
    fun showAddNewPairDialog()

    fun updateRecyclerOnSwap(updatedPairList: MutableList<Pair>, fromPosition: Int, toPosition: Int)

}