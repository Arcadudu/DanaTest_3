package ru.arcadudu.danatest_v030.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairRowLayoutBinding
import ru.arcadudu.danatest_v030.models.Pair

class PairRowAdapter : RecyclerView.Adapter<PairRowAdapter.PairRowViewHolder>() {
    private var pairList: MutableList<Pair> = mutableListOf()
    private lateinit var pair: Pair

    fun submitPairs(list: MutableList<Pair>) {
        pairList = list
    }

    fun removeItem(position: Int){
        pairList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairRowViewHolder {
        return PairRowViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.pair_row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PairRowViewHolder, position: Int) {
        pair = pairList[position]
        holder.bind(pair)
    }

    override fun getItemCount() = pairList.count()
    fun filterList(filteredList: MutableList<Pair>) {
        pairList = filteredList
        notifyDataSetChanged()
    }

    inner class PairRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = PairRowLayoutBinding.bind(itemView)

        fun bind(pair: Pair) {

            binding.apply {
                tvKey.text = pair.key
                tvValue.text = pair.value
            }

        }
    }
}