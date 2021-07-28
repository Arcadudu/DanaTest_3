package ru.arcadudu.danatest_v030.pairSetFragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
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
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairSetAdapter
import ru.arcadudu.danatest_v030.databinding.*
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.pairsetEditorActivity.PairsetEditorActivity
import ru.arcadudu.danatest_v030.test.TestActivity
import ru.arcadudu.danatest_v030.utils.*
import java.util.*


private const val TAG = "cycle"

class PairsetFragment : MvpAppCompatFragment(), PairsetFragmentView {

    private lateinit var fragmentPairsetBinding: FragmentPairSetBinding
    private lateinit var removeDialogBinding: DialogRemoveItemBinding
    private lateinit var addPairsetDialogBinding: DialogAddPairsetBinding

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etPairsetSearchField: EditText
    private lateinit var btnClearSearchField: ImageView
    private lateinit var pairsetRecyclerView: RecyclerView
    private lateinit var pairsetAdapter: PairSetAdapter
    private lateinit var fabAddNewPairset: FloatingActionButton
    private lateinit var noPairsetStub: MaterialTextView

    private lateinit var dialogBuilder: AlertDialog.Builder

    private val pairsetColorHandler = PairsetColorHandler()

    private val paintWithColorResource = { colorResource: Int ->
        ResourcesCompat.getColor(
            resources,
            colorResource,
            requireActivity().theme
        )
    }

    @InjectPresenter
    lateinit var pairsetPresenter: PairsetFragmentPresenter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pair_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        fragmentPairsetBinding = FragmentPairSetBinding.bind(view)

        dialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)

        toolbar = fragmentPairsetBinding.toolbar
        prepareToolbar(toolbar)

        pairsetPresenter.apply {
            captureContext(requireContext())
            initiatePairsetList()
        }

        pairsetRecyclerView = fragmentPairsetBinding.wordSetRecycler
        preparePairsetRecycler(pairsetRecyclerView)
        initRecyclerSwiper(pairsetRecyclerView)

        pairsetPresenter.providePairsetListCount()

        etPairsetSearchField = fragmentPairsetBinding.etPairsetFragmentSearchfield
        etPairsetSearchField.doOnTextChanged { text, _, _, _ ->
            showBtnClear(text.toString().isEmpty())
            fabAddNewPairset.visibility =
                if (text.toString().isEmpty()) View.VISIBLE else View.GONE
            pairsetPresenter.filter(text.toString())
        }

        noPairsetStub = fragmentPairsetBinding.tvNoPairsetStub
        pairsetPresenter.checkIfThereAnyPairsets()

        btnClearSearchField = fragmentPairsetBinding.btnSearchClose
        btnClearSearchField.setOnClickListener {
            etPairsetSearchField.text = null
        }

        fabAddNewPairset = fragmentPairsetBinding.fabAddPairset
        fabAddNewPairset.setOnClickListener {
            showAddNewPairsetDialog()
        }
    }

    private fun prepareToolbar(targetToolbar: MaterialToolbar) {
        targetToolbar.apply {
            inflateMenu(R.menu.pairset_fragment_toolbar_menu)
            pairsetPresenter.checkIfThereAnyPairsets()
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.toolbar_action_sortBy -> {
                        showSortPairsetListDialog()
                    }
                }
                true
            }

        }

    }

    private fun showBtnClear(isStringEmpty: Boolean) {
        btnClearSearchField.visibility = if (isStringEmpty) View.GONE else View.VISIBLE
    }

    private fun preparePairsetRecycler(targetRecyclerView: RecyclerView) {
        pairsetAdapter = PairSetAdapter()
        pairsetAdapter.captureContext(requireContext())
        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pairsetAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
        pairsetPresenter.providePairsetList()
        pairsetAdapter.onItemClickCallback(this)
    }

    private fun initRecyclerSwiper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                pairsetPresenter.onMove(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        vibratePhone(24)
                        pairsetPresenter.onSwipedLeft(position)
                    }

                    ItemTouchHelper.RIGHT -> {
                        vibratePhone(24)
                        pairsetPresenter.onSwipedRight(position)
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
                    DtSwipeDecorator(viewHolder = viewHolder, context = requireContext())
                val itemViewWidth = viewHolder.itemView.right - viewHolder.itemView.left
                val alphaOffset = dX.toInt() / (itemViewWidth / dX)

                // swiping left -> remove item
                if (dX < 0 && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val backgroundRectF = dtSwipeDecorator.getSwipeBackgroundRectF(dX)
                    val iconDeleteBitmap =
                        dtSwipeDecorator.getSwipeBitmap(R.drawable.icon_close_onbrand_white)

                    val paint = dtSwipeDecorator.getSwipePaint(R.color.dt3_error_100)
                    val iconDestination = dtSwipeDecorator.getSwipeIconDestinationRectF(dX)

                    if (-alphaOffset > -255)
                        paint.alpha = alphaOffset.toInt()

                    canvas.drawRoundRect(backgroundRectF, 24f, 24f, paint)

                    if (dX < -220) {
                        if (iconDeleteBitmap != null) {
                            canvas.drawBitmap(iconDeleteBitmap, null, iconDestination, paint)
                        }
                    }
                }

                // swiping right -> show start test dialog
                if (dX > 0 && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val background = dtSwipeDecorator.getSwipeBackgroundRectF(dX)
                    val iconPlayTestBitmap =
                        dtSwipeDecorator.getSwipeBitmap(R.drawable.icon_play_onbrand_white)
                    val paint = dtSwipeDecorator.getSwipePaint(R.color.dt3_brand_100)
                    val iconDestination = dtSwipeDecorator.getSwipeIconDestinationRectF(dX)

                    if (alphaOffset < 255)
                        paint.alpha = alphaOffset.toInt()

                    canvas.drawRoundRect(background, 24f, 24f, paint)
                    if (dX > 220) {
                        if (iconPlayTestBitmap != null) {
                            canvas.drawBitmap(iconPlayTestBitmap, null, iconDestination, paint)
                        }
                    }
                }

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

    override fun showOnEmptyPairsetDialog(chosenPairset: Pairset) {
        val emptyPairsetDialogView =
            this.layoutInflater.inflate(R.layout.dialog_on_empty_pairset, null, false)
        val emptyPairsetDialog = dialogBuilder.setView(emptyPairsetDialogView).create()

        val dismissedWithAction = false
        emptyPairsetDialog.setOnDismissListener {
            if (!dismissedWithAction) {
                pairsetAdapter.notifyDataSetChanged()
            }
        }

        val emptyPairsetDialogBinding = DialogOnEmptyPairsetBinding.bind(emptyPairsetDialogView)
        emptyPairsetDialogBinding.apply {
            val chosenPairsetColor =
                pairsetColorHandler.getColorIntOnColorConstant(chosenPairset.pairsetColor)
            dialogOnEmptyPairsetHeader.setBackgroundColor(paintWithColorResource(chosenPairsetColor))

            tvOnEmptyPairsetDialogTitle.text =
                getString(R.string.dt_on_empty_pairset_dialog_title, chosenPairset.name)

            //dismiss button
            btnDismissDialog.setOnClickListener {
                pairsetAdapter.notifyDataSetChanged()
                emptyPairsetDialog.dismiss()
            }
        }

        emptyPairsetDialog.show()
    }

    private fun showSortPairsetListDialog() {
        val sortPairsetListDialogView =
            this.layoutInflater.inflate(R.layout.dialog_sort_pairset_list, null, false)
        val sortPairsetDialog = dialogBuilder.setView(sortPairsetListDialogView).create()

        val sortPairsetListDialogBinding =
            DialogSortPairsetListBinding.bind(sortPairsetListDialogView)

        sortPairsetListDialogBinding.apply {
            val sortRadioGroup = this.sortPairsetsDialogRadioGroup
            sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                val sortId =
                    when (checkedId) {
                        R.id.sort_pairsets_dialog_rb_sortByName_ascending -> 0
                        R.id.sort_pairsets_dialog_rb_sortByName_descending -> 1
                        R.id.sort_pairsets_dialog_rb_sortByCount_ascending -> 2
                        R.id.sort_pairsets_dialog_rb_sortByCount_descending -> 3
                        R.id.sort_pairsets_dialog_rb_sortByDate_ascending -> 4
                        else -> 5
                    }

                pairsetPresenter.sortPairsetList(sortId)
                sortPairsetDialog.dismiss()
            }
        }

        sortPairsetDialog.show()

    }


    override fun showStartTestDialog(chosenPairset: Pairset) {
        val startTestDialogView =
            this.layoutInflater.inflate(R.layout.dialog_start_test, null, false)
        val startTestDialog = dialogBuilder.setView(startTestDialogView).create()

        var dismissedWithAction = false
        startTestDialog.setOnDismissListener {
            if (!dismissedWithAction) {
                pairsetAdapter.notifyDataSetChanged()
            }
        }

        val testArray = resources.getStringArray(R.array.dt_test_names_array)
        val testArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_test_item, testArray)

        val startTestDialogBinding = DialogStartTestBinding.bind(startTestDialogView)

        @ColorInt
        val colorInt = pairsetColorHandler.getColorIntOnColorConstant(chosenPairset.pairsetColor)

        startTestDialogBinding.apply {
            dialogStartTestHeader.setBackgroundColor(paintWithColorResource(colorInt))

            tvStartTestDialogPairsetTitle.text = chosenPairset.name
            autoCompleteTestCase.apply {
                setAdapter(testArrayAdapter)
                setDropDownBackgroundResource(R.drawable.drop_down_background_drawable)
                allPairsetVariantsCheckBox.visibility = View.VISIBLE

                startTestDialogBinding.autoCompleteTestCase.doOnTextChanged { text, _, _, _ ->
                    startTestDialogBinding.allPairsetVariantsCheckBox.visibility =
                        if (text.toString() == getString(R.string.variants)) View.VISIBLE else View.GONE
                    val headerTestVariationIcon =
                        when (text.toString()) {
                            getString(R.string.variants) -> R.drawable.icon_test_icon_variants_descriptive
                            getString(R.string.translate) -> R.drawable.icon_test_icon_translate_descriptive
                            else -> R.drawable.icon_test_icon_shuffle_descriptive
                        }
//
                }

            }

            shufflePairsetCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
                val checkBoxTextColor =
                    if (isChecked) R.color.dt3_brand_100 else R.color.dt3_on_surface_70
                checkBox.setTextColor(paintWithColorResource(checkBoxTextColor))
            }

            enableHintsCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
                val checkBoxTextColor =
                    if (isChecked) R.color.dt3_brand_100 else R.color.dt3_on_surface_70
                checkBox.setTextColor(paintWithColorResource(checkBoxTextColor))

            }

            enableHintsCheckBox.isChecked = true
            enableHintsCheckBox.setTextColor(
                paintWithColorResource(R.color.dt3_brand_100)
            )

            allPairsetVariantsCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
                val checkBoxTextColor =
                    if (isChecked) R.color.dt3_brand_100 else R.color.dt3_on_surface_70
                checkBox.setTextColor(paintWithColorResource(checkBoxTextColor))
            }

            allPairsetVariantsCheckBox.apply {
                isChecked = chosenPairset.getPairList().count() < 4
                isEnabled = chosenPairset.getPairList().count() >= 4
            }
        }

        //positive btn
        startTestDialogBinding.btnStartTest.setOnClickListener {

            // collecting settings for test:
            val shufflePairset = startTestDialogBinding.shufflePairsetCheckBox.isChecked
            val enableHintsForTest = startTestDialogBinding.enableHintsCheckBox.isChecked
            val useAllExistingPairsetsValuesAsVariants =
                startTestDialogBinding.allPairsetVariantsCheckBox.isChecked
            val chosenTest = startTestDialogBinding.autoCompleteTestCase.text.toString()

            val toTestIntent = Intent(this.activity, TestActivity::class.java)

            dismissedWithAction = true
            toTestIntent.apply {
                putExtra("shuffle", shufflePairset)
                putExtra("enableHints", enableHintsForTest)
                putExtra("useAllPairsets", useAllExistingPairsetsValuesAsVariants)
                putExtra("test", chosenTest)
                putExtra("pairset", chosenPairset)
            }
            startActivity(toTestIntent)
            startTestDialog.dismiss()
        }

        //negative btn
        startTestDialogBinding.btnCancelStartTest.setOnClickListener {
            startTestDialog.dismiss()
        }

        startTestDialog.show()
    }

    override fun showAddNewPairsetDialog() {
        val addPairsetDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pairset, null, false)
        val addPairsetDialog = dialogBuilder.setView(addPairsetDialogView).create()

        var inputPairSetName = ""

        addPairsetDialogBinding = DialogAddPairsetBinding.bind(addPairsetDialogView)
        addPairsetDialogBinding.tvAddPairSetDialogTitle.text =
            getString(R.string.dt_add_pairset_dialog_title)


        var colorIndex = 0
        var pairsetColorConstant = ""
        addPairsetDialogBinding.ivPaintPairset.setOnClickListener {
            if (colorIndex > pairsetColorHandler.getListLastIndex()) colorIndex = 0
            val pairsetColorInt = pairsetColorHandler.getColorIntOnIndex(colorIndex)
            pairsetColorConstant = pairsetColorHandler.getColorConstantOnIndex(colorIndex)


            addPairsetDialogBinding.dialogAddPairsetHeader.setBackgroundColor(
                paintWithColorResource(pairsetColorInt)

            )
            colorIndex++

        }

        addPairsetDialogBinding.inputLayoutNewPairSetName.editText?.doOnTextChanged { text, _, _, _ ->
            inputPairSetName = text.toString().capitalize(Locale.getDefault()).trim()
        }
        addPairsetDialogBinding.inputLayoutNewPairSetName.editText?.apply {
            setOnFocusChangeListener { v, _ ->
                (activity as? MvpAppCompatActivity)?.forceShowKeyboard(v)
            }
            requestFocus()
        }

        // positive btn
        addPairsetDialogBinding.btnAddPairSet.setOnClickListener {
            if (inputPairSetName.isEmpty()) {
                addPairsetDialogBinding.inputLayoutNewPairSetName.editText?.apply {
                    error = getString(R.string.dt_add_pairset_dialog_on_empty_title_error)
                }
            } else {
                pairsetPresenter.addNewPairset(
                    inputPairSetName,
                    newPairsetColor = pairsetColorConstant
                )
                addPairsetDialog.dismiss()
                //todo presenter checks database for pairsets with same names
            }
        }

        //negative btn
        addPairsetDialogBinding.btnCancelAddWordSet.setOnClickListener {
            addPairsetDialog.dismiss()
        }
        addPairsetDialog.show()
    }

    override fun showRemovePairsetDialog(
        chosenPairset: Pairset,
        position: Int,
        pairCount: Int
    ) {
        val removeDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null, false)
        val removeDialog = dialogBuilder.setView(removeDialogView).create()

        var dismissedWithAction = false
        removeDialog.setOnDismissListener {
            if (!dismissedWithAction)
                pairsetAdapter.notifyDataSetChanged()
        }

        removeDialogBinding = DialogRemoveItemBinding.bind(removeDialogView)
        removeDialogBinding.apply {
            val chosenPairsetColorInt =
                pairsetColorHandler.getColorIntOnColorConstant(chosenPairset.pairsetColor)
            dialogRemoveItemHeader.setBackgroundColor(
                paintWithColorResource(chosenPairsetColorInt)

            )
            tvRemoveDialogTitle.text = chosenPairset.name
            tvRemovePairsetDialogPairCounter.text = pairCount.toString()
            btnCancelRemove.setOnClickListener {
                removeDialog.dismiss()
            }
            btnRemovePair.setOnClickListener {
                pairsetPresenter.removePairsetAtPosition(position)
                dismissedWithAction = true
                removeDialog.dismiss()
            }
        }
        removeDialog.show()
    }

    override fun updateRecyclerOnAdded(pairsetList: MutableList<Pairset>) {
        pairsetAdapter.apply {
            submitList(pairsetList)
            notifyItemInserted(0)
        }
        pairsetPresenter.providePairsetListCount()
        pairsetRecyclerView.smoothScrollToPosition(0)
    }

    override fun updateRecyclerOnRestored(
        pairsetList: MutableList<Pairset>,
        restoredPairsetPosition: Int
    ) {
        pairsetAdapter.apply {
            submitList(pairsetList)
            notifyItemInserted(restoredPairsetPosition)
        }
//        pairsetPresenter.providePairsetList()
        pairsetRecyclerView.smoothScrollToPosition(restoredPairsetPosition)
    }

    override fun updateRecyclerOnRemoved(updatedPairsetList: MutableList<Pairset>, position: Int) {
        pairsetAdapter.apply {
            submitList(updatedPairsetList)
            notifyItemRemoved(position)
        }
        pairsetPresenter.providePairsetListCount()
    }

    override fun obtainFilteredList(filteredList: MutableList<Pairset>) {
        pairsetAdapter.apply {
//            filterList(filteredList)
            submitList(filteredList)
            notifyDataSetChanged()
        }
        recyclerLayoutAnimation(pairsetRecyclerView, R.anim.layout_fall_down_anim)
    }

    override fun putPairsetIdIntoIntent(selectedPairsetId: Int) {
        val toEditorIntent = Intent(requireActivity(), PairsetEditorActivity::class.java).apply {
            putExtra(SELECTED_PAIRSET_ID_TO_EDITOR_TAG, selectedPairsetId)
        }
        startActivity(toEditorIntent)
    }

    override fun updateViewOnEmptyPairsetList(count: Int) {
        noPairsetStub.visibility = if (count == 0) View.VISIBLE else View.GONE
        toolbar.menu[0].apply {
            isEnabled = count != 0
            val sortByIconDrawable =
                if (count == 0) R.drawable.icon_sort_by_disabled else R.drawable.icon_sort_by
            icon =
                ResourcesCompat.getDrawable(resources, sortByIconDrawable, requireActivity().theme)
        }

    }

    override fun updateFragmentOnSorted(
        sortedList: MutableList<Pairset>
    ) {
        pairsetAdapter.submitList(sortedList)
        pairsetAdapter.notifyItemRangeChanged(0, sortedList.count())
        recyclerLayoutAnimation(pairsetRecyclerView, R.anim.layout_fall_down_with_alpha_anim)
    }

    override fun showOnRemoveSnackbar(deletedPairsetName: String, pairsetColor: String) {

        Snackbar.make(
            fabAddNewPairset,
            getString(R.string.dt_on_remove_pairset_snackBar_title, deletedPairsetName),
            10_000
        )
            .setBackgroundTint(
                paintWithColorResource(R.color.dt3_surface_100)
            ).setTextColor(
                paintWithColorResource(R.color.dt3_on_surface_100)
            ).setActionTextColor(
                paintWithColorResource(R.color.dt3_error_100)
            ).setAction(R.string.dt_on_remove_pairset_snackBar_action_text) {
                pairsetPresenter.restoreDeletedPairset()
            }.setAnchorView(fabAddNewPairset).show()

    }


    //lifecycle
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause:")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        pairsetAdapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        pairsetPresenter.detachView(this)
        Log.d(TAG, "onDestroy: ")
    }


    override fun updateToolbarInfo(pairSetCounter: String) {
        fragmentPairsetBinding.toolbar.subtitle = pairSetCounter
    }

    override fun retrievePairsetList(pairsetList: MutableList<Pairset>) {
        pairsetAdapter.submitList(pairsetList)
    }

    override fun updateRecyclerOnSwap(
        pairsetList: MutableList<Pairset>,
        fromPosition: Int,
        toPosition: Int
    ) {
        pairsetAdapter.notifyItemMoved(fromPosition, toPosition)
        pairsetPresenter.providePairsetListCount()
    }


}