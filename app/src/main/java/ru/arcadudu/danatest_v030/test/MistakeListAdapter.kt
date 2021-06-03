package ru.arcadudu.danatest_v030.test

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairRowMistakesLayoutBinding
import ru.arcadudu.danatest_v030.models.Pair

class MistakeListAdapter : RecyclerView.Adapter<MistakeListAdapter.MistakeListRowViewHolder>() {
    private var wrongAnswerList = mutableListOf<String>()
    private var mistakeAndPairHashMap: MutableMap<String, Pair> = mutableMapOf()

    fun submitMistakenPairMap(mutableMap: MutableMap<String, Pair>){
        mistakeAndPairHashMap.clear()
        mistakeAndPairHashMap = mutableMap
        Log.d("check", "submitMistakenPairMap: mistakeAndPairHashMapCount = ${mistakeAndPairHashMap.count()}")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MistakeListRowViewHolder {
        return MistakeListRowViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pair_row_mistakes_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MistakeListRowViewHolder, position: Int) {
        val wrongAnswer = wrongAnswerList[position]
        val pair = mistakeAndPairHashMap[wrongAnswer]
//        pair?.let { holder.bindMistakeRow(wrongAnswer, it) }
        holder.bindMistakeRow(wrongAnswer, pair!!)
    }

    override fun getItemCount(): Int = wrongAnswerList.count()


    inner class MistakeListRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = PairRowMistakesLayoutBinding.bind(itemView)
        private val tvQuestWordTitleLine = binding.tvQuestWordTitleLine
        private val tvCorrectKeyLine = binding.tvCorrectKey
        private val tvMistakenKeyLine = binding.tvMistakenKey

        fun bindMistakeRow(wrongAnswer: String, mistakenPair: Pair) {
            tvQuestWordTitleLine.text = mistakenPair.pairValue
            tvCorrectKeyLine.text = mistakenPair.pairKey
            tvMistakenKeyLine.text = wrongAnswer
        }

    }


}