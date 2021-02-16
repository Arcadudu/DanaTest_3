package ru.arcadudu.danatest_v030.pairSetFragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.arcadudu.danatest_v030.models.PairSet

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface PairSetFragmentView : MvpView {

    fun updateToolbarInfo(pairSetCounter:String)

    fun retrievePairSetList(pairSetList: MutableList<PairSet>)

    fun updateRecyclerOnSwap(pairSetList: MutableList<PairSet>, fromPosition: Int, toPosition: Int)

    fun showRemovePairSetDialog(name: String, description: String, position: Int)

    fun updateRecyclerOnRemoved(updatedPairSetList: MutableList<PairSet>, position: Int)

    fun obtainFilteredList(filteredList: MutableList<PairSet>)

    fun showAddNewPairSetDialog()

    fun updateRecyclerOnAdded(pairSetList: MutableList<PairSet>)

    fun putPairSetIntoIntent(chosenPairSet: PairSet)
}