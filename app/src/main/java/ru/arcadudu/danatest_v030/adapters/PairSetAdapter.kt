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
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.pairSetFragment.PairsetFragmentView
import ru.arcadudu.danatest_v030.utils.PairsetDiffUtil
import java.text.SimpleDateFormat
import java.util.*

class PairSetAdapter :
    RecyclerView.Adapter<PairSetAdapter.PairSetViewHolder>() {

    private var pairsetList: MutableList<Pairset> = mutableListOf()
    private lateinit var pairsetFragmentImplementation: PairsetFragmentView

    private lateinit var pairSetFragmentContext: Context

    fun captureContext(context: Context) {
        this.pairSetFragmentContext = context
    }


    fun onItemClickCallback(pairsetFragmentImplementation: PairsetFragmentView) {
        this.pairsetFragmentImplementation = pairsetFragmentImplementation
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairSetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PairSetViewHolder(inflater.inflate(R.layout.pairset_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: PairSetViewHolder, position: Int) {
        holder.bind(pairsetList[position])
    }

    override fun getItemCount() = pairsetList.count()


    fun submitList(newPairsetList: MutableList<Pairset>) {
        val pairsetDiffUtil = PairsetDiffUtil(pairsetList, newPairsetList)
        val diffResult = DiffUtil.calculateDiff(pairsetDiffUtil)
        pairsetList = newPairsetList
        diffResult.dispatchUpdatesTo(this)
    }

    fun filterList(filteredList: MutableList<Pairset>) {
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
        private val rewardDrawable = {drawableResource: Int ->
            ResourcesCompat.getDrawable(
                pairSetFragmentContext.resources,
                drawableResource,
                pairSetFragmentContext.theme
            )
        }


        init {
            itemView.setOnClickListener {
                pairsetFragmentImplementation.putPairsetIdIntoIntent(pairsetList[bindingAdapterPosition].pairsetId)
            }
        }

        fun bind(pairset: Pairset) {
            val variantsRewardDrawableResource =
                if (pairset.variantsTestPassed) R.drawable.icon_test_icon_variants_enabled
                else R.drawable.icon_test_icon_variants_disabled

            val translateRewardDrawableResource =
                if(pairset.translateTestPassed) R.drawable.icon_test_icon_translate_enabled
                else R.drawable.icon_test_icon_translate_disabled

            binding.apply {
                tvItemTitle.text = pairset.name.capitalize(Locale.ROOT).trim()
                val date = simpleDateFormatExact.parse(pairset.date)
                tvItemDetails.text = simpleDateFormatExact.format(date!!)
                tvPairCounterBody.apply {
                    text = pairset.getPairList().count().toString()
                    background = when (pairset.getPairList().count()) {
                        0 -> background(R.drawable.pair_counter_container_has_no_pairs)
                        else -> background(R.drawable.pair_counter_container_pairs)
                    }
                }
                rewardMarkVariants.setImageDrawable(rewardDrawable(variantsRewardDrawableResource))
                rewardMarkTranslate.setImageDrawable(rewardDrawable(translateRewardDrawableResource))
            }
        }
    }


}


