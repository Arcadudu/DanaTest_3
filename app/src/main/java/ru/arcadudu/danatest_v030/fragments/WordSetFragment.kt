package ru.arcadudu.danatest_v030.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.WsEditorActivity
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.databinding.FragmentWordSetBinding
import ru.arcadudu.danatest_v030.interfaces.ClickableItem
import ru.arcadudu.danatest_v030.models.WordSet
import java.util.*

private lateinit var myAdapter: WordSetAdapter
private lateinit var itemList: MutableList<WordSet>
private lateinit var favWordSet: WordSet
private lateinit var recyclerView: RecyclerView
private lateinit var etSearch: EditText
private lateinit var btnAddWordSet: ImageView


//view binding
private lateinit var wordSetBinding: FragmentWordSetBinding

class WordSetFragment : Fragment(), ClickableItem {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWordSetBinding.bind(view)
        wordSetBinding = binding
        ///////////////////////////////////////////////

        packList()

        recyclerView = wordSetBinding.wordSetRecycler
        recyclerView
            .apply {
                myAdapter = WordSetAdapter(this@WordSetFragment)
                adapter = myAdapter
                myAdapter.submitList(itemList)
                layoutManager = LinearLayoutManager(activity)
            }

        initSwiper(recyclerView)

        etSearch = wordSetBinding.etWsFragSearchfield
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })

        var added = 0
        btnAddWordSet = wordSetBinding.ivWSFragAddIcon
        btnAddWordSet.setOnClickListener {
//            todo: open modal window for adding new wordSet
            added++
//            Toast.makeText(activity, "you added new wordSet", Toast.LENGTH_SHORT).show()
            itemList.add(
                1,
                WordSet(
                    name = "new Item $added",
                    description = "This is an item tt you added pushing plus button"
                )
            )
            myAdapter.notifyItemInserted(1)
            val layoutManager = recyclerView.layoutManager
            layoutManager?.scrollToPosition(0)
        }


    }


    private fun initSwiper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder?.adapterPosition == 0) return 0
                return super.getMovementFlags(recyclerView, viewHolder)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                return if (fromPosition != 0 && toPosition != 0) {
                    Collections.swap(itemList, fromPosition, toPosition)
                    myAdapter.notifyItemMoved(fromPosition, toPosition)
                    val layoutManager = recyclerView.layoutManager
                    layoutManager?.scrollToPosition(toPosition)
                    true
                } else {
                    false
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val chosenItem: WordSet = itemList[position]
                val chosenItemName = chosenItem.name

                when (direction) {
                    ItemTouchHelper.LEFT -> {
//                        val builder = AlertDialog.Builder(activity)
//                        builder.apply {
//                            setTitle("Удалить набор ${chosenItem.name}?")
//                            setPositiveButton("Ок", DialogInterface.OnClickListener{dialog, which ->
//                                itemList.remove(chosenItem)
//                                myAdapter.notifyItemRemoved(position)
//                            })
//                            setNegativeButton("Отмена",DialogInterface.OnClickListener{dialog, which ->
//                                dialog.dismiss()
//                            })
//                            show()
//                        }
                        itemList.remove(chosenItem)
                        myAdapter.notifyItemRemoved(position)
                        Snackbar.make(
                            recyclerView,
                            "$chosenItemName удалено",
                            5000
                        ).setBackgroundTint(resources.getColor(R.color.plt_active_blue_light))
                            .setAnchorView(R.id.my_navigation_bar)
                            .setAction("Отмена", View.OnClickListener {
                                itemList.add(position, chosenItem)
                                myAdapter.notifyItemInserted(position)
                            })
                            .show()

                    }

                }

            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun filter(text: String) {
        val filteredList: MutableList<WordSet> = mutableListOf()
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
        favWordSet = WordSet(
            isFavorites = true,
            name = "Избранный набор",
            description = "Сюда попадают избранные Вами пары из других наборов"
        )

        itemList = mutableListOf()
        itemList.add(0, favWordSet)
        for (i in 1..20) itemList.add(WordSet(name = "WordSet $i", description = string))
    }

    override fun clickToEditor(wordSet: WordSet) {
        //todo: startActivityForResult -> into editor and back
        val intent = Intent(activity, WsEditorActivity::class.java)
        intent.putExtra("selected_wordset", wordSet)
        startActivity(intent)
    }
}