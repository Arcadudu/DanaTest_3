package ru.arcadudu.danatest_v030.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.PairsetRowLayoutBinding
import ru.arcadudu.danatest_v030.databinding.PairsetRowLayoutFavBinding
import ru.arcadudu.danatest_v030.interfaces.TransferToEditor
import ru.arcadudu.danatest_v030.models.PairSet

class WordSetAdapter(var transferToEditor: TransferToEditor) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList: MutableList<PairSet> = mutableListOf()

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
                    R.layout.pairset_row_layout_fav,
                    parent,
                    false
                )
            )
            else -> WordSetViewHolder(inflater.inflate(R.layout.pairset_row_layout, parent, false))
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


    fun submitList(list: MutableList<PairSet>) {
        itemList = list
        notifyDataSetChanged()
    }

    fun filterList(list: MutableList<PairSet>) {
        itemList = list
        notifyDataSetChanged()
    }

    //REGULAR
    inner class WordSetViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = PairsetRowLayoutBinding.bind(view)

        init {
            itemView.setOnClickListener {
                Log.d("AAA", "adapter on click listener: adapter pos = $bindingAdapterPosition ")
                goToEditor(bindingAdapterPosition)

            }
        }

        fun bind(pairSet: PairSet) {
            binding.apply {
                tvItemTitle.text = pairSet.name
                tvItemDetails.text = pairSet.description
//                tvItemLength.text = wordSet.listLength.toString()
            }
        }

        private fun goToEditor(position: Int) {
            val pairSet: PairSet = itemList[position]
            Log.d("AAA", "adapter goToEditor: ${pairSet == null} ")
            transferToEditor.clickToEditor(pairSet)
        }
    }

    // FAVOURITE
    inner class FavWordSetViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = PairsetRowLayoutFavBinding.bind(view)

        init {
            itemView.setOnClickListener {
                goToEditor(0)
            }
        }

        fun bind(favPairSet: PairSet) {
            binding.apply {
                tvFavTitle.text = favPairSet.name
                tvFavDetails.text = favPairSet.description
//                tvFavLength.text = favWordSet.listLength.toString()
            }
        }

        private fun goToEditor(position: Int) {
            val pairSet: PairSet = itemList[position]
            Log.d("AAA", "adapter goToEditor: ${pairSet == null} ")
            transferToEditor.clickToEditor(pairSet)
        }


    }


}