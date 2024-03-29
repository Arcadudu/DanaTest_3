package ru.arcadudu.danatest_v030.pairsetEditorActivity

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.HomeActivity
import ru.arcadudu.danatest_v030.adapters.PairRowAdapter
import ru.arcadudu.danatest_v030.databinding.*
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.utils.*
import java.util.*


class PairsetEditorActivity : MvpAppCompatActivity(), PairsetEditorView {
    private lateinit var activityWsEditorBinding: ActivityPairsetEditorBinding


    private lateinit var removeDialogBinding: DialogRemoveItemBinding
    private lateinit var pairsetForTesting: Pairset

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etPairSearchField: EditText
    private lateinit var btnClearSearchField: ImageView
    private lateinit var pairRecyclerView: RecyclerView
    private lateinit var pairRowAdapter: PairRowAdapter
    private lateinit var fabAddPair: FloatingActionButton
    private lateinit var emptyPairsetStub: MaterialTextView

    private lateinit var dialogBuilder: AlertDialog.Builder

    private val pairsetColorHandler = PairsetColorHandler()

    private val paintWithColorResource = { colorResource: Int ->
        ResourcesCompat.getColor(
            resources,
            colorResource,
            theme
        )
    }


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
                pairset_id_tag =
                SELECTED_PAIRSET_ID_TO_EDITOR_TAG
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
            fabAddPair.visibility = if (text.toString().isEmpty()) View.VISIBLE else View.GONE
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

    private fun prepareToolbar(targetToolbar: MaterialToolbar) {
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
            inflateMenu(R.menu.pairset_editor_menu)
            setOnClickListener {
                pairsetEditorPresenter.onToolbarClick()
            }
            setOnMenuItemClickListener {
                showSortPairlistDialog()
                true
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

    private fun showSortPairlistDialog() {
        val sortPairlistDialogView =
            this.layoutInflater.inflate(R.layout.dialog_sort_pair_list, null, false)
        val sortPairlistDialog = dialogBuilder.setView(sortPairlistDialogView).create()

        val sortPairlistDialogBinding = DialogSortPairListBinding.bind(sortPairlistDialogView)

        sortPairlistDialogBinding.apply {
            val sortPairlistRadioGroup = this.sortPairsDialogRadioGroup
            sortPairlistRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                val sortId =
                    when (checkedId) {
                        R.id.sort_pairs_dialog_key_sortByName_ascending -> 0
                        R.id.sort_pairs_dialog_key_sortByName_descending -> 1
                        R.id.sort_pairs_dialog_value_sortByName_ascending -> 2
                        else -> 3
                    }
                pairsetEditorPresenter.sortPairlist(sortId)
                sortPairlistDialog.dismiss()
            }
        }

        sortPairlistDialog.show()
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
        comment VerticalOverScrollBounceEffectDecorator initialization and
        uncomment the following: */

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

            // no need to show pair counter when a single pair is removed from pairset,
            // so we hide it
            flRemovePairsetDialogPairCounterContainer.visibility = View.GONE

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

            //on reward info warning
            constraintLayoutOnRewardRemoveInformation.onRewardRemoveInformationNoteContainer.visibility =
                if (pairsetEditorPresenter.checkPairsetHasRewards()) View.VISIBLE else View.GONE


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
                    pairsetEditorPresenter.apply {
                        saveEditedPair(resultPairKey, resultPairValue, position)
                        removePassedTestRewardsOnAddedPair()
                    }
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

            constraintLayoutOnRewardRemoveInformation.onRewardRemoveInformationNoteContainer.visibility =
                if (pairsetEditorPresenter.checkPairsetHasRewards()) View.VISIBLE else View.GONE

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
                    pairsetEditorPresenter.removePassedTestRewardsOnAddedPair()
                    addPairDialog.dismiss()
                }
            }
            // negative button
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

        // flags indicating pairset changes
        var hasPairsetColorChanged = false
        var hasPairsetNameChanged: Boolean

