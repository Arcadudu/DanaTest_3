package ru.arcadudu.danatest_v030.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

val simpleDateFormatExact = SimpleDateFormat("dd MMMM yyyy kk:mm", Locale.getDefault())
var creationDate = simpleDateFormatExact.format(Date()).toString()

open class Pairset(
    var name: String,
    var date: String = creationDate,
    val pairsetId: Int = (1..1000000).random()
) : Serializable {
    private var pairList: MutableList<Pair> = mutableListOf()


    fun getPairList() = pairList


    fun setNewPairList(newPairList: MutableList<Pair>) {
        this.pairList.apply {
            clear()
            addAll(newPairList)
        }
    }

    fun addPair(key: String, value: String) {
        val newPair = Pair(key, value)
        pairList.add(newPair)
    }

    fun getPairListKeySet(): MutableList<String> {
        val keySet: MutableList<String> = mutableListOf()
        for (pair in pairList) keySet.add(pair.pairKey)
        return keySet
    }


    fun getPairListValueSet(): MutableList<String> {
        val valueSet: MutableList<String> = mutableListOf()
        for (pair in pairList) valueSet.add(pair.pairValue)
        return valueSet
    }
}

