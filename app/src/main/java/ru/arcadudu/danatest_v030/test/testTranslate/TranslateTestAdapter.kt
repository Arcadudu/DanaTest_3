package ru.arcadudu.danatest_v030.test.testTranslate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairSelectorRowBinding
import ru.arcadudu.danatest_v030.models.Pair
import java.util.*

class TranslateTestAdapter : RecyclerView.Adapter<TranslateTestAdapter.PairSelectorViewHolder>() {
    private var pairList: MutableList<Pair> = mutableListOf()
    private lateinit var pair: Pair
    private lateinit var translateFragmentImpl : TranslateFragmentView

    fun submitData(list: MutableList<Pair>) {
        pairList = list
    }

    fun translateAdapterCallback(fragmentCallback:TranslateFragmentView) {
        this.translateFragmentImpl = fragmentCallback
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairSelectorViewHolder {
        return PairSelectorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.pair_selector_row, parent, false)
        )
    }

//    override fun onBindViewHolder(holder: MyHolder, position: Int) {
//        // - get element from your dataset at this position
//        val item = myDataset.get(holder.absoluteAdapterPosition)
//    }

    override fun onBindViewHolder(holder: PairSelectorViewHolder, position: Int) {
        pair = pairList[position]
        holder.bind(pair)

    }

    override fun getItemCount() = pairList.count()




    inner class PairSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = PairSelectorRowBinding.bind(itemView)
        private val questWordItem = binding.questPairValue

        fun bind(pair: Pair) {
            questWordItem.text = pair.pairValue.capitalize(Locale.ROOT ).trim()
            
        }


    }
}