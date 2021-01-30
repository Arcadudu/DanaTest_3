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
//        val chosenPair = currentPairList[swipePosition]
//        viewState.showAddNewPairDialog(chosenPair.pairKey, chosenPair.pairValue)
//    }


    //providePairList(currentPairList){
//      viewState.obtainPairList(currentPairList) ***
//    }

    //onMove(fromPosition, toPosition){
//      Collections.swap(currentPairList, fromPosition, toPosition)
//      viewState.onSwap(currentPairList)
//    }

    //showRemoveItemDialog(){}

    //showAddItemDialog(){}

    private fun filter(text: String) {
        val filteredList: MutableList<Pair> = mutableListOf()
        for (item in currentPairList) {
            if (item.pairKey.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                item.pairValue.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        // here view receives and filters list
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

    //checkStringForLetters(resultString){
//      val isStringEmpty = resultString.isEmpty
//      viewState.showBtnClearAll(!isStringEmpty) ***
//      filter(resultString)
//      }


}