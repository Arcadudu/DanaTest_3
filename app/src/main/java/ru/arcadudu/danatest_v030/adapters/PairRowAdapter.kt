package ru.arcadudu.danatest_v030.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairRowLayoutBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.pairSetEditorActivity.PairSetEditorView
import java.util.*

class
PairRowAdapter : RecyclerView.Adapter<PairRowAdapter.PairRowViewHolder>() {
    private var pairList: MutableList<Pair> = mutableListOf()
    private lateinit var pair: Pair
    private lateinit var activityImplementation: PairSetEditorView

    fun submitPairs(list: MutableList<Pair>) {
        pairList = list
    }

    fun onItemClickCallback(activityImplementation: PairSetEditorView) {
        this.activityImplementation = activityImplementation
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

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                val currentPairKey = pairList[position].pairKey
                val currentPairValue = pairList[position].pairValue
                activityImplementation.showEditPairDialog(
                    position = position,
                    pairKey = currentPairKey,
                    pairValue = currentPairValue
                )
            }
        }

        fun bind(pair: Pair) {

            binding.apply {
                tvKey.text = pair.pairKey.capitalize(Locale.ROOT).trim()
                tvValue.text = pair.pairValue.capitalize(Locale.ROOT).trim()
            }

        }
    }
}