package ru.arcadudu.danatest_v030.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.models.WordSet

private lateinit var binding: ActivityWsEditorBinding
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var btnEdit: ImageView
private lateinit var tvTitle: TextView
private lateinit var tvDetails: TextView
private lateinit var recyclerView: RecyclerView
private lateinit var myAdapter: PairRowAdapter

class WsEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWsEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


//        tvTitle = binding.tvEditorTitle
//        tvDetails = binding.editorDetails
//        btnEdit = binding.ivEditorEditDetailsIcon
        // todo: optional color change on title and detail editing

        val incomingWordSet: WordSet = intent.getSerializableExtra("selected_wordset") as WordSet
//        checkForFav(incomingWordSet)

        toolbar = binding.toolbar
        toolbar.apply {
            setSupportActionBar(this)
            title = incomingWordSet.name
            subtitle = incomingWordSet.description
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            navigationIcon = resources.getDrawable(R.drawable.icon_arrow_back_blue)
            setNavigationOnClickListener {
                //todo: save wordSet state
                onBackPressed()
            }

        }

        recyclerView = binding.pairsRecycler
        recyclerView.apply {
            myAdapter = PairRowAdapter()
            myAdapter.submitPairs(incomingWordSet.getPairList())
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this@WsEditorActivity)
        }

        binding.etEditorSearchField.hint = "Поиск слова"

//        mActionBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back))

//        tvTitle.text = incomingWordSet.name
//        tvDetails.text = incomingWordSet.description

        var itemList = intent.getSerializableExtra("key") as ArrayList<WordSet>
        for (item in itemList) {
            Log.d("itemlist", "onCreate: ${item.name}")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.ws_editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.toolbar_edit){
            Toast.makeText(this, "редактировать название и детали", Toast.LENGTH_SHORT).show()
        }
        return true

    }
}

private fun checkForFav(incomingWordSet: WordSet) {
    if (incomingWordSet.isFavorites) {
        btnEdit.apply {
            isEnabled = false
            setImageDrawable(
                resources.getDrawable(
                    R.drawable.icon_star_favorite_blue_outlined
                )
            )
            isVisible = true
        }
        tvTitle.setOnClickListener {
            Snackbar.make(
                recyclerView,
                "Невозможно изменить название избранного набора",
                Snackbar.LENGTH_LONG
            ).show()
        }
        tvDetails.setOnClickListener {
            Snackbar.make(
                recyclerView,
                "Невозможно изменить описание избранного набора",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}