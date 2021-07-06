package ru.arcadudu.danatest_v030.utils

import androidx.recyclerview.widget.DiffUtil
import ru.arcadudu.danatest_v030.models.Pair

class PairDiffUtil(private val oldList: MutableList<Pair>, private val newList: MutableList<Pair>) :
    DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size


    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].pairId == newList[newItemPosition].pairId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition].pairId != newList[newItemPosition].pairId -> false
            oldList[oldItemPosition].pairKey != newList[newItemPosition].pairKey -> false
            oldList[oldItemPosition].pairValue != newList[newItemPosition].pairValue -> false
            else -> true

        }
    }
}