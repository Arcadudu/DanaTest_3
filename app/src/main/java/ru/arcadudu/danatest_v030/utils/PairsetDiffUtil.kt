package ru.arcadudu.danatest_v030.utils

import androidx.recyclerview.widget.DiffUtil
import ru.arcadudu.danatest_v030.models.PairSet

class PairsetDiffUtil(
    private val oldPairsetList: MutableList<PairSet>,
    private val newPairsetList: MutableList<PairSet>
) : DiffUtil.Callback() {


    override fun getOldListSize() = oldPairsetList.size

    override fun getNewListSize() = newPairsetList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPairsetList[oldItemPosition] == newPairsetList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldPairsetList[oldItemPosition].name != newPairsetList[newItemPosition].name -> false
            oldPairsetList[oldItemPosition].date != newPairsetList[newItemPosition].date ->false
//            TODO: do proper comparing inner pairSets's lists
//            oldPairsetList[oldItemPosition].getPairList() == newPairsetList[newItemPosition].getPairList() -> false
            else -> true
        }

    }
}