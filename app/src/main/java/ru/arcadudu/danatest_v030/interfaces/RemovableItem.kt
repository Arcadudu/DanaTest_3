package ru.arcadudu.danatest_v030.interfaces

import ru.arcadudu.danatest_v030.models.Pairset

interface RemovableItem {

    fun obtainRemovableItemName(itemName:String)

    fun removeRemovableItem(pairset:Pairset)
}