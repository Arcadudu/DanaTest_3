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

class WordSetAdapter(clickableItem: ClickableItem) :
    RecyclerView.Adapter<WordSetAdapter.MyViewHolder>() {
    var itemList: MutableList<WordSet> = mutableListOf()
    var clickableItem: ClickableItem = clickableItem


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.wordset_row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(itemList[position])
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


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.tv_item_title)
        private var details: TextView = itemView.findViewById(R.id.tv_item_details)
        private var length: TextView = itemView.findViewById(R.id.tv_item_length_val)

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


}