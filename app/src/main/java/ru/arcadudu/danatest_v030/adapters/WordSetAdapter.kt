package ru.arcadudu.danatest_v030.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.WordSet

class WordSetAdapter(var itemList:MutableList<WordSet> ) :
    RecyclerView.Adapter<WordSetAdapter.MyViewHolder>() {


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

    fun filterList(list:MutableList<WordSet>){
        itemList = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.tv_item_title)
        private var details: TextView = itemView.findViewById(R.id.tv_item_details)
        private var length: TextView = itemView.findViewById(R.id.tv_item_length_val)

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                val context = itemView.context
                Toast.makeText(context, "you clicked ${itemList[position].name}", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(wordSet: WordSet) {
            title.text = wordSet.name
            details.text = wordSet.description
            length.text = wordSet.listLength.toString()
        }

    }
}