package ru.arcadudu.danatest_v030.utils

import androidx.recyclerview.widget.DiffUtil
import ru.arcadudu.danatest_v030.models.Pairset

class PairsetDiffUtil(
    private val oldPairsetList: MutableList<Pairset>,
    private val newPairsetList: MutableList<Pairset>
) : DiffUtil.Callback() {


    override fun getOldListSize() = oldPairsetList.size

    override fun getNewListSize() = newPairsetList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPairsetList[oldItemPosition].pairsetId == newPairsetList[newItemPosition].pairsetId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldPairsetList[oldItemPosition].pairsetId != newPairsetList[newItemPosition].pairsetId -> false
            oldPairsetList[oldItemPosition].name != newPairsetList[newItemPosition].name -> false
            oldPairsetList[oldItemPosition].date != newPairsetList[newItemPosition].date -> false
            else -> true
        }
    }
}