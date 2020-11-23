package ru.arcadudu.danatest_v030.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.WsEditorActivity
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.models.WordSet
import java.util.*

private lateinit var recyclerView: RecyclerView
private lateinit var myAdapter: WordSetAdapter

private lateinit var et_searchBar: EditText

private lateinit var itemList: MutableList<WordSet>

class WordSetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageView = view.findViewById(R.id.iv_WS_frag_back_arrow_icon)
        btnBack.setOnClickListener {
            // todo: onBackPressed override
        }

        packList()

        recyclerView = view.findViewById(R.id.wordSet_recycler)
        recyclerView.apply {
            myAdapter = WordSetAdapter()
            adapter = myAdapter
            myAdapter.submitList(itemList)
            layoutManager = LinearLayoutManager(activity)

        }
        initSwiper(recyclerView)

        et_searchBar = view.findViewById(R.id.et_ws_frag_searchfield)
        et_searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })

        val btnAddWordSet: ImageView = view.findViewById(R.id.iv_WS_frag_add_icon)
        var added = 0
        btnAddWordSet.setOnClickListener {
//            todo: open modal window for adding new wordSet
            added++
//            Toast.makeText(activity, "you added new wordSet", Toast.LENGTH_SHORT).show()
            itemList.add(
                0,
                WordSet(
                    name = "new Item $added",
                    description = "This is an item tt you added pushing plus button"
                )
            )
            myAdapter.notifyItemInserted(0)
            val layoutManager = recyclerView.layoutManager
            layoutManager?.scrollToPosition(0)
        }


    }

    private fun initSwiper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(itemList, fromPosition, toPosition)
                myAdapter.notifyItemMoved(fromPosition, toPosition)
                 return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val chosenItem: WordSet = itemList[position]
                val chosenItemName = chosenItem.name

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        itemList.remove(chosenItem)
                        myAdapter.notifyItemRemoved(position)
                        Snackbar.make(
                            recyclerView,
                            "$chosenItemName удалено",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Отмена", View.OnClickListener {
                                itemList.add(position, chosenItem)
                                myAdapter.notifyItemInserted(position)
                            }).show()

                    }
                    ItemTouchHelper.RIGHT -> {
                        myAdapter.notifyDataSetChanged()
                        val intent = Intent(activity, WsEditorActivity::class.java)
                        intent.putExtra("chosen_item", chosenItem)
                        startActivity(intent)

                    }

                }
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    private fun filter(text: String) {
        var filteredList: MutableList<WordSet> = mutableListOf()
        for (item in itemList) {
            if (item.name.toLowerCase()
                    .contains(text.toLowerCase()) || item.description.toLowerCase()
                    .contains(text.toLowerCase())
            ) {
                filteredList.add(item)
            }
        }
        myAdapter.filterList(filteredList)
    }

    private fun packList() {
        val string = getString(R.string.dummy_text)
        itemList = mutableListOf()
        for (i in 1..20) itemList.add(WordSet(name = "WordSet $i", description = string))
    }
}