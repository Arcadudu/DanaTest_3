package ru.arcadudu.danatest_v030.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import java.util.*

private lateinit var binding: ActivityWsEditorBinding
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var search: EditText
private lateinit var recyclerView: RecyclerView
private lateinit var myAdapter: PairRowAdapter


private lateinit var currentWordSet: WordSet
private lateinit var currentPairList: MutableList<Pair>

class WsEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWsEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        currentWordSet = intent.getSerializableExtra("selected_wordset") as WordSet
        currentPairList = currentWordSet.getPairList()


/*        checkForFav(incomingWordSet)*/

        toolbar = binding.toolbar
        toolbar.apply {
            setSupportActionBar(this)
            title = currentWordSet.name
            subtitle = currentWordSet.description
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            navigationIcon = resources.getDrawable(R.drawable.icon_arrow_back_blue)
            setNavigationOnClickListener {
                //todo: save wordSet state
                onBackPressed()
            }

        }

        recyclerView = binding.pairsRecycler.apply {
            myAdapter = PairRowAdapter()
            myAdapter.submitPairs(currentPairList)
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this@WsEditorActivity)

            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(
                resources.getDrawable(
                    R.drawable.divider_drawable, theme
                )
            )
            addItemDecoration(divider)
        }


        initSwiper(recyclerView)

        search = binding.etEditorSearchField.apply {
            hint = "Поиск слова"
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    filter(s.toString())
                }

            })
        }


/*
*  etSearch = binding.etWsFragSearchfield
        etSearch.hint = "Поиск набора"
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })*/


        /*val itemList = intent.getSerializableExtra("key") as ArrayList<WordSet>
        for (item in itemList) {
            Log.d("itemlist", "onCreate: ${item.name}")
        }*/


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
                return super.getMovementFlags(recyclerView, viewHolder)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition

                Collections.swap(currentPairList, fromPosition, toPosition)
                myAdapter.notifyItemMoved(fromPosition, toPosition)
                val layoutManager = recyclerView.layoutManager
                layoutManager?.scrollToPosition(toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun filter(text: String) {
        val filteredList: MutableList<Pair> = mutableListOf()
        for (item in currentPairList) {
            if (item.key.toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT)) || item.value.toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        myAdapter.filterList(filteredList)
    }


    // toolbar menu setup
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.ws_editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_edit) {
            Toast.makeText(this, "редактировать название и детали", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}

//private fun checkForFav(incomingWordSet: WordSet) {
//    if (incomingWordSet.isFavorites) {
//        btnEdit.apply {
//            isEnabled = false
//            setImageDrawable(
//                resources.getDrawable(
//                    R.drawable.icon_star_favorite_blue_outlined
//                )
//            )
//            isVisible = true
//        }
//        tvTitle.setOnClickListener {
//            Snackbar.make(
//                recyclerView,
//                "Невозможно изменить название избранного набора",
//                Snackbar.LENGTH_LONG
//            ).show()
//        }
//        tvDetails.setOnClickListener {
//            Snackbar.make(
//                recyclerView,
//                "Невозможно изменить описание избранного набора",
//                Snackbar.LENGTH_LONG
//            ).show()
//        }
//    }
//}