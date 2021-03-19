package ru.arcadudu.danatest_v030.pairsetEditorActivity

import android.content.Intent
import android.graphics.Canvas
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityPairsetEditorBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairLightBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.TestActivity
import ru.arcadudu.danatest_v030.utils.*
import java.util.*


private const val TO_EDITOR_SELECTED_WORD_SET = "selectedWordSet"

private const val TRANSLATE_FRAGMENT_ID = "TRANSLATE_FRAGMENT_ID"
private const val SHUFFLE_FRAGMENT_ID = "SHUFFLE_FRAGMENT_ID"
private const val VARIANTS_FRAGMENT_ID = "VARIANTS_FRAGMENT_ID"


class PairsetEditorActivity : MvpAppCompatActivity(), PairsetEditorView {
    private lateinit var activityWsEditorBinding: ActivityPairsetEditorBinding
    private lateinit var removeDialogBinding: DialogRemoveItemBinding
    private lateinit var pairSetForTesting: PairSet

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etPairSearchField: EditText
    private lateinit var btnClearSearchField: ImageView
    private lateinit var pairRecyclerView: RecyclerView
    private lateinit var pairRowAdapter: PairRowAdapter
    private lateinit var fabAddPair: FloatingActionButton


    @InjectPresenter
    lateinit var pairsetEditorPresenter: PairsetEditorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWsEditorBinding = ActivityPairsetEditorBinding.inflate(layoutInflater)
        val view = activityWsEditorBinding.root
        setContentView(view)

        pairsetEditorPresenter.extractIncomingWordSet(intent, TO_EDITOR_SELECTED_WORD_SET)

        toolbar = activityWsEditorBinding.toolbar
        prepareToolbar(toolbar)

        pairRecyclerView = activityWsEditorBinding.pairsRecycler
        preparePairRecycler(pairRecyclerView)
        initRecyclerSwiper(pairRecyclerView)

        etPairSearchField = activityWsEditorBinding.etEditorSearchField
        addTextWatcher(etPairSearchField)

        btnClearSearchField = activityWsEditorBinding.btnSearchClose
        showBtnClearAll(true)
        btnClearSearchField.setOnClickListener {
            etPairSearchField.text = null
        }

        fabAddPair = activityWsEditorBinding.fabAddPair
        fabAddPair.setOnClickListener {
            pairsetEditorPresenter.onAddNewPair()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pairset_editor_menu, menu)
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

        pairsetEditorPresenter.deliverWordSetForTest()
        toTestIntent.putExtra(CONST_PAIRSET_TO_TEST_TAG, pairSetForTesting)
        startActivity(toTestIntent)
        return super.onOptionsItemSelected(item)
    }

    private fun prepareToolbar(targetToolbar: MaterialToolbar) {
        val popUpMenuDrawable = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.icon_play_button
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
            overflowIcon = popUpMenuDrawable


        }
        pairsetEditorPresenter.provideDataForToolbar()
    }

    override fun getDataForToolbar(wordSetTitle: String, wordSetDescription: String) {
        toolbar.apply {
            title = wordSetTitle.capitalize(Locale.ROOT).trim()
            subtitle = wordSetDescription
        }
    }

    private fun preparePairRecycler(targetRecyclerView: RecyclerView) {
        pairRowAdapter = PairRowAdapter()

        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pairRowAdapter
            layoutManager = LinearLayoutManager(context)
        }
        pairsetEditorPresenter.providePairList()
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

                pairsetEditorPresenter.onMove(
                    fromPosition = viewHolder.bindingAdapterPosition,
                    toPosition = target.bindingAdapterPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        vibratePhone(50)
                        pairsetEditorPresenter.onSwipedLeft(position)
                    }
                }

            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val dtSwipeDecorator = DtSwipeDecorator(viewHolder = viewHolder, context = this@PairsetEditorActivity)

                // swiping left
                if (dX < 0 && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val background = dtSwipeDecorator.getSwipeBackgroundRectF(dX)
                    val iconDeleteBitmap = dtSwipeDecorator.getSwipeBitmap(R.drawable.icon_delete_error_white)
                    val paint = dtSwipeDecorator.getSwipePaint(R.color.dt3_error_red_70)
                    val iconDestination = dtSwipeDecorator.getSwipeIconDestinationRectF(dX)

                    canvas.drawRoundRect(background, 24f, 24f, paint)
                    if (iconDeleteBitmap != null) {
                        canvas.drawBitmap(iconDeleteBitmap, null, iconDestination, paint)
                    }
                }

                /* method called again with dx restriction for left swipe */
                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX / 8.0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        VerticalOverScrollBounceEffectDecorator(
            RecyclerViewOverScrollDecorAdapter(
                recyclerView,
                itemTouchHelperCallback
            )
        )

        /* In case of overScroll decoration is not needed -
        * uncomment the following: */
