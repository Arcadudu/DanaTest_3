package ru.arcadudu.danatest_v030.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.WordsetRowLayoutBinding
import ru.arcadudu.danatest_v030.databinding.WordsetRowLayoutFavBinding
import ru.arcadudu.danatest_v030.interfaces.TransferToEditor
import ru.arcadudu.danatest_v030.models.WordSet

class WordSetAdapter(var transferToEditor: TransferToEditor) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList: MutableList<WordSet> = mutableListOf()
//    private lateinit var swipeListener : OnItemSwipedListener

    interface OnItemSwipedListener{
        fun showRemoveAlertDialog(position:Int)
    }

    fun removeItem(position: Int){
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> FavWordSetViewHolder(
                inflater.inflate(
                    R.layout.wordset_row_layout_fav,
                    parent,
                    false
                )
            )
            else -> WordSetViewHolder(inflater.inflate(R.layout.wordset_row_layout, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position].isFavorites) 0 else 1 // 0 = favorite, 1 = regular
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavWordSetViewHolder -> holder.bind(itemList[position])
            is WordSetViewHolder -> holder.bind(itemList[position])
        }
    }

    override fun getItemCount() = itemList.count()


    fun submitList(list: MutableList<WordSet>) {
        itemList = list
        notifyDataSetChanged()
    }

    fun filterList(list: MutableList<WordSet>) {
        itemList = list
        notifyDataSetChanged()
    }

    //REGULAR
    inner class WordSetViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = WordsetRowLayoutBinding.bind(view)

        init {
            itemView.setOnClickListener {
                Log.d("AAA", "adapter on click listener: adapter pos = $adapterPosition ")
                goToEditor(adapterPosition)

            }
        }

        fun bind(wordSet: WordSet) {
            binding.apply {
                tvItemTitle.text = wordSet.name
                tvItemDetails.text = wordSet.description
//                tvItemLength.text = wordSet.listLength.toString()
            }
        }

        private fun goToEditor(position: Int) {
            val wordSet: WordSet = itemList[position]
            Log.d("AAA", "adapter goToEditor: ${wordSet == null} ")
            transferToEditor.clickToEditor(wordSet)
        }
    }

    // FAVOURITE
    inner class FavWordSetViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = WordsetRowLayoutFavBinding.bind(view)

        init {
            itemView.setOnClickListener {
                goToEditor(0)
            }
        }

        fun bind(favWordSet: WordSet) {
            binding.apply {
                tvFavTitle.text = favWordSet.name
                tvFavDetails.text = favWordSet.description
//                tvFavLength.text = favWordSet.listLength.toString()
            }
        }

        private fun goToEditor(position: Int) {
            val wordSet: WordSet = itemList[position]
            Log.d("AAA", "adapter goToEditor: ${wordSet == null} ")
            transferToEditor.clickToEditor(wordSet)
        }


    }


}