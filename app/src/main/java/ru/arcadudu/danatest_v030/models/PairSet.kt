package ru.arcadudu.danatest_v030.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

val simpleDateFormatExact = SimpleDateFormat("dd MMMM yyyy kk:mm", Locale.getDefault())
var creationDate = simpleDateFormatExact.format(Date()).toString()

open class PairSet(var name: String, val date: String = creationDate) : Serializable {
    private var pairList: MutableList<Pair> = mutableListOf()


    companion object {
        @JvmStatic
        private val serialVersionUID: Long = 239
    }


    fun getPairList() = pairList

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