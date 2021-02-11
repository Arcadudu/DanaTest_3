package ru.arcadudu.danatest_v030.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.tests.TranslateActivity
import ru.arcadudu.danatest_v030.databinding.TestTranslateRowBinding
import ru.arcadudu.danatest_v030.interfaces.IProgress
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet

class TranslateCardAdapter : RecyclerView.Adapter<TranslateCardAdapter.TranslateCardViewHolder>() {
    private lateinit var pairSet: PairSet
    private lateinit var iProgress: IProgress
    var pairs: MutableList<Pair> = mutableListOf()

    var done = 0
    val multiply = 1000

    fun submitData(set: PairSet) {
        pairSet = set
        pairs = set.getPairList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslateCardViewHolder {
        return TranslateCardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.test_translate_row, parent, false)
        )
    }

    override fun onBindViewHolder(holderTranslate: TranslateCardViewHolder, position: Int) {
        holderTranslate.bind(pairSet)
    }

    override fun getItemCount() = pairs.count()

    inner class TranslateCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TestTranslateRowBinding.bind(itemView)


        fun bind(pairSet: PairSet) {

            val position = bindingAdapterPosition
            iProgress = TranslateActivity()
            //
            binding.apply {

                if (pairs.size != 0) {

                    tvPairSetTitle.text = pairSet.name
                    tvPairSetCount.text = "${position + 1}/${pairs.size}"
                    tvQuestWord.text = pairs[position].pairKey


                    btnAddToFav.setOnClickListener {

                    }



                    btnConfirm.setOnClickListener {
                        // todo: checking quest=>answer correctness
                        pairs.removeAt(position)
                        notifyItemRemoved(position)
                        done++
                        Log.d("progress", "bind: done = $done")
                        iProgress.setTestProgress((done*multiply))
                    }
                }
                // todo -> checking quest+answer via interface
            }
        }


    }
}