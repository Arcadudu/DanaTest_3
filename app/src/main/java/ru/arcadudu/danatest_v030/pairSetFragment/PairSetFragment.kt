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
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairSetAdapter
import ru.arcadudu.danatest_v030.databinding.*
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.pairsetEditorActivity.PairsetEditorActivity
import ru.arcadudu.danatest_v030.test.TestActivity
import ru.arcadudu.danatest_v030.utils.DtSwipeDecorator
import ru.arcadudu.danatest_v030.utils.SELECTED_PAIRSET_INDEX_TO_EDITOR_TAG
import ru.arcadudu.danatest_v030.utils.recyclerLayoutAnimation
import ru.arcadudu.danatest_v030.utils.vibratePhone
import java.util.*


private const val TAG = "cycle"

class PairSetFragment : MvpAppCompatFragment(), PairSetFragmentView {

    private lateinit var fragmentWordSetBinding: FragmentPairSetBinding
    private lateinit var removeDialogBinding: DialogRemoveItemBinding
    private lateinit var addPairSetDialogBinding: DialogAddPairsetBinding

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etPairSetSearchField: EditText
    private lateinit var btnClearSearchField: ImageView
    private lateinit var pairSetRecyclerView: RecyclerView
    private lateinit var pairSetAdapter: PairSetAdapter
    private lateinit var fabAddNewPairSet: FloatingActionButton
    private lateinit var noPairsetStub: MaterialTextView

    private lateinit var dialogBuilder: AlertDialog.Builder


    @InjectPresenter
    lateinit var pairSetPresenter: PairSetFragmentPresenter


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

        fragmentWordSetBinding = FragmentPairSetBinding.bind(view)

        dialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)

        toolbar = fragmentWordSetBinding.toolbar
        prepareToolbar(toolbar)

        pairSetPresenter.apply {
            captureContext(requireContext())
            initiatePairSetList()
        }

        pairSetRecyclerView = fragmentWordSetBinding.wordSetRecycler
        preparePairsetRecycler(pairSetRecyclerView)
        initRecyclerSwiper(pairSetRecyclerView)

        pairSetPresenter.providePairSetListCount()

        etPairSetSearchField = fragmentWordSetBinding.etWsFragSearchfield
        etPairSetSearchField.doOnTextChanged { text, _, _, _ ->
            showBtnClear(text.toString().isEmpty())
            pairSetPresenter.filter(text.toString())
        }

        noPairsetStub = fragmentWordSetBinding.tvNoPairsetStub
        pairSetPresenter.checkIfThereAnyPairsets()

        btnClearSearchField = fragmentWordSetBinding.btnSearchClose
        btnClearSearchField.setOnClickListener {
            etPairSetSearchField.text = null
        }

        fabAddNewPairSet = fragmentWordSetBinding.fabAddPairSet
        fabAddNewPairSet.setOnClickListener {
            showAddNewPairSetDialog()
        }
    }

    private fun prepareToolbar(targetToolbar: MaterialToolbar) {
        val popUpMenuDrawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.icon_hamgburger_menu_brand,
            activity?.theme
        )
        targetToolbar.overflowIcon = popUpMenuDrawable
    }

    private fun showBtnClear(isStringEmpty: Boolean) {
        btnClearSearchField.visibility = if (isStringEmpty) View.GONE else View.VISIBLE
    }

    private fun preparePairsetRecycler(targetRecyclerView: RecyclerView) {
        pairSetAdapter = PairSetAdapter()
        pairSetAdapter.captureContext(requireContext())
        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = pairSetAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        pairSetPresenter.providePairSetList()
        pairSetAdapter.onItemClickCallback(this)
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
                pairSetPresenter.onMove(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        vibratePhone(24)
                        pairSetPresenter.onSwipedLeft(position)
                    }

                    ItemTouchHelper.RIGHT -> {
                        vibratePhone(24)
                        pairSetPresenter.onSwipedRight(position)
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

                // swiping right -> play test
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

    override fun showOnEmptyPairSetDialog(chosenPairset: PairSet) {
        val emptyPairsetDialogView =
            this.layoutInflater.inflate(R.layout.dialog_on_empty_pairset, null, false)
        val emptyPairsetDialog = dialogBuilder.setView(emptyPairsetDialogView).create()

        val dismissedWithAction = false
        emptyPairsetDialog.setOnDismissListener {
            if (!dismissedWithAction) {
                pairSetAdapter.notifyDataSetChanged()
            }
        }

        val emptyPairsetDialogBinding = DialogOnEmptyPairsetBinding.bind(emptyPairsetDialogView)
        emptyPairsetDialogBinding.tvOnEmptyPairsetDialogTitle.text =
            getString(R.string.dt_on_empty_pairset_dialog_title, chosenPairset.name)

        //dismiss button
        emptyPairsetDialogBinding.btnDismissDialog.setOnClickListener {
            pairSetAdapter.notifyDataSetChanged()
            emptyPairsetDialog.dismiss()
        }

        emptyPairsetDialog.show()
    }

    override fun showStartTestDialog(chosenPairSet: PairSet) {
        val startTestDialogView =
            this.layoutInflater.inflate(R.layout.dialog_start_test, null, false)
        val startTestDialog = dialogBuilder.setView(startTestDialogView).create()

        var dismissedWithAction = false
        startTestDialog.setOnDismissListener {
            if (!dismissedWithAction) {
                pairSetAdapter.notifyDataSetChanged()
            }
        }

        val testArray = resources.getStringArray(R.array.dt_test_names_array)
        val testArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_test_item, testArray)

        val startTestDialogBinding = DialogStartTestBinding.bind(startTestDialogView)

        startTestDialogBinding.apply {
            tvStartTestDialogTitle.text =
                getString(R.string.dt_start_test_dialog_title)
            autoCompleteTestCase.apply {
                setAdapter(testArrayAdapter)
                setDropDownBackgroundResource(R.drawable.drop_down_background_drawable)
                startTestDialogBinding.autoCompleteTestCase.doOnTextChanged { text, _, _, _ ->
                    startTestDialogBinding.allPairSetVariantsCheckBox.visibility =
                        if (text.toString() == getString(R.string.variants)) View.VISIBLE else View.GONE
                }

            }

            //todo : make a style for checkboxes with textColor colorset
            shufflePairSetCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
                val checkBoxTextColor =
                    if (isChecked) R.color.dt3_brand_100 else R.color.dt3_hint_color_70
                checkBox.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        checkBoxTextColor,
                        activity?.theme
                    )
                )
            }

            allPairSetVariantsCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
                val checkBoxTextColor =
                    if (isChecked) R.color.dt3_error_100 else R.color.dt3_brand_100
                checkBox.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        checkBoxTextColor,
                        activity?.theme
                    )
                )
            }
        }

        //positive btn
        startTestDialogBinding.btnStartTest.setOnClickListener {

            val shufflePairset = startTestDialogBinding.shufflePairSetCheckBox.isChecked
            val chosenTest = startTestDialogBinding.autoCompleteTestCase.text.toString()
            val toTestIntent = Intent(this.activity, TestActivity::class.java)

            dismissedWithAction = true
            toTestIntent.apply {
                putExtra("shuffle", shufflePairset)
                putExtra("test", chosenTest)
                putExtra("pairset", chosenPairSet)
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

    override fun showAddNewPairSetDialog() {
        val addPairSetDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pairset, null, false)
        val addPairSetDialog = dialogBuilder.setView(addPairSetDialogView).create()

        var inputPairSetName = ""

        addPairSetDialogBinding = DialogAddPairsetBinding.bind(addPairSetDialogView)
        addPairSetDialogBinding.tvAddPairSetDialogTitle.text =
            getString(R.string.dt_add_pairset_dialog_title)

        addPairSetDialogBinding.inputLayoutNewPairSetName.editText?.doOnTextChanged { text, _, _, _ ->
            inputPairSetName = text.toString().capitalize(Locale.getDefault()).trim()
        }

        // positive btn
        addPairSetDialogBinding.btnAddPairSet.setOnClickListener {
            if (inputPairSetName.isEmpty()) {
                addPairSetDialogBinding.inputLayoutNewPairSetName.editText?.apply {
                    error = getString(R.string.dt_add_pairset_dialog_on_empty_title_error)
                }
            } else {
                pairSetPresenter.addNewPairSet(inputPairSetName)
                addPairSetDialog.dismiss()
                //todo presenter checks database for pairsets with same names
            }
        }

        //negative btn
        addPairSetDialogBinding.btnCancelAddWordSet.setOnClickListener {
            addPairSetDialog.dismiss()
        }
        addPairSetDialog.show()
    }

    override fun showRemovePairSetDialog(name: String, description: String, position: Int) {
        val removeDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null, false)
        val removeDialog = dialogBuilder.setView(removeDialogView).create()

        var dismissedWithAction = false
        removeDialog.setOnDismissListener {
            if (!dismissedWithAction)
                pairSetAdapter.notifyDataSetChanged()
        }

        removeDialogBinding = DialogRemoveItemBinding.bind(removeDialogView)
        removeDialogBinding.apply {
            tvRemoveDialogTitle.text = name
            tvRemoveDialogMessage.text = getString(R.string.remove_dialog_message)
            btnCancelRemove.setOnClickListener {
                removeDialog.dismiss()
            }
            btnRemovePair.setOnClickListener {
                pairSetPresenter.removePairSetAtPosition(position)
                dismissedWithAction = true
                removeDialog.dismiss()
            }
        }
        removeDialog.show()
    }

    override fun updateRecyclerOnAdded(pairSetList: MutableList<PairSet>) {
        pairSetAdapter.apply {
            submitList(pairSetList)
            notifyItemInserted(0)
        }
        pairSetPresenter.providePairSetListCount()
        pairSetRecyclerView.scrollToPosition(0)
    }

    override fun updateRecyclerOnRemoved(updatedPairSetList: MutableList<PairSet>, position: Int) {
        pairSetAdapter.apply {
            submitList(updatedPairSetList)
            notifyItemRemoved(position)
        }
        pairSetPresenter.providePairSetListCount()
    }

    override fun obtainFilteredList(filteredList: MutableList<PairSet>) {
        pairSetAdapter.apply {
            filterList(filteredList)
            notifyDataSetChanged()
        }
        recyclerLayoutAnimation(pairSetRecyclerView, R.anim.layout_fall_down_anim)
    }

    override fun putPairsetIndexIntoIntent(bindingAdapterPosition: Int) {
        val toEditorIntent = Intent(activity, PairsetEditorActivity::class.java).apply {
            putExtra(SELECTED_PAIRSET_INDEX_TO_EDITOR_TAG, bindingAdapterPosition)
        }
        startActivity(toEditorIntent)
    }

    override fun setOnEmptyStub(count: Int) {
        noPairsetStub.visibility = if (count == 0) View.VISIBLE else View.GONE
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
        pairSetAdapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        pairSetPresenter.detachView(this)
        Log.d(TAG, "onDestroy: ")
    }


    override fun updateToolbarInfo(pairSetCounter: String) {
        fragmentWordSetBinding.toolbar.subtitle = pairSetCounter
    }

    override fun retrievePairSetList(pairSetList: MutableList<PairSet>) {
        pairSetAdapter.submitList(pairSetList)
    }

    override fun updateRecyclerOnSwap(
        pairSetList: MutableList<PairSet>,
        fromPosition: Int,
        toPosition: Int
    ) {
        pairSetAdapter.apply {
            submitList(pairSetList)
            notifyItemMoved(fromPosition, toPosition)
        }
        pairSetPresenter.providePairSetListCount()
    }
}