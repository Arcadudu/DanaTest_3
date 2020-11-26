package ru.arcadudu.danatest_v030.models

import java.io.Serializable

open class WordSet(var name:String, var description:String, var isFavorites:Boolean = false) : Serializable {
    private var pairList:MutableList<Pair> = mutableListOf()

    init {

        if(isFavorites){
            name = "Избранный набор"
            description = "Сюда попадают избранные Вами пары из других наборов"
        }

    }

    val listLength = pairList.size

    fun addPair(key:String, description: String){
        val newPair = Pair(key, description)
        pairList.add(newPair)
    }



}