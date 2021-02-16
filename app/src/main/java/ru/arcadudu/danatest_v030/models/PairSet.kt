package ru.arcadudu.danatest_v030.models

import java.io.Serializable

open class PairSet(var name:String, var description:String, var isFavorites:Boolean = false) : Serializable {
    private var pairList:MutableList<Pair> = mutableListOf()

    fun getPairList() = pairList

    fun addPair(key:String, description: String){
        val newPair = Pair(key, description)
        pairList.add(newPair)
    }



}