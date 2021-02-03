package ru.arcadudu.danatest_v030.wordSetEditorActivity

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
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.models.Pair

private lateinit var activityWsEditorBinding: ActivityWsEditorBinding
private lateinit var removeDialogBinding: DialogRemoveItemBinding

private lateinit var toolbar: Toolbar
private lateinit var etPairSearchField: EditText
private lateinit var btnClearSearchField: ImageView
private lateinit var btnAddPair: ImageView
private lateinit var recyclerView: RecyclerView
private lateinit var pairRowAdapter: PairRowAdapter


private const val TO_EDITOR_SELECTED_WORD_SET = "selectedWordSet"

class WsEditorActivity : MvpAppCompatActivity(), WordSetEditorView {
    @InjectPresenter
    lateinit var wsEditorPresenter: WsEditorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWsEditorBinding = ActivityWsEditorBinding.inflate(layoutInflater)
        val view = activityWsEditorBinding.root
        setContentView(view)

        wsEditorPresenter.extractIncomingWordSet(intent, TO_EDITOR_SELECTED_WORD_SET)

        toolbar = activityWsEditorBinding.toolbar
        prepareToolbar(toolbar)

        recyclerView = activityWsEditorBinding.pairsRecycler
        preparePairRecycler(recyclerView)
        initRecyclerSwiper(recyclerView)

        /*поле поиска "пары" по названию*/
        etPairSearchField = activityWsEditorBinding.etEditorSearchField
        etPairSearchField.hint = getString(R.string.pair_search_field_hint)
        addTextWatcher(etPairSearchField)

        btnClearSearchField = activityWsEditorBinding.btnSearchClose
        showBtnClearAll(true)
        btnClearSearchField.setOnClickListener {
            etPairSearchField.text = null
        }

        btnAddPair = activityWsEditorBinding.ivEditorAddIcon
        btnAddPair.setOnClickListener {
            //presenter calls addNewPairDialog
            wsEditorPresenter.onAddNewPair()
            Log.d("pair", "onCreate: you pressed addPairBtn ")
        }
    }


    private fun prepareToolbar(targetToolbar: Toolbar) {
        setSupportActionBar(targetToolbar)
        targetToolbar.apply {
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
        wsEditorPresenter.provideDataForToolbar()
    }

    private fun preparePairRecycler(targetRecyclerView: RecyclerView) {
        pairRowAdapter = PairRowAdapter()
        val horizontalDivider = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider_drawable, null)?.let {
            horizontalDivider.setDrawable(
                it

            )
        }

        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pairRowAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(horizontalDivider)
        }
        pairRowAdapter.submitPairs(wsEditorPresenter.providePairList())
    }

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

                wsEditorPresenter.onMove(
                    fromPosition = viewHolder.bindingAdapterPosition,
                    toPosition = target.bindingAdapterPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                Log.d("Swiper", "Activity: onSwiped called")
                when (direction) {
                    ItemTouchHelper.LEFT -> {
//                        pairRowAdapter.notifyDataSetChanged()
                        Log.d(
                            "Swipe",
                            "Activity, direction = left! : onSwiped: position = $position"
                        )
                        wsEditorPresenter.onSwipedLeft(position)
                        Log.d("Swipe", "Activity: swiped left")
                    }
                }

            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun addTextWatcher(targetEditText: EditText) {
        targetEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                showBtnClearAll(s.toString().isEmpty())
                wsEditorPresenter.filter(s.toString())
            }

        })
    }

    override fun showBtnClearAll(isStringEmpty: Boolean) {
        btnClearSearchField.visibility = if (isStringEmpty) View.GONE else View.VISIBLE
    }


    override fun showRemovePairDialog(
        chosenPairKey: String,
        chosenPairValue: String,
        position: Int
    ) {

        val removeDialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val removeDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null)
        removeDialogBuilder.setView(removeDialogView)
        val removeDialog = removeDialogBuilder.create()

        removeDialogBinding = DialogRemoveItemBinding.bind(removeDialogView)
        removeDialogBinding.tvRemoveDialogTitle.text = "$chosenPairKey - $chosenPairValue"
        removeDialogBinding.tvRemoveDialogMessage.text =
            getString(R.string.remove_dialog_message)

        //negative btn
        removeDialogBinding.btnCancelRemove.setOnClickListener {
            pairRowAdapter.notifyDataSetChanged()
            removeDialog.dismiss()
        }
        //positive btn
        removeDialogBinding.btnRemovePair.setOnClickListener {
            wsEditorPresenter.removePairAtPosition(position)
            removeDialog.dismiss()
        }

        removeDialog.show()
    }

    override fun showAddNewPairDialog() {
        val addPairDialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val layoutInflater = this.layoutInflater
        val addPairDialogView = layoutInflater.inflate(R.layout.dialog_add_pair, null, false)
        addPairDialogBuilder.setView(addPairDialogView)
        val addPairDialog = addPairDialogBuilder.create()

        var inputKey = ""
        var inputValue = ""

        val addPairBinding = DialogAddPairBinding.bind(addPairDialogView)
        addPairBinding.tvAddPairDialogTitle.text = getString(R.string.add_pair_dialog_title)

        addPairBinding.etNewPairKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                inputKey = s.toString()
            }

        })

        addPairBinding.etNewPairValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                inputValue = s.toString()
            }

        })

        addPairBinding.btnAddPair.setOnClickListener {
            if (inputKey.isBlank()) inputKey = "Ключ не задан"
            if (inputValue.isBlank()) inputValue = "Значение не задано"
            wsEditorPresenter.addNewPair(inputKey, inputValue)
            addPairDialog.dismiss()
        }

        addPairBinding.btnCancelAddPair.setOnClickListener {
            addPairDialog.dismiss()
        }
        addPairDialog.show()
    }

    override fun obtainDataForToolbar(wordSetTitle: String, wordSetDescription: String) {
        toolbar.apply {
            title = wordSetTitle
            subtitle = wordSetDescription
        }
    }

    override fun obtainFilteredList(filteredList: MutableList<Pair>) {
        pairRowAdapter.filterList(filteredList)
    }


    override fun updatePairList(updatedPairList: MutableList<Pair>) {
        pairRowAdapter.submitPairs(updatedPairList)
        pairRowAdapter.notifyDataSetChanged()
    }


    override fun updateRecyclerOnSwap(
        updatedPairList: MutableList<Pair>,
        fromPosition: Int,
        toPosition: Int
    ) {
        pairRowAdapter.apply {
            submitPairs(updatedPairList)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    override fun updateRecyclerOnRemoved(updatedPairList: MutableList<Pair>, removePosition: Int) {
        pairRowAdapter.apply {
            submitPairs(updatedPairList)
            notifyItemRemoved(removePosition)
        }
    }

    override fun updateRecyclerOnAdded(updatedPairList: MutableList<Pair>) {
        pairRowAdapter.apply {
            submitPairs(updatedPairList)
            notifyItemInserted(0)
        }
        recyclerView.scrollToPosition(0)
    }
}