        val editPairsetNameDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pairset, null, false)
        val editPairsetNameDialog = dialogBuilder.setView(editPairsetNameDialogView).create()

        val editPairsetNameDialogBinding = DialogAddPairsetBinding.bind(editPairsetNameDialogView)
        editPairsetNameDialogBinding.tvAddPairSetDialogTitle.text =
            getString(R.string.dt_edit_pairset_name_dialog_title)

        var colorIndex =
            pairsetColorHandler.getIndexOfColorConstant(pairsetEditorPresenter.getCurrentPairsetColor())

        // pairset original colorIntId and colorConstant
        val pairsetColorIntBeforeChange = pairsetColorHandler.getColorIntOnIndex(colorIndex)
        val pairsetColorConstantBeforeChange = pairsetEditorPresenter.getCurrentPairsetColor()

        // dialog header is painted with original pairsetColor
        val alertDialogHeader = editPairsetNameDialogBinding.dialogAddPairsetHeader
        alertDialogHeader.setBackgroundColor(paintWithColorResource(pairsetColorIntBeforeChange))

        // variables used to save color change
        var pairsetColorIntAfterChange: Int
        var pairsetColorConstantAfterChange = pairsetColorConstantBeforeChange

        // paint button (imageView)
        val buttonPaintPairset = editPairsetNameDialogBinding.ivPaintPairset
        buttonPaintPairset.setOnClickListener {
            colorIndex++
            if (colorIndex > pairsetColorHandler.getListLastIndex()) colorIndex = 0

            pairsetColorIntAfterChange = pairsetColorHandler.getColorIntOnIndex(colorIndex)
            pairsetColorConstantAfterChange =
                pairsetColorHandler.getColorConstantOnIndex(colorIndex)

            alertDialogHeader.setBackgroundColor(paintWithColorResource(pairsetColorIntAfterChange))

            // checks whether color changed or not
            hasPairsetColorChanged = pairsetColorIntBeforeChange != pairsetColorIntAfterChange
        }

        // editText
        val pairsetNameEditText = editPairsetNameDialogBinding.inputLayoutNewPairSetName.editText
        pairsetNameEditText?.apply {
            setText(currentPairsetName)
            requestFocus()
        }

        // positive button
        val buttonSaveChanges = editPairsetNameDialogBinding.btnAddPairSet
        buttonSaveChanges.text = getString(R.string.dt_edit_pairset_name_dialog_positive_btn)
        buttonSaveChanges.setOnClickListener {

            val pairsetFinalName = pairsetNameEditText?.text?.trim()
            hasPairsetNameChanged = pairsetFinalName?.equals(currentPairsetName)!!

            when {
                // empty name causes error to appear
                pairsetFinalName.isEmpty() ->
                    pairsetNameEditText.error =
                        getString(R.string.dt_add_pairset_dialog_on_empty_title_error)

                // no changes dismisses dialog
                !hasPairsetColorChanged && !hasPairsetNameChanged ->
                    editPairsetNameDialog.dismiss()

                // changes applied to pairset
                else -> {
                    pairsetEditorPresenter.changePairsetName(
                        newPairsetName = pairsetFinalName.toString(),
                        pairsetColorValue = pairsetColorConstantAfterChange
                    )
                    editPairsetNameDialog.dismiss()
                }
            }
        }

        // dismiss without changes button
        val buttonCancelChanges = editPairsetNameDialogBinding.btnCancelAddWordSet
        buttonCancelChanges.setOnClickListener {
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


    override fun obtainPairsetForTest(currentPairset: Pairset) {
        pairsetForTesting = currentPairset
    }

    override fun setOnEmptyStub(count: Int) {
        Log.d("menuItem", "setOnEmptyStub: count = $count ")
        emptyPairsetStub.visibility = if (count == 0) View.VISIBLE else View.GONE
    }

    override fun updateViewOnEmptyPairList(count: Int) {
        emptyPairsetStub.visibility = if (count == 0) View.VISIBLE else View.GONE
        toolbar.menu[0].apply {
            isEnabled = count != 0
            val sortByIconDrawable =
                if (count == 0) R.drawable.icon_sort_by_disabled else R.drawable.icon_sort_by
            icon = ResourcesCompat.getDrawable(resources, sortByIconDrawable, theme)
        }
    }

    override fun updateViewOnSortedPairlist(sortedPairlist: MutableList<Pair>, sortIndex: Int) {
        pairRowAdapter.apply {
            submitPairs(sortedPairlist)
            notifyItemRangeChanged(0, sortedPairlist.count())
        }
        recyclerLayoutAnimation(pairRecyclerView, R.anim.layout_fall_down_with_alpha_anim)

        //pairsetAdapter.submitList(sortedList)
        //        pairsetAdapter.notifyItemRangeChanged(0, sortedList.count())
        //        recyclerLayoutAnimation(pairsetRecyclerView, R.anim.layout_fall_down_with_alpha_anim)
    }

    override fun showOnRemovePairSnackbar(pair: Pair) {
        Snackbar.make(
            fabAddPair,
            getString(R.string.dt_on_remove_pair_snackBar_title, pair.pairValue, pair.pairKey),
            5000
        ).apply {
            anchorView = fabAddPair
            setBackgroundTint(
                ResourcesCompat.getColor(
                    resources,
                    R.color.dt3_error_100,
                    this@PairsetEditorActivity.theme
                )
            )
            setAction(getString(R.string.dt_on_remove_pair_snackBar_action_text)) {
                pairsetEditorPresenter.restoreDeletedPair()
            }
            show()
        }

    }

    override fun updateRecyclerOnAdded(updatedPairList: MutableList<Pair>) {
        pairRowAdapter.submitPairs(updatedPairList)
        pairRowAdapter.notifyItemInserted(0)
        pairsetEditorPresenter.checkIfPairsetIsEmpty()
//        toolbar.menu[0].isEnabled = updatedPairList.isNotEmpty()
        pairRecyclerView.smoothScrollToPosition(0)
    }

    override fun updateRecyclerOnRestored(
        currentPairList: MutableList<Pair>,
        lastRemovedPairPosition: Int
    ) {
        pairRowAdapter.submitPairs(currentPairList)
        pairRowAdapter.notifyItemInserted(lastRemovedPairPosition)
        pairRecyclerView.smoothScrollToPosition(lastRemovedPairPosition)
    }

    override fun showOnRemoveRewardsSnackBar(pairsetTitle: String) {
        Snackbar.make(fabAddPair, "$pairsetTitle изменен: результаты сброшены", 5000)
            .apply {
                anchorView = fabAddPair
                setBackgroundTint(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.dt3_brand_100,
                        this@PairsetEditorActivity.theme
                    )
                )
                show()
            }
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


    override fun updateRecyclerOnEditedPair(updatedPairList: MutableList<Pair>, position: Int) {
        pairRowAdapter.apply {
            submitPairs(updatedPairList)
            notifyItemInserted(position)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("stop", "onStop: callback ok")
    }
}




