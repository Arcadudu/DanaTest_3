package ru.arcadudu.danatest_v030.pairSetEditorActivity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairWhiteBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.TestActivity
import java.util.*


private const val TO_EDITOR_SELECTED_WORD_SET = "selectedWordSet"

private const val TRANSLATE_FRAGMENT_ID = "TRANSLATE_FRAGMENT_ID"
private const val SHUFFLE_FRAGMENT_ID = "SHUFFLE_FRAGMENT_ID"
private const val VARIANTS_FRAGMENT_ID = "VARIANTS_FRAGMENT_ID"


class PairSetEditorActivity : MvpAppCompatActivity(), PairSetEditorView {
    private lateinit var activityWsEditorBinding: ActivityWsEditorBinding
    private lateinit var removeDialogBinding: DialogRemoveItemBinding
    private lateinit var pairSetForTesting: PairSet

    private lateinit var toolbar: Toolbar
    private lateinit var etPairSearchField: EditText
    private lateinit var btnClearSearchField: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var pairRowAdapter: PairRowAdapter
    private lateinit var fabAddPair: FloatingActionButton

    private val WORDSET_TO_TEST_TAG = "wordSetToTestTag"

    @InjectPresenter
    lateinit var pairSetEditorPresenter: PairSetEditorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWsEditorBinding = ActivityWsEditorBinding.inflate(layoutInflater)
        val view = activityWsEditorBinding.root
        setContentView(view)

        pairSetEditorPresenter.extractIncomingWordSet(intent, TO_EDITOR_SELECTED_WORD_SET)

        toolbar = activityWsEditorBinding.toolbar
        prepareToolbar(toolbar)

        recyclerView = activityWsEditorBinding.pairsRecycler
        preparePairRecycler(recyclerView)
        initRecyclerSwiper(recyclerView)

        /*поле поиска "пары" по названию*/
        etPairSearchField = activityWsEditorBinding.etEditorSearchField
        addTextWatcher(etPairSearchField)

        btnClearSearchField = activityWsEditorBinding.btnSearchClose
        showBtnClearAll(true)
        btnClearSearchField.setOnClickListener {
            etPairSearchField.text = null
        }

        fabAddPair = activityWsEditorBinding.fabAddPair
        fabAddPair.setOnClickListener {
            pairSetEditorPresenter.onAddNewPair()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ws_editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val toTestIntent = Intent(this, TestActivity::class.java)
        Log.d("aaa", "PSEditor: onOptionsItemSelected: callback ok")
        when (item.itemId) {
            R.id.begin_test_translate_menu_action -> toTestIntent.putExtra(
                "testFragmentId",
                TRANSLATE_FRAGMENT_ID
            )
            R.id.begin_test_shuffle_menu_action -> toTestIntent.putExtra(
                "testFragmentId",
                SHUFFLE_FRAGMENT_ID
            )
            R.id.begin_test_variants_menu_action -> toTestIntent.putExtra(
                "testFragmentId",
                VARIANTS_FRAGMENT_ID
            )
        }

        pairSetEditorPresenter.deliverWordSetForTest()
        toTestIntent.putExtra(WORDSET_TO_TEST_TAG, pairSetForTesting)
        startActivity(toTestIntent)
        return super.onOptionsItemSelected(item)
    }

    //    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val intent =
//            when (item.itemId) {
//                R.id.begin_test_translate_menu_action ->
//                    Intent(this, TranslateActivity::class.java)
//                R.id.begin_test_variants_menu_action ->
//                    Intent(this, VariantsActivity::class.java)
//                R.id.begin_test_shuffle_menu_action ->
//                    Intent(this, ShuffleActivity::class.java)
//                else -> null
//            }
//
//        pairSetEditorPresenter.deliverWordSetForTest()
//        intent?.putExtra(WORDSET_TO_TEST_TAG, pairSetForTesting)
//        startActivity(intent)
//        return super.onOptionsItemSelected(item)
//    }

