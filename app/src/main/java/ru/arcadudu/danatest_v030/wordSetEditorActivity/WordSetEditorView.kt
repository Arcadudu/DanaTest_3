package ru.arcadudu.danatest_v030.wordSetEditorActivity

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface WordSetEditorView : MvpView {

    fun obtainFilteredList(filteredList: MutableList<Pair>)

    fun showBtnClearAll(isStringEmpty: Boolean)

    fun showRemovePairDialog(chosenPairKey: String, chosenPairValue: String, position: Int)
    fun showAddNewPairDialog()
    fun onSuccessfulAddedPair()

    fun notifyAdapterOnSwap(fromPosition: Int, toPosition: Int)
    fun notifyAdapterOnRemove(removePosition: Int)
}