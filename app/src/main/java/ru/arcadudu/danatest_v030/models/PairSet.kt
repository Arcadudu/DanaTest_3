package ru.arcadudu.danatest_v030.models

import java.io.Serializable

open class PairSet(var name:String, var description:String, var isFavorites:Boolean = false) : Serializable {
    private var pairList:MutableList<Pair> = mutableListOf()

    init {

        if(isFavorites){
            name = "Избранный набор"
            description = "Сюда попадают избранные Вами пары из других наборов"
        }

    }

    fun getPairList() = pairList

    val pairListSize = pairList.size

    fun addPair(key:String, description: String){
        val newPair = Pair(key, description)
        pairList.add(newPair)
    }



}