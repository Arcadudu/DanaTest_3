package ru.arcadudu.danatest_v030.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import moxy.MvpView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.QuestCardBinding
import ru.arcadudu.danatest_v030.interfaces.TestAdapterCallback
import ru.arcadudu.danatest_v030.models.Pair
import java.util.*


class TranslateTestAdapter : RecyclerView.Adapter<TranslateTestAdapter.PairSelectorViewHolder>() {
    private var pairList: MutableList<Pair> = mutableListOf()
    private lateinit var translateFragmentImpl: TestAdapterCallback
    private var pairsetName = ""


    fun submitData(list: MutableList<Pair>) {
        pairList = list
    }

    fun adapterCallback(fragmentCallback: MvpView) {
        this.translateFragmentImpl = fragmentCallback as TestAdapterCallback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairSelectorViewHolder {
        return PairSelectorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.quest_card, parent, false)
        )
    }


    override fun onBindViewHolder(holder: PairSelectorViewHolder, position: Int) {
        val pair = pairList[position]
        holder.bindNoCounter(pair)
    }


    override fun getItemCount() = pairList.count()


    inner class PairSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = QuestCardBinding.bind(itemView)
        private val questWordItem = binding.questPairValue


        init {
            itemView.setOnClickListener {
                translateFragmentImpl.onAdapterItemClick()
            }
        }

        fun bindNoCounter(pair: Pair) {
            questWordItem.text = pair.pairValue.capitalize(Locale.ROOT).trim()


        }


    }
}