package ru.arcadudu.danatest_v030.test.testTranslate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.ValueTranslateRowBinding
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
            LayoutInflater.from(parent.context).inflate(R.layout.value_translate_row, parent, false)
        )
    }



    override fun onBindViewHolder(holder: PairSelectorViewHolder, position: Int) {
        pair = pairList[position]
        holder.bind(pair)

    }

    override fun getItemCount() = pairList.count()




    inner class PairSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ValueTranslateRowBinding.bind(itemView)
        private val questWordItem = binding.questPairValue
//        private val scrollCounter = binding.tvOnScrollCounter

        init {
            itemView.setOnLongClickListener{
//                scrollCounter.visibility = View.VISIBLE
                translateFragmentImpl.detachSnapHelperFromRecyclerView()
                true
            }
        }

        fun bind(pair: Pair) {
            questWordItem.text = pair.pairValue.capitalize(Locale.ROOT ).trim()
//            scrollCounter.text = bindingAdapterPosition.toString()
        }




    }
}