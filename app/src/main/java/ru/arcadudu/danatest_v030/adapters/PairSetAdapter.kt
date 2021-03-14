package ru.arcadudu.danatest_v030.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairsetRowLayoutBinding
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.pairSetFragment.PairSetFragmentView
import ru.arcadudu.danatest_v030.utils.PairsetDiffUtil
import java.util.*

class PairSetAdapter :
    RecyclerView.Adapter<PairSetAdapter.PairSetViewHolder>() {

    private var pairsetList: MutableList<PairSet> = mutableListOf()
    private lateinit var pairSetFragmentImplementation: PairSetFragmentView


    fun onItemClickCallback(pairSetFragmentImplementation: PairSetFragmentView) {
        this.pairSetFragmentImplementation = pairSetFragmentImplementation
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairSetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PairSetViewHolder(inflater.inflate(R.layout.pairset_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: PairSetViewHolder, position: Int) {
        holder.bind(pairsetList[position])
    }

    override fun getItemCount() = pairsetList.count()

    //fun submitPairs(newList: MutableList<Pair>) {
    //        val pairDiffUtil = PairDiffUtil(pairList, newList)
    //        val diffResult = DiffUtil.calculateDiff(pairDiffUtil)
    //        pairList = newList
    //        diffResult.dispatchUpdatesTo(this)
    //    }

    fun submitList(newPairsetList: MutableList<PairSet>) {
        val pairsetDiffUtil = PairsetDiffUtil(pairsetList, newPairsetList)
        val diffResult = DiffUtil.calculateDiff(pairsetDiffUtil)
        pairsetList = newPairsetList
        diffResult.dispatchUpdatesTo(this)
    }

    fun filterList(filteredList: MutableList<PairSet>) {
        pairsetList = filteredList
    }


    //REGULAR
    inner class PairSetViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = PairsetRowLayoutBinding.bind(view)

        init {
            itemView.setOnClickListener {
                val chosenPairSet = pairsetList[bindingAdapterPosition]
                pairSetFragmentImplementation.putPairSetIntoIntent(chosenPairSet)
                           }
        }

        fun bind(pairSet: PairSet) {
            binding.apply {
                tvItemTitle.text = pairSet.name.capitalize(Locale.ROOT).trim()
                tvItemDetails.text = pairSet.details.capitalize(Locale.ROOT).trim()
            }
        }


    }


}


