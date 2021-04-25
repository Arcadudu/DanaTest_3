package ru.arcadudu.danatest_v030.pairSetFragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.utils.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


@InjectViewState
class PairSetFragmentPresenter : MvpPresenter<PairSetFragmentView>() {

    private lateinit var pairSetList: MutableList<PairSet>
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    private lateinit var pairsetListSPHandler :PairsetListSPHandler


    private fun saveData() {
        sharedPreferences =
            context.getSharedPreferences(APP_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        gson = Gson()
        val json = gson.toJson(pairSetList)
        editor.putString(SHARED_PREFERENCES_PAIRSET_LIST, json)
        editor.apply()
    }

    private fun loadData() {
        sharedPreferences =
            context.getSharedPreferences(APP_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        gson = Gson()
        val json = sharedPreferences.getString(SHARED_PREFERENCES_PAIRSET_LIST, null)
        val type: Type = object : TypeToken<ArrayList<PairSet?>?>() {}.type

        // if user visits app for the first time
        if (gson.fromJson<Any>(json, type) == null) {
            getDefaultPairsetList()

        // if user already has visited app before
        } else {
            pairSetList = gson.fromJson<Any>(json, type) as MutableList<PairSet>
        }
    }

    fun captureContext(context: Context) {
        this.context = context
        pairsetListSPHandler = PairsetListSPHandler(context)
    }

    fun providePairSetListCount() {
        val message = context.resources.getQuantityString(
            R.plurals.plurals_2,
            pairSetList.count(),
            pairSetList.count()
        )
        viewState.updateToolbarInfo(message)
    }

    private fun getDefaultPairsetList() {
        pairSetList.clear()
        var pairSetCount = 0
        repeat(3) {
            pairSetCount++
            pairSetList.add(
                PairSet(
                    name = "Набор #$pairSetCount"
                )
            )
        }
        pairSetList.add(0, getTimePairSet())
        pairSetList.add(0, getDummyPairSet())
    }


    fun initiatePairSetList() {
        pairSetList = mutableListOf()
        pairSetList.apply {
            clear()
            addAll(pairsetListSPHandler.loadSpPairsetList())
        }
       // loadData()
    }

    fun providePairSetList() {
        viewState.retrievePairSetList(pairSetList)
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(pairSetList, fromPosition, toPosition)
        viewState.updateRecyclerOnSwap(pairSetList, fromPosition, toPosition)

        pairsetListSPHandler.saveSpPairsetList(pairSetList)
//        saveData()
    }

    fun onSwipedLeft(swipePosition: Int) {
        val chosenPairSet = pairSetList[swipePosition]
        viewState.showRemovePairSetDialog(
            chosenPairSet.name,
            chosenPairSet.date,
            position = swipePosition
        )
    }

    fun onSwipedRight(swipePosition: Int) {
        val chosenPairset = pairSetList[swipePosition]
        if (chosenPairset.getPairList().count() != 0) {
            viewState.showStartTestDialog(chosenPairset)
        } else {
            viewState.showOnEmptyPairSetDialog(chosenPairset)
        }
    }

    fun removePairSetAtPosition(position: Int) {
        pairSetList.removeAt(position)
        viewState.updateRecyclerOnRemoved(pairSetList, position)
        viewState.setOnEmptyStub(pairSetList.count())

        pairsetListSPHandler.saveSpPairsetList(pairSetList)
       // saveData()

    }

    fun filter(text: String) {
        val filteredList: MutableList<PairSet> = mutableListOf()
        for (item in pairSetList) {
            if (item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                item.date.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        viewState.obtainFilteredList(filteredList)
    }

    fun onAddNewPairSet() {
        for (i in 0..30) {
            val plural = context.resources.getQuantityString(R.plurals.plurals_2, i, i)
            Log.d("plural", "onAddNewPairSet: i = $i // plural = $plural")
        }
        viewState.showAddNewPairSetDialog()
    }

    fun addNewPairSet(inputPairSetName: String) {
        val dateOfAdding =
            SimpleDateFormat("dd MMMM yyyy kk:mm", Locale.getDefault()).format(Date()).toString()

        pairSetList.add(
            index = 0,
            element = PairSet(name = inputPairSetName, date = dateOfAdding)
        )

        pairsetListSPHandler.saveSpPairsetList(pairSetList)
       // saveData()
        viewState.updateRecyclerOnAdded(pairSetList)
        viewState.setOnEmptyStub(pairSetList.count())

    }

    fun checkIfThereAnyPairsets() {
        viewState.setOnEmptyStub(pairSetList.count())
    }

    fun onFragmentStop() {

        pairsetListSPHandler.saveSpPairsetList(pairSetList)
      // saveData()
    }


}