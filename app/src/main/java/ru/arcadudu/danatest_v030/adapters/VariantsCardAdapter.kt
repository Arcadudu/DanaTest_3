package ru.arcadudu.danatest_v030.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.tests.VariantsActivity
import ru.arcadudu.danatest_v030.databinding.VariantsTestRowBinding
import ru.arcadudu.danatest_v030.interfaces.ICheckWord
import ru.arcadudu.danatest_v030.interfaces.IProgress
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet

class VariantsCardAdapter : RecyclerView.Adapter<VariantsCardAdapter.VariantsCardHolder>() {
    private lateinit var wordSet: WordSet
    private lateinit var iProgress: IProgress
    private lateinit var iCheckWord: ICheckWord

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

    override fun getItemCount() = pairs.count()

    inner class VariantsCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = VariantsTestRowBinding.bind(itemView)


        fun bind(wordSet: WordSet) {

            val position = bindingAdapterPosition
            if (pairs.size != 0) {

                binding.apply {
                    tvPairSetTitle.text = wordSet.name
                    tvPairSetCount.text = "${position + 1}/${pairs.size}"
                    tvQuestWord.text = pairs[position].key

                    radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                        iCheckWord = VariantsActivity()
                        var word: String = ""
                        when (checkedId) {
                            R.id.radio_var1 -> {
                                word = radioVar1.text.toString()
                                Log.d("radio", "bind: you pressed radio_var1")
                                Log.d("radio", "bind: word == $word")
                                iCheckWord.checkWord(word)
                            }
                            R.id.radio_var2 -> {
                                word = radioVar2.text.toString()
                                Log.d("radio", "bind: you pressed radio_var2")
                                Log.d("radio", "bind: word == $word")
                                iCheckWord.checkWord(word)
                            }
                            R.id.radio_var3 -> {
                                word = radioVar3.text.toString()
                                Log.d("radio", "bind: you pressed radio_var3")
                                Log.d("radio", "bind: word == $word")
                                iCheckWord.checkWord(word)
                            }
                        }
                    })

                    // todo: variant generator

                    btnConfirm.setOnClickListener {
                        pairs.removeAt(position)
                        notifyItemRemoved(position)
                    }


//                        // todo: checking quest=>answer correctness

                }

            }
        }
    }
}