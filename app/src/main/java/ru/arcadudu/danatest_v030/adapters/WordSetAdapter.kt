package ru.arcadudu.danatest_v030.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.interfaces.ClickableItem
import ru.arcadudu.danatest_v030.models.WordSet

class WordSetAdapter(var clickableItem: ClickableItem) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList: MutableList<WordSet> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordSetViewHolder {


        val layout: Int =
            when (viewType) {
                // Favorite
                0 -> R.layout.wordset_row_layout_fav
                // Regular
                else -> R.layout.wordset_row_layout
            }
        return WordSetViewHolder(
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position].isFavorites) 0 else 1 // 0 = favorite, 1 = regular
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0){
            val favViewHolder : FavWordSetViewHolder = holder as FavWordSetViewHolder
            favViewHolder.bind(itemList[position])
        }else{
            val viewHolder : WordSetViewHolder = holder as WordSetViewHolder
            viewHolder.bind(itemList[position])
        }
//        val item: WordSet = itemList[position]
//        if(item.isFavorites){
//            val favViewHolder : FavWordSetViewHolder = holder as FavWordSetViewHolder
//            favViewHolder.bind(itemList[position])
//        }else{
//            val viewHolder : WordSetViewHolder = holder as WordSetViewHolder
//            viewHolder.bind(itemList[position])
//        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun submitList(list: MutableList<WordSet>) {
        itemList = list
        notifyDataSetChanged()
    }

    fun filterList(list: MutableList<WordSet>) {
        itemList = list
        notifyDataSetChanged()
    }

    //REGULAR
    inner class WordSetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var title: TextView = itemView.findViewById(R.id.tv_item_title)
        private var details: TextView = itemView.findViewById(R.id.tv_item_details)
        private var length: TextView = itemView.findViewById(R.id.tv_item_length)

        init {
            itemView.setOnClickListener {
                Log.d("AAA", "adapter on click listener: adapter pos = $adapterPosition ")
                goToEditor(adapterPosition)

            }
        }

        fun bind(wordSet: WordSet) {
            title.text = wordSet.name
            details.text = wordSet.description
            length.text = wordSet.listLength.toString()
        }

        private fun goToEditor(position: Int) {
            val wordSet: WordSet = itemList[position]
            Log.d("AAA", "adapter goToEditor: ${wordSet == null} ")
            clickableItem.clickToEditor(wordSet)
        }
    }

    // FAVOURITE
    inner class FavWordSetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var favTitle: TextView = itemView.findViewById(R.id.tv_fav_title)
        private var favDetails: TextView = itemView.findViewById(R.id.tv_fav_details)
        private var favLength: TextView = itemView.findViewById(R.id.tv_fav_length)

        init {
            itemView.setOnClickListener {
               goToEditor(0)
            }
        }

        fun bind(wordSet: WordSet) {
            favTitle.text = wordSet.name
            favDetails.text = wordSet.description
            favLength.text = wordSet.listLength.toString()
        }

        private fun goToEditor(position: Int) {
            val wordSet: WordSet = itemList[position]
            Log.d("AAA", "adapter goToEditor: ${wordSet == null} ")
            clickableItem.clickToEditor(wordSet)
        }



    }


}