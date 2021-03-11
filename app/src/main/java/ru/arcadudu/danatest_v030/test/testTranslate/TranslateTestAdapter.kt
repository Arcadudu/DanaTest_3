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
    private lateinit var translateFragmentImpl: TranslateFragmentView

    fun submitData(list: MutableList<Pair>) {
        pairList = list
    }

    fun translateAdapterCallback(fragmentCallback: TranslateFragmentView) {
        this.translateFragmentImpl = fragmentCallback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairSelectorViewHolder {
        return PairSelectorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.value_translate_row, parent, false)
        )
    }


    override fun onBindViewHolder(holder: PairSelectorViewHolder, position: Int) {
        val pair = pairList[position]
        holder.bindNoCounter(pair)

    }

    //private void updateBackground(ViewHolder holder, int position) {
    //    holder.itemView.setBackgroundResource(
    //        (position == selectedPosition) ? R.drawable.highlight : R.drawable.stroke);
    //}
    //
    //@Override
    //public void onBindViewHolder(ViewHolder holder, int position) {
    //    updateBackground(holder, position);
    //    // other view binding stuff
    //}

//    fun onBindViewHolder(holder: ViewHolder?, position: Int, payloads: List<Any?>?) {
//        if (payloads != null && payloads.contains("BACKGROUND")) {
//            updateBackground(holder, position)
//        }
//    }


    override fun getItemCount() = pairList.count()


    inner class PairSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ValueTranslateRowBinding.bind(itemView)
        private val questWordItem = binding.questPairValue
        private val scrollCounter = binding.tvOnScrollCounter

        init {
            itemView.setOnLongClickListener {
                translateFragmentImpl.onAdapterLongClick()
                true
            }
        }

        fun bindNoCounter(pair: Pair) {
            questWordItem.text = pair.pairValue.capitalize(Locale.ROOT).trim()

        }


    }
}