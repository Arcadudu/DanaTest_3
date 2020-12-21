package ru.arcadudu.danatest_v030.activities

import android.content.Context
import android.content.DialogInterface
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
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.alertDialogs.AddPairDialogFragment
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import java.util.*

private lateinit var binding: ActivityWsEditorBinding
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var search: EditText
private lateinit var recyclerView: RecyclerView
private lateinit var myAdapter: PairRowAdapter

private lateinit var addPairDialog: AddPairDialogFragment

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
             showAddNewPairAlertDialog(this){ key, value ->
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
        Log.d("Swipe", "showRemoveAlertDialog: called ")
        val chosenPair: Pair = currentPairList[position]
        val itemName = "${chosenPair.key} / ${chosenPair.value}"
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Удаление пары")
            setMessage("Вы действительно хотите удалить пару\n\"$itemName\" ?")

            setPositiveButton("Удалить", DialogInterface.OnClickListener { _, _ ->
                myAdapter.removeItem(position)
                Snackbar.make(
                    recyclerView,
                    "\"$itemName\" удалено",
                    3000
                ).setBackgroundTint(resources.getColor(R.color.plt_active_blue, theme))
                    .setAction("Отмена", View.OnClickListener {
                        currentPairList.add(position, chosenPair)
                        myAdapter.notifyItemInserted(position)
                    })
                    .show()

            })
            setNegativeButton("Отмена", DialogInterface.OnClickListener { _, _ ->
                myAdapter.notifyDataSetChanged()
            })
        }

        val dialog = builder.create()
        dialog.show()

        val btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(R.color.plt_almost_black))
        val btnOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(R.color.plt_almost_black))

    }


    private fun showAddNewPairAlertDialog(
        context: Context,
        onConfirm: ((String, String) -> Unit)?
    ): AlertDialog {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_add_pair, null, false)//кнопки добавь в верстку
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        val dialogBinding = DialogAddPairBinding.bind(view)
        var keyString = ""
        var valueString = ""
        dialogBinding.etNewPairKey.addTextChangedListener(object : TextWatcher {
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
                keyString = s.toString()
            }
        })
        dialogBinding.etNewPairValue.addTextChangedListener(object : TextWatcher {
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
                valueString = s.toString()
            }
        })
        dialogBinding.btnAdd.setOnClickListener {
            if (keyString.isBlank() && valueString.isBlank()) {
                keyString = "Ключ не задан"
                valueString = "Значение не задано"
            }
            onConfirm?.invoke(keyString, valueString)
            dialog.dismiss()
        }
        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        return dialog
    }

    /*
        private fun showAddNewPairAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.dialog_add_pair, null)


        val dialogBinding = DialogAddPairBinding.bind(view)

        val editTextKey = dialogBinding.etNewPairKey

        val editTextValue = dialogBinding.etNewPairValue


        builder.setView(inflater.inflate(R.layout.dialog_add_pair, null))
            .setPositiveButton("Добавить", DialogInterface.OnClickListener { _, _ ->
*//*                val keyStr = dialogBinding.etNewPairKey.text.toString().trim()
                val valueStr = dialogBinding.etNewPairValue.text.toString().trim()*//*
                Log.d(
                    "pair",
                    "showAddNewPairAlertDialog: keyStr = $keyString | valueStr = $valueString"
                )
                Log.d(
                    "pair",
                    "showAddNewPairAlertDialog: before currentPairList size = ${currentPairList.size} "
                )
                Log.d(
                    "pair",
                    "showAddNewPairAlertDialog: after currentPairList size = ${currentPairList.size} "
                )

                editTextKey.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        keyString = s.toString().trim()
                    }

                })

                editTextValue.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        valueString = s.toString().trim()
                    }

                })
                currentPairList.add(0, Pair(keyString, valueString))

                myAdapter.notifyItemInserted(0)
            })
            .setNegativeButton("Отмена", DialogInterface.OnClickListener { _, _ ->
                myAdapter.notifyDataSetChanged()
            })

        val dialog = builder.create()
        dialog.show()

        val btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(getColor(R.color.plt_almost_black))
        val btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(R.color.plt_almost_black))
    }*/


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