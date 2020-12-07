package ru.arcadudu.danatest_v030.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.VariantsTestRowBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet

class VariantsCardAdapter : RecyclerView.Adapter<VariantsCardAdapter.VariantsCardHolder>() {
    private lateinit var wordSet: WordSet
    var pairs: MutableList<Pair> = mutableListOf()

    fun submitData(set: WordSet) {
        wordSet = set
        pairs = set.getPairList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantsCardHolder {
        return VariantsCardHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.variants_test_row, parent, false)
        )
    }

    override fun onBindViewHolder(holderShuffle: VariantsCardHolder, position: Int) {
        holderShuffle.bind(wordSet)
    }

    override fun getItemCount() = pairs.size

    inner class VariantsCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = VariantsTestRowBinding.bind(itemView)


        fun bind(wordSet: WordSet) {

            val position = bindingAdapterPosition
            if (pairs.size != 0) {

                binding.apply {
                    tvPairSetTitle.text = wordSet.name

                    tvPairSetCount.text = "${position + 1}/${pairs.size}"

                    tvQuestWord.text = pairs[position].key

                    // todo: variant generator

                    btnConfirm.setOnClickListener {
                        pairs.removeAt(position)
                        notifyItemRemoved(position)
                    }

//                    btnConfirm.setOnClickListener {
//                        // todo: checking quest=>answer correctness
//                        pairs.removeAt(position)
//                        notifyItemRemoved(position)
//                        done++
//                        Log.d("progress", "bind: done = $done")
//                        iProgress.setTestProgress((done*multiply))
//                    }
                }

            }
        }
    }
}