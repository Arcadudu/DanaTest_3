package ru.arcadudu.danatest_v030.pairsetEditorActivity

import android.content.Intent
import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import java.util.*

@InjectViewState
class PairsetEditorPresenter : MvpPresenter<PairsetEditorView>() {

    private lateinit var currentPairSet: PairSet
    private lateinit var currentPairList: MutableList<Pair>
    private lateinit var pairSetTitle: String
    private lateinit var pairSetDetails: String

    fun extractIncomingWordSet(incomingIntent: Intent, INTENT_TAG: String) {
        currentPairSet = incomingIntent.getSerializableExtra(INTENT_TAG) as PairSet
        pairSetTitle = currentPairSet.name
        pairSetDetails = currentPairSet.date
        currentPairList = currentPairSet.getPairList()
    }

    fun checkIfPairsetIsEmpty(){
        viewState.setOnEmptyStub(currentPairList.count())
    }

    fun deliverWordSetForTest(){
        viewState.obtainWordSetForTest(currentPairSet)
    }

    fun provideDataForToolbar() {
        val pairsetUpdateExactDateString =
        viewState.getDataForToolbar(pairSetTitle, pairSetDetails)
    }

    fun providePairList(){
        viewState.initPairList(currentPairList)
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(currentPairList, fromPosition, toPosition)
        viewState.updateRecyclerOnSwap(currentPairList, fromPosition, toPosition)
    }

    fun saveEditedPair(newPairKey:String, newPairValue:String, position:Int){
        currentPairList.removeAt(position)
        viewState.updateRecyclerOnRemoved(currentPairList, removePosition = position)
        currentPairList.add(position, Pair(newPairKey, newPairValue))
        viewState.updateRecyclerOnEditedPair(currentPairList, position)
    }

    fun onAddNewPair() {
        viewState.showAddNewPairDialog()
    }

    fun addNewPair(inputKey: String, inputValue: String) {
        currentPairList.add(index = 0, element = Pair(inputKey, inputValue))
        viewState.updateRecyclerOnAdded(currentPairList)
        viewState.setOnEmptyStub(currentPairList.count())
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
        Log.d("Swiper", "Presenter: onSwipedLeft: position = $swipePosition")
        viewState.showRemovePairDialog(
            chosenPair.pairKey,
            chosenPair.pairValue,
            position = swipePosition
        )

    }

    fun removePairAtPosition(removePosition: Int) {
        currentPairList.removeAt(removePosition)
        viewState.updateRecyclerOnRemoved(currentPairList, removePosition)
        viewState.setOnEmptyStub(currentPairList.count())
    }


}