//        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
//        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun addTextWatcher(targetEditText: EditText) {
        targetEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                showBtnClearAll(s.toString().isEmpty())
                pairsetEditorPresenter.filter(s.toString())
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
        var dismissedWithAction = false
        removeDialog.setOnDismissListener {
            if (!dismissedWithAction)
                pairRowAdapter.notifyDataSetChanged()
        }

        removeDialogBinding = DialogRemoveItemBinding.bind(removeDialogView)

        removeDialogBinding.tvRemoveDialogTitle.text = getString(
            R.string.dt_remove_pair_dialog_title,
            chosenPairKey.capitalize(Locale.ROOT).trim(),
            chosenPairValue.capitalize(Locale.ROOT).trim()
        )
        removeDialogBinding.tvRemoveDialogMessage.text =
            getString(R.string.dt_remove_pair_dialog_message)

        //negative btn
        removeDialogBinding.btnCancelRemove.setOnClickListener {
            removeDialog.dismiss()
        }
        //positive btn
        removeDialogBinding.btnRemovePair.setOnClickListener {
            pairsetEditorPresenter.removePairAtPosition(position)
            dismissedWithAction = true
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
            this.layoutInflater.inflate(R.layout.dialog_add_pair_light, null, false)
        editPairDialogBuilder.setView(editPairDialogView)
        val editPairDialog = editPairDialogBuilder.create()

        val editPairBinding = DialogAddPairLightBinding.bind(editPairDialogView)
        editPairBinding.tvAddPairDialogTitle.text =
            getString(R.string.edit_pair_dialog_edit_button_text)

        var resultPairKey: String
        var resultPairValue: String

        var pairKeyAfterChange = ""
        var pairValueAfterChange = ""

        val etNewPairKey = editPairBinding.inputLayoutNewPairKey.editText
        val etNewPairValue = editPairBinding.inputLayoutNewPairValue.editText

        etNewPairKey?.setText(pairKey.capitalize(Locale.ROOT))
        etNewPairValue?.setText(pairValue.capitalize(Locale.ROOT))

        etNewPairKey?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                pairKeyAfterChange = s.toString().capitalize(Locale.ROOT).trim()
            }

        })

        etNewPairValue?.addTextChangedListener(object : TextWatcher {
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
                pairsetEditorPresenter.saveEditedPair(resultPairKey, resultPairValue, position)
            }
            editPairDialog.dismiss()

        }

        editPairBinding.btnCancelAddPair.setOnClickListener {
            editPairDialog.dismiss()
        }

        editPairBinding.ivSwapPairBtn.setOnClickListener {
            if (etNewPairKey != null && etNewPairValue != null)
                swapEditTexts(etNewPairKey, etNewPairValue)
        }

        editPairDialog.show()
    }


    override fun showAddNewPairDialog() {
        val addPairDialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val addPairDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pair_light, null, false)
        addPairDialogBuilder.setView(addPairDialogView)
        val addPairDialog = addPairDialogBuilder.create()

        var inputKey = ""
        var inputValue = ""

        val addPairBinding = DialogAddPairLightBinding.bind(addPairDialogView)
        addPairBinding.tvAddPairDialogTitle.text = getString(R.string.add_pair_dialog_title)

        val etNewPairKey = addPairBinding.inputLayoutNewPairKey.editText
        val etNewPairValue = addPairBinding.inputLayoutNewPairValue.editText

        etNewPairKey?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                inputKey = s.toString().capitalize(Locale.ROOT).trim()
            }

        })

        etNewPairValue?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                inputValue = s.toString().capitalize(Locale.ROOT).trim()
            }

        })

        addPairBinding.btnAddPair.setOnClickListener {
            if(inputKey.isBlank() || inputValue.isBlank()){
                if (inputKey.isBlank()) {
                    etNewPairKey?.error = "Ключ не может быть пустым"
                }
                if (inputValue.isBlank()) {
                    etNewPairValue?.error = "Значение не может быть пустым"
                }
            }else{
                pairsetEditorPresenter.addNewPair(inputKey, inputValue)
                addPairDialog.dismiss()
            }

        }

        addPairBinding.btnCancelAddPair.setOnClickListener {
            addPairDialog.dismiss()
        }

        addPairBinding.ivSwapPairBtn.setOnClickListener {
            if (etNewPairKey != null && etNewPairValue != null)
                swapEditTexts(etNewPairKey, etNewPairValue)
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


    override fun initPairList(currentPairList: MutableList<Pair>) {
        pairRowAdapter.apply {
            submitPairs(currentPairList)
            notifyDataSetChanged()
        }
    }

    override fun obtainFilteredList(filteredList: MutableList<Pair>) {
        pairRowAdapter.filterList(filteredList)
        pairRowAdapter.notifyDataSetChanged()
        recyclerLayoutAnimation(pairRecyclerView, R.anim.layout_fall_down_anim)
    }


    override fun obtainWordSetForTest(currentPairSet: PairSet) {
        pairSetForTesting = currentPairSet
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
        pairRecyclerView.scrollToPosition(0)
    }

    override fun updateRecyclerOnEditedPair(updatedPairList: MutableList<Pair>, position: Int) {
        pairRowAdapter.apply {
            submitPairs(updatedPairList)
            notifyItemInserted(position)
        }
    }
}




