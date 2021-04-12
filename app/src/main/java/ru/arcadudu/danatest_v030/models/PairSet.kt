package ru.arcadudu.danatest_v030.models

import java.io.Serializable

open class PairSet(var name: String, var details: String = "") : Serializable {
    private var pairList: MutableList<Pair> = mutableListOf()

    fun getPairList() = pairList

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


    fun addPair(key: String, details: String) {
        val newPair = Pair(key, details)
        pairList.add(newPair)
    }


}