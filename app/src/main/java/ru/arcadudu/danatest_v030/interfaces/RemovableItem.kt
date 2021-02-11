package ru.arcadudu.danatest_v030.interfaces

import ru.arcadudu.danatest_v030.models.PairSet

interface RemovableItem {

    fun obtainRemovableItemName(itemName:String)

    fun removeRemovableItem(pairSet:PairSet)
}