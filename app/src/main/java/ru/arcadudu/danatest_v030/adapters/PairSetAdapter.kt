package ru.arcadudu.danatest_v030.adapters

import android.content.Context
import android.util.Log
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
import ru.arcadudu.danatest_v030.utils.*
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


    inner class PairSetViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = PairsetRowLayoutBinding.bind(view)
        private val simpleDateFormatExact = SimpleDateFormat("dd MMMM", Locale.getDefault())

        private val containerBackground = { colorResource: Int ->
            ResourcesCompat.getColor(
                pairSetFragmentContext.resources,
                colorResource,
                pairSetFragmentContext.theme
            )
        }
        private val rewardDrawable = { drawableResource: Int ->
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
                if (pairset.translateTestPassed) R.drawable.icon_test_icon_translate_enabled
                else R.drawable.icon_test_icon_translate_disabled

            Log.d("color", "bind: pairsetcolor = ${pairset.pairsetColor}")

            val containerColor = when (pairset.pairsetColor) {
                PAIRSET_COLOR_BLUE -> R.color.dt3_pairset_color_blue
                PAIRSET_COLOR_GREEN -> R.color.dt3_pairset_color_green
                PAIRSET_COLOR_RED -> R.color.dt3_pairset_color_red
                PAIRSET_COLOR_GREY -> R.color.dt3_pairset_color_grey
                PAIRSET_COLOR_VIOLET -> R.color.dt3_pairset_color_violet
                else -> R.color.dt3_pairset_color_default
            }

            binding.apply {
                tvItemTitle.text = pairset.name.capitalize(Locale.ROOT).trim()

                val date = simpleDateFormatExact.parse(pairset.date)
                tvItemDetails.text = simpleDateFormatExact.format(date!!)

                tvPairCounterBody.text = pairset.getPairList().count().toString()

                rewardMarkVariants.setImageDrawable(rewardDrawable(variantsRewardDrawableResource))
                rewardMarkTranslate.setImageDrawable(rewardDrawable(translateRewardDrawableResource))

                pairsetRowContainer.setBackgroundColor(containerBackground(containerColor))
            }
        }
    }


}