    private fun prepareToolbar(targetToolbar: Toolbar) {
        val drawable = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.icon_hamburger_menu_active_violet_light
        )
        setSupportActionBar(targetToolbar)
        targetToolbar.apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            navigationIcon =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.icon_arrow_back_violet_light,
                    theme
                )
            this.setNavigationOnClickListener {
                // TODO: save wordSet state
                onBackPressed()
            }
            overflowIcon = drawable

        }
        pairSetEditorPresenter.provideDataForToolbar()
    }

    private fun preparePairRecycler(targetRecyclerView: RecyclerView) {
        pairRowAdapter = PairRowAdapter()

        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pairRowAdapter
            layoutManager = LinearLayoutManager(context)
        }
        pairSetEditorPresenter.providePairList()
        pairRowAdapter.onItemClickCallback(this)
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

                pairSetEditorPresenter.onMove(
                    fromPosition = viewHolder.bindingAdapterPosition,
                    toPosition = target.bindingAdapterPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
//                        pairRowAdapter.notifyDataSetChanged()
                        pairSetEditorPresenter.onSwipedLeft(position)
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
                pairSetEditorPresenter.filter(s.toString())
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
            pairSetEditorPresenter.removePairAtPosition(position)
            removeDialog.dismiss()
        }

        removeDialog.show()
    }

    override fun showEditPairDialog(
        position: Int,
        pairKey: String,
        pairValue: String
    ) {
        val editPairDialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val editPairDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pair_white, null, false)
        editPairDialogBuilder.setView(editPairDialogView)
        val editPairDialog = editPairDialogBuilder.create()

        val editPairBinding = DialogAddPairWhiteBinding.bind(editPairDialogView)
        editPairBinding.tvAddPairDialogTitle.text =
            getString(R.string.edit_pair_dialog_edit_button_text)

        var resultPairKey: String
        var resultPairValue: String

        var pairKeyAfterChange = ""
        var pairValueAfterChange = ""

        editPairBinding.etNewPairKey.setText(pairKey)
        editPairBinding.etNewPairValue.setText(pairValue)

        editPairBinding.etNewPairKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                pairKeyAfterChange = s.toString().capitalize(Locale.ROOT).trim()
            }

        })

        editPairBinding.etNewPairValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                pairValueAfterChange = s.toString().capitalize(Locale.ROOT).trim()
            }

        })

        editPairBinding.btnAddPair.text = getString(R.string.edit_pair_dialog_save_button_text)
        editPairBinding.btnAddPair.setOnClickListener {

            resultPairKey = if (pairKeyAfterChange.isEmpty()) pairKey.capitalize(Locale.ROOT).trim()
            else pairKeyAfterChange
            resultPairValue =
                if (pairValueAfterChange.isEmpty()) pairValue.capitalize(Locale.ROOT).trim()
                else pairValueAfterChange.capitalize(Locale.ROOT).trim()

            if (resultPairKey == pairKey && resultPairValue == pairValue) {
                editPairDialog.dismiss()
            } else {
                pairSetEditorPresenter.saveEditedPair(resultPairKey, resultPairValue, position)
            }
            editPairDialog.dismiss()

        }

        editPairBinding.btnCancelAddPair.setOnClickListener {
            editPairDialog.dismiss()
        }

        editPairBinding.ivSwapPairBtn.setOnClickListener {
            swapEditTexts(editPairBinding.etNewPairKey, editPairBinding.etNewPairValue)
        }

        editPairDialog.show()
    }


    override fun showAddNewPairDialog() {
        val addPairDialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val addPairDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pair_white, null, false)
        addPairDialogBuilder.setView(addPairDialogView)
        val addPairDialog = addPairDialogBuilder.create()

        var inputKey = ""
        var inputValue = ""

        val addPairBinding = DialogAddPairWhiteBinding.bind(addPairDialogView)
        addPairBinding.tvAddPairDialogTitle.text = getString(R.string.add_pair_dialog_title)

        addPairBinding.etNewPairKey.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                inputKey = s.toString().capitalize(Locale.ROOT).trim()
            }

        })

        addPairBinding.etNewPairValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                inputValue = s.toString().capitalize(Locale.ROOT).trim()
            }

        })

        addPairBinding.btnAddPair.setOnClickListener {
            if (inputKey.isBlank()) inputKey = getString(R.string.emptyInputKey)
            if (inputValue.isBlank()) inputValue = getString(R.string.emptyInputValue)
            pairSetEditorPresenter.addNewPair(inputKey, inputValue)
            addPairDialog.dismiss()
        }

        addPairBinding.btnCancelAddPair.setOnClickListener {
            addPairDialog.dismiss()
        }

        addPairBinding.ivSwapPairBtn.setOnClickListener {
            swapEditTexts(addPairBinding.etNewPairKey, addPairBinding.etNewPairValue)
        }

        addPairDialog.show()
    }

    private fun swapEditTexts(et1: EditText, et2: EditText) {
        Log.d("swapEditTexts", "swapEditTexts: callback ok ")
        val newEt1 = et2.text
        val newEt2 = et1.text

        et1.text = newEt1
        et2.text = newEt2
    }


    override fun getDataForToolbar(wordSetTitle: String, wordSetDescription: String) {
        toolbar.apply {
            title = wordSetTitle
            subtitle = wordSetDescription
        }
    }

    override fun initPairList(currentPairList: MutableList<Pair>) {
        pairRowAdapter.apply {
            submitPairs(currentPairList)
            notifyDataSetChanged()
        }
    }

    override fun obtainFilteredList(filteredList: MutableList<Pair>) {
        pairRowAdapter.filterList(filteredList)
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

    override fun obtainWordSetForTest(currentPairSet: PairSet) {
        pairSetForTesting = currentPairSet
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

    override fun updateRecyclerOnEditedPair(updatedPairList: MutableList<Pair>, position: Int) {
        pairRowAdapter.apply {
            submitPairs(updatedPairList)
            notifyItemInserted(position)
        }
    }
}




