package ru.arcadudu.danatest_v030.models

import java.io.Serializable

open class PairSet(var name:String, var details:String) : Serializable {
    private var pairList:MutableList<Pair> = mutableListOf()

    fun getPairList() = pairList

    fun addPair(key:String, details: String){
        val newPair = Pair(key, details)
        pairList.add(newPair)
    }



}