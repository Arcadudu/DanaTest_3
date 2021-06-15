package ru.arcadudu.danatest_v030.test

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairRowMistakesLayoutBinding
import ru.arcadudu.danatest_v030.models.Pair
import java.util.*

class MistakeListAdapter : RecyclerView.Adapter<MistakeListAdapter.MistakeListRowViewHolder>() {
    private lateinit var context: Context
    private var wrongAnswerList = mutableListOf<String>()
    private var mistakeAndPairList: MutableList<Pair> = mutableListOf()

    fun captureContext(capturedContext: Context) {
        this.context = capturedContext
    }

    fun submitMistakenPairListAndWrongAnswerList(
        mutableList: MutableList<Pair>,
        answerList: MutableList<String>
    ) {
        mistakeAndPairList.clear()
        wrongAnswerList.clear()
        mistakeAndPairList = mutableList
        wrongAnswerList = answerList
        Log.d(
            "check",
            "submitMistakenPairMap: mistakeAndPairHashMapCount = ${mistakeAndPairList.count()}"
        )
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MistakeListRowViewHolder {
        return MistakeListRowViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pair_row_mistakes_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MistakeListRowViewHolder, position: Int) {
        val wrongAnswer = wrongAnswerList[position]
        val pair = mistakeAndPairList[position]
//        pair?.let { holder.bindMistakeRow(wrongAnswer, it) }
        holder.bindMistakeRow(position, wrongAnswer, pair)
    }

    override fun getItemCount(): Int = wrongAnswerList.count()


    inner class MistakeListRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = PairRowMistakesLayoutBinding.bind(itemView)
        private val tvQuestWordTitleLine = binding.tvQuestWordTitleLine
        private val tvCorrectKeyLine = binding.tvCorrectKey
        private val tvMistakenKeyLine = binding.tvMistakenKey

        fun bindMistakeRow(mistakeNumber: Int, wrongAnswer: String, mistakenPair: Pair) {
//            tvQuestWordTitleLine.text = "${mistakeNumber + 1}. ${mistakenPair.pairValue}"
            tvQuestWordTitleLine.text = context.getString(
                R.string.dt_on_test_result_dialog_tv_quest_word_title_line,
                (mistakeNumber + 1),
                mistakenPair.pairValue
            )
            tvCorrectKeyLine.text = mistakenPair.pairKey.toLowerCase(Locale.ROOT)
            Log.d("checkTr", "bindMistakeRow: correctKey = ${mistakenPair.pairKey} ")
            tvMistakenKeyLine.text = wrongAnswer
            Log.d("checkTr", "bindMistakeRow: mistake = $wrongAnswer ")
        }

    }


}