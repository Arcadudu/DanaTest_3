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
    private lateinit var wordSetTitle: String
    private lateinit var wordSetDescription: String

    fun extractIncomingWordSet(incomingIntent: Intent, INTENT_TAG: String) {
        currentWordSet = incomingIntent.getSerializableExtra(INTENT_TAG) as WordSet
        wordSetTitle = currentWordSet.name
        wordSetDescription = currentWordSet.description
        currentPairList = currentWordSet.getPairList()
    }

    fun provideDataForToolbar() {
        viewState.obtainDataForToolbar(wordSetTitle, wordSetDescription)
    }

    fun providePairList() = currentPairList

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(currentPairList, fromPosition, toPosition)
        viewState.updateRecyclerOnSwap(currentPairList, fromPosition, toPosition)
    }

    fun onAddNewPair() {
        viewState.showAddNewPairDialog()
    }

    fun addNewPair(inputKey: String, inputValue: String) {
        currentPairList.add(index = 0, element = Pair(inputKey, inputValue))
        viewState.updateRecyclerOnAdded(currentPairList)
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
    }


}