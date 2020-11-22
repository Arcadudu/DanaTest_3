package ru.arcadudu.danatest_v030.models

import java.io.Serializable

class WordSet(val name:String, val description:String) : Serializable {
    private val pairList:MutableList<Pair> = mutableListOf()
    val listLength = pairList.size

    fun addPair(key:String, description: String){
        val newPair = Pair(key, description)
        pairList.add(newPair)
    }
}