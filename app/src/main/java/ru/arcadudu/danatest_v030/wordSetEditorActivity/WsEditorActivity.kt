package ru.arcadudu.danatest_v030.wordSetEditorActivity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moxy.MvpAppCompatActivity
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import java.util.*

private lateinit var activityWsEditorBinding: ActivityWsEditorBinding
private lateinit var dialogRemovePairBinding: DialogRemoveItemBinding

private lateinit var toolbar: Toolbar
private lateinit var etPairSearchField: EditText
private lateinit var btnClearSearchField: ImageView
private lateinit var btnAddPair: ImageView
private lateinit var currentWordSet: WordSet
private lateinit var currentPairList: MutableList<Pair>

private lateinit var recyclerView: RecyclerView
private lateinit var pairRowAdapter: PairRowAdapter

private var ivBtnClearIsShownAndEnabled = false

private const val TO_EDITOR_SELECTED_WORD_SET = "selectedWordSet"

class WsEditorActivity : MvpAppCompatActivity(),
    WordSetAdapter.OnItemSwipedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWsEditorBinding = ActivityWsEditorBinding.inflate(layoutInflater)
        val view = activityWsEditorBinding.root
        setContentView(view)

        extractIncomingWordSet(tag = TO_EDITOR_SELECTED_WORD_SET)

        toolbar = activityWsEditorBinding.toolbar

        prepareToolbar(toolbar, currentWordSet)

        recyclerView = activityWsEditorBinding.pairsRecycler
        preparePairRecycler(recyclerView, currentPairList)
        initRecyclerSwiper(recyclerView)

        etPairSearchField = activityWsEditorBinding.etEditorSearchField
        etPairSearchField.hint = getString(R.string.pair_search_field_hint)
        addTextWatcher(etPairSearchField)

        btnClearSearchField = activityWsEditorBinding.btnSearchClose
        showBtnClear(btnClearSearchField, false)
        btnClearSearchField.setOnClickListener {
            etPairSearchField.text = null
        }

        btnAddPair = activityWsEditorBinding.ivEditorAddIcon
        btnAddPair.setOnClickListener {
            //presenter calls addNewPairDialog
            showAddNewPairAlertDialog(this) { key, value ->
                currentPairList.add(0, Pair(key, value))
                //view notifies adapter
                pairRowAdapter.notifyItemInserted(0)
            }
            Log.d("pair", "onCreate: you pressed addPairBtn ")
        }
    }

    /*LEAVE HERE*/
    private fun prepareToolbar(targetToolbar: Toolbar, wordSet: WordSet) {
        setSupportActionBar(targetToolbar)
        targetToolbar.apply {
            title = wordSet.name
            subtitle = currentWordSet.description
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.icon_arrow_back_blue, theme)
            this.setNavigationOnClickListener {
                // TODO: 13.01.2021 save wordSet state
                onBackPressed()
            }
            targetToolbar.setOnClickListener {
                //Presenter calls dialog with editTexts for title and details
                Toast.makeText(context, "You clicked on toolbar!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*LEAVE HERE*/
    private fun preparePairRecycler(targetRecyclerView: RecyclerView, pairList: MutableList<Pair>) {
        pairRowAdapter = PairRowAdapter()
        pairRowAdapter.submitPairs(pairList) // to be executed after rendering
        val horizontalDivider = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        horizontalDivider.setDrawable(
            resources.getDrawable(R.drawable.divider_drawable, null)
        )

        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pairRowAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(horizontalDivider)
        }


    }

    /*LEAVE HERE*/
    private fun initRecyclerSwiper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                // presenter onMove(fromPosition, toPosition)

                Collections.swap(currentPairList, fromPosition, toPosition)
                pairRowAdapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        pairRowAdapter.notifyDataSetChanged()
                        // presenter showRemoveDialog
                        showRemoveAlertDialog(position)
                        Log.d("Swipe", "activity: swiped left")
                    }
                }

            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /*LEAVE HERE*/
    private fun addTextWatcher(targetEditText: EditText) {
        targetEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                //presenter checkStringForLetters(s.toString())
                val isStringEmpty = s.toString().isNotEmpty()
                showBtnClear(imageView = btnClearSearchField, showAndEnable = isStringEmpty)
                filter(s.toString())
            }

        })
    }

    /*LEAVE HERE*/
    private fun showBtnClear(imageView: ImageView, showAndEnable: Boolean) {
        imageView.visibility = if (showAndEnable) View.VISIBLE else View.GONE
        ivBtnClearIsShownAndEnabled = showAndEnable
    }


    /*MOVE TO PRESENTER*/
    private fun filter(text: String) {
        val filteredList: MutableList<Pair> = mutableListOf()
        for (item in currentPairList) {
            if (item.key.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                item.value.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        // here view receives and filters list
        pairRowAdapter.filterList(filteredList)
    }

    /*MOVE TO PRESENTER*/
    private fun extractIncomingWordSet(tag: String) {
        // presenter extractIncomingWordSet(serializableIncomingIntent :Intent)
        currentWordSet = intent.getSerializableExtra(tag) as WordSet
        currentPairList = currentWordSet.getPairList()
    }

    /*MOVE TO PRESENTER OR MAKE CLASS*/
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
            pairRowAdapter.notifyDataSetChanged()
            removeDialog.dismiss()
        }

        dialogRemovePairBinding.btnRemoveWordSet.setOnClickListener {
            pairRowAdapter.removeItem(position)
            removeDialog.dismiss()
        }
        removeDialog.show()

    }

    /*MOVE TO PRESENTER OR MAKE CLASS*/
    private fun showAddNewPairAlertDialog(
        context: Context,
        onConfirm: ((String, String) -> Unit)?
    ): AlertDialog {
        val addPairDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val layoutInflater = this.layoutInflater
        val addPairDialogView = layoutInflater.inflate(R.layout.dialog_add_pair, null, false)
        addPairDialogBuilder.setView(addPairDialogView)
        val addPairDialog = addPairDialogBuilder.create()

        var inputKey = ""
        var inputValue = ""

        val addPairBinding = DialogAddPairBinding.bind(addPairDialogView)
        addPairBinding.tvAddPairDialogTitle.text = getString(R.string.add_pair_dialog_title)

        addPairBinding.etNewPairKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                inputKey = s.toString()
            }
        })

        addPairBinding.etNewPairValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                inputValue = s.toString()
            }
        })

        addPairBinding.btnAddPair.setOnClickListener {
            if (inputKey.isBlank() && inputValue.isBlank()) {
                inputKey = "Ключ не задан"
                inputValue = "Значение не задано"
            }
            onConfirm?.invoke(inputKey, inputValue)
            addPairDialog.dismiss()
        }

        addPairBinding.btnCancelAddPair.setOnClickListener {
            addPairDialog.dismiss()
        }

        addPairDialog.show()
        return addPairDialog
    }


}