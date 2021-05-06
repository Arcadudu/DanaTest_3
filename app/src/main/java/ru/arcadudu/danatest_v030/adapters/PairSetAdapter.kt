package ru.arcadudu.danatest_v030.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairsetRowLayoutBinding
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.pairSetFragment.PairSetFragmentView
import ru.arcadudu.danatest_v030.utils.PairsetDiffUtil
import java.text.SimpleDateFormat
import java.util.*

class PairSetAdapter :
    RecyclerView.Adapter<PairSetAdapter.PairSetViewHolder>() {

    private var pairsetList: MutableList<PairSet> = mutableListOf()
    private lateinit var pairSetFragmentImplementation: PairSetFragmentView

    private lateinit var pairSetFragmentContext: Context

    fun captureContext(context: Context) {
        this.pairSetFragmentContext = context
    }


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
        private val simpleDateFormatExact = SimpleDateFormat("dd MMMM", Locale.getDefault())
        private val background = { drawableResource: Int ->
            ResourcesCompat.getDrawable(
                pairSetFragmentContext.resources,
                drawableResource,
                pairSetFragmentContext.theme
            )
        }


        init {
            itemView.setOnClickListener {
                pairSetFragmentImplementation.putPairsetIndexIntoIntent(bindingAdapterPosition)
            }
        }

        fun bind(pairSet: PairSet) {
            binding.apply {
                tvItemTitle.text = pairSet.name.capitalize(Locale.ROOT).trim()
                val date = simpleDateFormatExact.parse(pairSet.date)
                tvItemDetails.text = simpleDateFormatExact.format(date!!)
                tvPairCounterBody.apply {
                    text = pairSet.getPairList().count().toString()
                    background = when (pairSet.getPairList().count()) {
                        0 -> background(R.drawable.pair_counter_container_has_no_pairs)
                        else -> background(R.drawable.pair_counter_container_pairs)
                    }
                }
            }
        }
    }


}


