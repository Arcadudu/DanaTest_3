package ru.arcadudu.danatest_v030.wordSetEditorActivity

import android.content.Intent
import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import java.util.*

@InjectViewState
class WsEditorPresenter : MvpPresenter<WordSetEditorView>() {

    private lateinit var currentWordSet: WordSet
    private lateinit var currentPairList: MutableList<Pair>
    lateinit var wordSetTitle: String
    lateinit var wordSetDescription: String

    fun extractIncomingWordSet(incomingIntent: Intent, INTENT_TAG: String) {
        currentWordSet = incomingIntent.getSerializableExtra(INTENT_TAG) as WordSet
        wordSetTitle = currentWordSet.name
        wordSetDescription = currentWordSet.description
        currentPairList = currentWordSet.getPairList()
    }

    fun providePairList() = currentPairList

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(currentPairList, fromPosition, toPosition)
        viewState.notifyAdapterOnSwap(fromPosition, toPosition)
    }

    fun onAddNewPair() {
        viewState.showAddNewPairDialog()
    }

//    fun onSwipedLeft(swipePosition: Int) {

    //providePairList(currentPairList){


    private fun filter(text: String) {
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


    fun checkStringForLetters(resultString: String) {
        val isStringEmpty = resultString.isEmpty()
        viewState.showBtnClearAll(!isStringEmpty)
        filter(resultString)
    }

    fun addNewPair(inputKey: String, inputValue: String) {
        Log.d("presenter", "addNewPair: list size before: ${currentPairList.size}")
        currentPairList.add(index = 0, element = Pair(inputKey, inputValue))
        Log.d("presenter", "addNewPair: list size after: ${currentPairList.size}")
        viewState.onSuccessfulAddedPair()
    }

    fun onSwipedLeft(swipePosition: Int) {
        val chosenPair = currentPairList[swipePosition]
        viewState.showRemovePairDialog(chosenPair.pairKey, chosenPair.pairValue, position = swipePosition)

    }

    fun removePairFromList(removePosition:Int) {
        currentPairList.removeAt(removePosition)
        viewState.notifyAdapterOnRemove(removePosition)
    }

    //checkStringForLetters(resultString){
//      val isStringEmpty = resultString.isEmpty
//      viewState.showBtnClearAll(!isStringEmpty) ***
//      filter(resultString)
//      }


}