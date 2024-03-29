package ru.arcadudu.danatest_v030.pairSetFragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.Pairset

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface PairsetFragmentView : MvpView {


    fun updateToolbarInfo(pairSetCounter: String)

    fun retrievePairsetList(pairsetList: MutableList<Pairset>)

    fun updateRecyclerOnSwap(pairsetList: MutableList<Pairset>, fromPosition: Int, toPosition: Int)

    @StateStrategyType(value = SkipStrategy::class)
    fun showRemovePairsetDialog(chosenPairset:Pairset, position: Int, pairCount:Int)

    @StateStrategyType(value = SkipStrategy::class)
    fun showAddNewPairsetDialog()

    @StateStrategyType(value = SkipStrategy::class)
    fun showStartTestDialog(chosenPairset: Pairset)

    @StateStrategyType(value = SkipStrategy::class)
    fun showOnEmptyPairsetDialog(chosenPairset: Pairset)

    fun updateRecyclerOnRemoved(updatedPairsetList: MutableList<Pairset>, position: Int)

    fun obtainFilteredList(filteredList: MutableList<Pairset>)

    fun updateRecyclerOnAdded(pairsetList: MutableList<Pairset>)

    fun updateRecyclerOnRestored(pairsetList:MutableList<Pairset>, restoredPairsetPosition:Int)

    fun putPairsetIdIntoIntent(selectedPairsetId: Int)

    fun updateViewOnEmptyPairsetList(count: Int)

    fun updateFragmentOnSorted(sortedList: MutableList<Pairset>)

    fun showOnRemoveSnackbar(deletedPairsetName: String, pairsetColor:String)


}