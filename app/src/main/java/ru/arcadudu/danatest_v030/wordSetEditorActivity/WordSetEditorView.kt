package ru.arcadudu.danatest_v030.wordSetEditorActivity

import androidx.appcompat.widget.Toolbar
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pair

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface WordSetEditorView : MvpView {

    //  viewState.decorateToolbar(wordSetTitle, wordSetDetails) ***
    //    fun prepareToolbar(targetToolbar: Toolbar, wordSetTitle: String, wordSetDescription: String)

    fun showAddNewPairDialog()

    fun notifyAdapterOnSwap(fromPosition: Int, toPosition: Int)

    //  viewState.obtainPairList(currentPairList) ***
    fun obtainPairList(currentPairList: MutableList<Pair>)

    //  viewState.onSwap(currentPairList)
    fun onSwap(currentPairList: MutableList<Pair>)

    //  viewState.obtainFilteredList(filteredList) ***
    fun obtainFilteredList(filteredList: MutableList<Pair>)

    //  viewState.showBtnClearAll(!isStringEmpty) ***
    fun showBtnClearAll(isStringEmpty: Boolean)

    fun onSuccessfulAddedPair()

    fun showRemovePairDialog(chosenPairKey: String, chosenPairValue: String, position: Int)

    fun notifyAdapterOnRemove(removePosition: Int)


}