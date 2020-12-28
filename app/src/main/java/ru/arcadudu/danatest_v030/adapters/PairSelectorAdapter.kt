package ru.arcadudu.danatest_v030.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairSelectorRowBinding
import ru.arcadudu.danatest_v030.models.Pair

class PairSelectorAdapter : RecyclerView.Adapter<PairSelectorAdapter.PairSelectorViewHolder>() {
    private var pairList: MutableList<Pair> = mutableListOf()
    private lateinit var pair: Pair
    private lateinit var context: Context




    fun submitData(list: MutableList<Pair>, context: Context) {
        pairList = list
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairSelectorViewHolder {
        return PairSelectorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.pair_selector_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PairSelectorViewHolder, position: Int) {
        pair = pairList[position]
        holder.bind(pair, position)
    }

    override fun getItemCount() = pairList.count()

    inner class PairSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = PairSelectorRowBinding.bind(itemView)
        private val questWordItem = binding.questWordItem

        fun bind(pair: Pair, position: Int) {
            Log.d("change", "bind: position = $position, selected position = ")
            questWordItem.text = pair.key.trim()
            Log.d("change", "bind: pair.key = ${pair.key}")



            questWordItem.setOnClickListener {


            }


        }


    }
}