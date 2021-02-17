package ru.arcadudu.danatest_v030.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairsetRowLayoutBinding
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.pairSetFragment.PairSetFragmentView

class PairSetAdapter :
    RecyclerView.Adapter<PairSetAdapter.PairSetViewHolder>() {

    private var itemList: MutableList<PairSet> = mutableListOf()
    private lateinit var pairSetFragmentImplementation: PairSetFragmentView


    fun onItemClickCallback(pairSetFragmentImplementation: PairSetFragmentView) {
        this.pairSetFragmentImplementation = pairSetFragmentImplementation
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairSetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PairSetViewHolder(inflater.inflate(R.layout.pairset_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: PairSetViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.count()

    fun submitList(list: MutableList<PairSet>) {
        itemList = list
    }

    fun filterList(filteredList: MutableList<PairSet>) {
        itemList = filteredList
        notifyDataSetChanged()
    }


    //REGULAR
    inner class PairSetViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = PairsetRowLayoutBinding.bind(view)

        init {
            itemView.setOnClickListener {
                val chosenPairSet = itemList[bindingAdapterPosition]
                pairSetFragmentImplementation.putPairSetIntoIntent(chosenPairSet)
                           }
        }

        fun bind(pairSet: PairSet) {
            binding.apply {
                tvItemTitle.text = pairSet.name
                tvItemDetails.text = pairSet.details
            }
        }


    }


}


