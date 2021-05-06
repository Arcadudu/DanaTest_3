package ru.arcadudu.danatest_v030.pairsetEditorActivity

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.HomeActivity
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityPairsetEditorBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairBinding
import ru.arcadudu.danatest_v030.databinding.DialogAddPairsetBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.TestActivity
import ru.arcadudu.danatest_v030.utils.*
import java.util.*


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
    private lateinit var emptyPairsetStub: MaterialTextView

    private lateinit var dialogBuilder: AlertDialog.Builder


    @InjectPresenter
    lateinit var pairsetEditorPresenter: PairsetEditorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWsEditorBinding = ActivityPairsetEditorBinding.inflate(layoutInflater)
        val view = activityWsEditorBinding.root
        setContentView(view)

        dialogBuilder = AlertDialog.Builder(this, R.style.dt_CustomAlertDialog)

        pairsetEditorPresenter.apply {
            captureContext(applicationContext)
            extractIncomingPairset(
                incomingIntent =
                intent,
                pairset_index_tag =
                SELECTED_PAIRSET_INDEX_TO_EDITOR_TAG
            )
        }

        toolbar = activityWsEditorBinding.toolbar
        prepareToolbar(toolbar)


        pairRecyclerView = activityWsEditorBinding.pairsRecycler
        preparePairRecycler(pairRecyclerView)
        initRecyclerSwiper(pairRecyclerView)

        etPairSearchField = activityWsEditorBinding.etEditorSearchField
        etPairSearchField.doOnTextChanged { text, _, _, _ ->
            showBtnClearAll(text.toString().isEmpty())
            pairsetEditorPresenter.filter(text.toString())
        }

        emptyPairsetStub = activityWsEditorBinding.tvNoPairInPairsetStub
        pairsetEditorPresenter.checkIfPairsetIsEmpty()

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
        pairsetEditorPresenter.deliverPairsetForTest()
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
                startActivity(Intent(this@PairsetEditorActivity, HomeActivity::class.java))
            }
            overflowIcon = popUpMenuDrawable
            setOnClickListener {
                pairsetEditorPresenter.onToolbarClick()
            }
        }
        pairsetEditorPresenter.provideDataForToolbar()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@PairsetEditorActivity, HomeActivity::class.java))
    }

    override fun getDataForToolbar(pairsetTitle: String, pairsetUpdateDate: String) {
        toolbar.apply {
            title = pairsetTitle.capitalize(Locale.ROOT).trim()
            subtitle = pairsetUpdateDate
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

                val dtSwipeDecorator =
                    DtSwipeDecorator(viewHolder = viewHolder, context = this@PairsetEditorActivity)
                val itemViewWidth = viewHolder.itemView.right - viewHolder.itemView.left
                val alphaOffset = dX.toInt() / (itemViewWidth / dX)

                // swiping left
                if (dX < 0 && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val background = dtSwipeDecorator.getSwipeBackgroundRectF(dX)
                    val iconDeleteBitmap =
                        dtSwipeDecorator.getSwipeBitmap(R.drawable.icon_close_onbrand_white)
                    val paint = dtSwipeDecorator.getSwipePaint(R.color.dt3_error_100)

                    val iconDestination = dtSwipeDecorator.getSwipeIconDestinationRectF(dX)

                    if (-alphaOffset > -255) {
                        paint.alpha = alphaOffset.toInt()
                    }

                    canvas.drawRoundRect(background, 24f, 24f, paint)
                    if (dX < -220) {
                        if (iconDeleteBitmap != null) {
                            canvas.drawBitmap(iconDeleteBitmap, null, iconDestination, paint)
                        }
                    }
                }

                /* method called again with dx restriction for left swipe */
                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
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

    override fun showBtnClearAll(isStringEmpty: Boolean) {
        btnClearSearchField.visibility = if (isStringEmpty) View.GONE else View.VISIBLE
    }

    override fun showRemovePairDialog(
        chosenPairKey: String,
        chosenPairValue: String,
        position: Int
    ) {

        val removeDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null, false)
        val removeDialog = dialogBuilder.setView(removeDialogView).create()

        var dismissedWithAction = false
        removeDialog.setOnDismissListener {
            if (!dismissedWithAction)
                pairRowAdapter.notifyDataSetChanged()
        }

        removeDialogBinding = DialogRemoveItemBinding.bind(removeDialogView)

        removeDialogBinding.apply {
            tvRemoveDialogTitle.text = getString(
                R.string.dt_remove_pair_dialog_title,
                chosenPairKey.capitalize(Locale.ROOT).trim(),
                chosenPairValue.capitalize(Locale.ROOT).trim()
            )
            tvRemoveDialogMessage.text =
                getString(R.string.dt_remove_pair_dialog_message)

            //negative btn
            btnCancelRemove.setOnClickListener {
                removeDialog.dismiss()
            }
            //positive btn
            btnRemovePair.setOnClickListener {
                if (etPairSearchField.text.isNotEmpty())
                    etPairSearchField.text = null
                pairsetEditorPresenter.removePairAtPosition(position)
                dismissedWithAction = true
                removeDialog.dismiss()
            }
        }
        removeDialog.show()
    }

    override fun showEditPairDialog(
        position: Int,
        pairKey: String,
        pairValue: String
    ) {

        val editPairDialogView = this.layoutInflater.inflate(R.layout.dialog_add_pair, null, false)
        val editPairDialog = dialogBuilder.setView(editPairDialogView).create()

        val editPairBinding = DialogAddPairBinding.bind(editPairDialogView)
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

        etNewPairKey?.doOnTextChanged { text, _, _, _ ->
            pairKeyAfterChange = text.toString().capitalize(Locale.getDefault()).trim()
        }

        etNewPairValue?.doOnTextChanged { text, _, _, _ ->
            pairValueAfterChange = text.toString().capitalize(Locale.getDefault()).trim()
        }

        editPairBinding.apply {
            // positive button
            btnAddPair.text = getString(R.string.edit_pair_dialog_save_button_text)
            btnAddPair.setOnClickListener {
                resultPairKey =
                    if (pairKeyAfterChange.isEmpty()) pairKey.capitalize(Locale.ROOT).trim()
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
            // negative button
            btnCancelAddPair.setOnClickListener {
                editPairDialog.dismiss()
            }
            // swap key and value button
            ivSwapPairBtn.setOnClickListener {
                if (etNewPairKey != null && etNewPairValue != null)
                    swapEditTexts(etNewPairKey, etNewPairValue)
            }
        }
        etNewPairKey?.requestFocus()
        editPairDialog.show()
    }


    override fun showAddNewPairDialog() {
        val addPairDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pair, null, false)
        val addPairDialog = dialogBuilder.setView(addPairDialogView).show()

        var inputKey = ""
        var inputValue = ""

        val addPairBinding = DialogAddPairBinding.bind(addPairDialogView)
        addPairBinding.tvAddPairDialogTitle.text = getString(R.string.add_pair_dialog_title)

        val etNewPairKey = addPairBinding.inputLayoutNewPairKey.editText
        val etNewPairValue = addPairBinding.inputLayoutNewPairValue.editText

        etNewPairKey?.doOnTextChanged { text, _, _, _ ->
            inputKey = text.toString().capitalize(Locale.getDefault()).trim()
        }

        etNewPairValue?.doOnTextChanged { text, _, _, _ ->
            inputValue = text.toString().capitalize(Locale.getDefault()).trim()
        }

        addPairBinding.apply {
            // positive button
            btnAddPair.setOnClickListener {
                if (inputKey.isBlank() || inputValue.isBlank()) {
                    if (inputKey.isBlank()) {
                        etNewPairKey?.error = getString(R.string.dt_add_pair_dialog_empty_key_error)
                    }
                    if (inputValue.isBlank()) {
                        etNewPairValue?.error =
                            getString(R.string.dt_add_pair_dialog_empty_value_error)
                    }
                } else {
                    pairsetEditorPresenter.addNewPair(inputKey, inputValue)
                    addPairDialog.dismiss()
                }
            }
            // positive button
            btnCancelAddPair.setOnClickListener {
                addPairDialog.dismiss()
            }
            // swap pair key and value button
            ivSwapPairBtn.setOnClickListener {
                if (etNewPairKey != null && etNewPairValue != null)
                    swapEditTexts(etNewPairKey, etNewPairValue)
            }

        }
        etNewPairKey?.requestFocus()
        addPairDialog.show()
    }

    override fun showEditPairsetNameDialog(currentPairsetName: String) {
        val editPairsetNameDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pairset, null, false)
        val editPairsetNameDialog = dialogBuilder.setView(editPairsetNameDialogView).create()

        val editPairsetNameDialogBinding = DialogAddPairsetBinding.bind(editPairsetNameDialogView)
        editPairsetNameDialogBinding.tvAddPairSetDialogTitle.text =
            getString(R.string.dt_edit_pairset_name_dialog_title)

        var newPairsetName = ""

        editPairsetNameDialogBinding.inputLayoutNewPairSetName.editText?.apply {
            setText(currentPairsetName)
            doOnTextChanged { text, _, _, _ ->
                newPairsetName = text.toString().capitalize(Locale.getDefault()).trim()
            }
        }
        editPairsetNameDialogBinding.inputLayoutNewPairSetName.editText?.requestFocus()


        // positive btn
        editPairsetNameDialogBinding.btnAddPairSet.apply {
            text = getString(R.string.dt_edit_pairset_name_dialog_positive_btn)
            setOnClickListener {
                if (newPairsetName.isEmpty()) {
                    editPairsetNameDialogBinding.inputLayoutNewPairSetName.editText?.apply {
                        error = getString(R.string.dt_add_pairset_dialog_on_empty_title_error)
                    }
                } else {
                    //todo presenter check pairsetList for pairset with same names
                    pairsetEditorPresenter.changePairsetName(newPairsetName)
                    editPairsetNameDialog.dismiss()
                }
            }
        }

        // negative btn
        editPairsetNameDialogBinding.btnCancelAddWordSet.setOnClickListener {
            editPairsetNameDialog.dismiss()
        }



        editPairsetNameDialog.show()
    }


    private fun swapEditTexts(et1: EditText, et2: EditText) {
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
        pairRowAdapter.apply {
            filterList(filteredList)
            notifyDataSetChanged()
        }
        recyclerLayoutAnimation(pairRecyclerView, R.anim.layout_fall_down_anim)
    }


    override fun obtainPairsetForTest(currentPairSet: PairSet) {
        pairSetForTesting = currentPairSet
    }

    override fun setOnEmptyStub(count: Int) {
        emptyPairsetStub.visibility = if (count == 0) View.VISIBLE else View.GONE
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

    override fun onStop() {
        super.onStop()
        Log.d("stop", "onStop: callback ok")
//        pairsetEditorPresenter.onEditorStop()
    }
}




