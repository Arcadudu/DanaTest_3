package ru.arcadudu.danatest_v030.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import java.util.*

private lateinit var binding: ActivityWsEditorBinding
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var search: EditText
private lateinit var recyclerView: RecyclerView
private lateinit var myAdapter: PairRowAdapter
private const val TO_EDITOR_SELECTED_WORD_SET = "selectedWordSet"

private lateinit var dialogRemovePairBinding: DialogRemoveItemBinding


private lateinit var searchCloseBtn: ImageView
private lateinit var addPairBtn: ImageView

private lateinit var currentWordSet: WordSet
private lateinit var currentPairList: MutableList<Pair>

class WsEditorActivity : AppCompatActivity(), WordSetAdapter.OnItemSwipedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWsEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        currentWordSet = intent.getSerializableExtra(TO_EDITOR_SELECTED_WORD_SET) as WordSet
        currentPairList = currentWordSet.getPairList()

/*        checkForFav(incomingWordSet)*/


        toolbar = binding.toolbar
        toolbar.apply {
            setSupportActionBar(this)
            title = currentWordSet.name
            subtitle = currentWordSet.description
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            navigationIcon = resources.getDrawable(R.drawable.icon_arrow_back_blue, theme)
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
                    if (s.toString().isNotEmpty()) {
                        searchCloseBtn.apply {
                            visibility = View.VISIBLE
                            isEnabled = true
                        }
                    } else {
                        searchCloseBtn.apply {
                            visibility = View.GONE
                            isEnabled = false
                        }
                    }
                    filter(s.toString())
                }

            })
        }

        searchCloseBtn = binding.btnSearchClose.apply {
            visibility = View.GONE
            setOnClickListener {
                if (searchCloseBtn.visibility == View.VISIBLE) {
                    search.setText("")
                }
            }
        }

        addPairBtn = binding.ivEditorAddIcon
        addPairBtn.setOnClickListener {
            showAddNewPairAlertDialog(this) { key, value ->
                currentPairList.add(0, Pair(key, value))
                myAdapter.notifyItemInserted(0)
            }
            Log.d("pair", "onCreate: you pressed addPairBtn ")

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
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        myAdapter.notifyDataSetChanged()
                        showRemoveAlertDialog(position)
                        Log.d("Swipe", "activity: swiped left")
                    }
                }

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

    override fun showRemoveAlertDialog(position: Int) {
        val removeDialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val layoutInflater = this.layoutInflater
        val removeDialogView = layoutInflater.inflate(R.layout.dialog_remove_item, null)
        removeDialogBuilder.setView(removeDialogView)
        val removeDialog = removeDialogBuilder.create()
        val chosenPair = currentPairList[position]

        dialogRemovePairBinding = DialogRemoveItemBinding.bind(removeDialogView)

        dialogRemovePairBinding.tvRemoveDialogTitle.text = "${chosenPair.key} — ${chosenPair.value}"

        dialogRemovePairBinding.tvRemoveDialogMessage.text =
            getString(R.string.remove_dialog_warning)

        dialogRemovePairBinding.btnCancelRemove.setOnClickListener {
            myAdapter.notifyDataSetChanged()
            removeDialog.dismiss()
        }

        dialogRemovePairBinding.btnRemoveWordSet.setOnClickListener {
            myAdapter.removeItem(position)
            removeDialog.dismiss()
        }
        removeDialog.show()

    }

    private fun showAddNewPairAlertDialog(
        context: Context,
        onConfirm: ((String, String) -> Unit)?
    ): AlertDialog {
        val addPairDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val layoutInflater = this.layoutInflater
        val addPairDialogView = layoutInflater.inflate(R.layout.dialog_add_pair, null, false)
        addPairDialogBuilder.setView(addPairDialogView)
        val addPairDialog = addPairDialogBuilder.create()

        var keyInput = ""
        var valueInput = ""

        val addPairBinding = DialogAddPairBinding.bind(addPairDialogView)
        addPairBinding.tvAddPairDialogTitle.text = getString(R.string.add_pair_dialog_title)

        addPairBinding.etNewPairKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                keyInput = s.toString()
            }
        })

        addPairBinding.etNewPairValue.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               valueInput = s.toString()
            }
        })

        addPairBinding.btnAddPair.setOnClickListener{
            if(keyInput.isBlank()&& valueInput.isBlank()){
                keyInput = "Ключ не задан"
                valueInput = "Значение не задано"
            }
            onConfirm?.invoke(keyInput, valueInput)
            addPairDialog.dismiss()
        }

        addPairBinding.btnCancelAddPair.setOnClickListener{
            addPairDialog.dismiss()
        }

        addPairDialog.show()
        return addPairDialog
    }


//    private fun showAddNewPairAlertDialog(
//        context: Context,
//        onConfirm: ((String, String) -> Unit)?
//    ): AlertDialog {
//        val view = LayoutInflater.from(context)
//            .inflate(R.layout.dialog_add_pair, null, false)//кнопки добавь в верстку
//        val dialog = AlertDialog.Builder(context)
//            .setView(view)
//            .create()
//        val dialogBinding = DialogAddPairBinding.bind(view)
//        var keyString = ""
//        var valueString = ""
//        dialogBinding.etNewPairKey.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                keyString = s.toString()
//            }
//        })
//        dialogBinding.etNewPairValue.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                valueString = s.toString()
//            }
//        })
//        dialogBinding.btnAdd.setOnClickListener {
//            if (keyString.isBlank() && valueString.isBlank()) {
//                keyString = "Ключ не задан"
//                valueString = "Значение не задано"
//            }
//            onConfirm?.invoke(keyString, valueString)
//            dialog.dismiss()
//        }
//        dialogBinding.btnClose.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//        return dialog
//    }


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