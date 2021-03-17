package ru.arcadudu.danatest_v030.pairSetFragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairSetAdapter
import ru.arcadudu.danatest_v030.databinding.DialogAddPairSetBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.databinding.DialogStartTestBinding
import ru.arcadudu.danatest_v030.databinding.FragmentPairSetBinding
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.pairsetEditorActivity.PairsetEditorActivity
import ru.arcadudu.danatest_v030.test.TestActivity
import ru.arcadudu.danatest_v030.utils.drawableToBitmap
import ru.arcadudu.danatest_v030.utils.recyclerLayoutAnimation
import ru.arcadudu.danatest_v030.utils.vibratePhone
import java.util.*


private const val TAG = "cycle"
private const val TO_EDITOR_SELECTED_WORD_SET = "selectedWordSet"

class PairSetFragment : MvpAppCompatFragment(), PairSetFragmentView {

    private lateinit var fragmentWordSetBinding: FragmentPairSetBinding
    private lateinit var removeDialogBinding: DialogRemoveItemBinding
    private lateinit var addPairSetDialogBinding: DialogAddPairSetBinding

    private lateinit var etPairSetSearchField: EditText
    private lateinit var btnClearSearchField: ImageView
    private lateinit var pairSetRecyclerView: RecyclerView
    private lateinit var pairSetAdapter: PairSetAdapter
    private lateinit var fabAddNewPairSet: FloatingActionButton


    private lateinit var toolbar: MaterialToolbar

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

        toolbar = fragmentWordSetBinding.toolbar
        prepareToolbar(toolbar)

        val context = context
        if (context != null) {
            pairSetPresenter.captureContext(context)
        }

        pairSetPresenter.initiatePairSetList()

        pairSetRecyclerView = fragmentWordSetBinding.wordSetRecycler
        preparePairsetRecycler(pairSetRecyclerView)
        initRecyclerSwiper(pairSetRecyclerView)

        pairSetPresenter.providePairSetListCount()

        etPairSetSearchField = fragmentWordSetBinding.etWsFragSearchfield
        addTextWatcher(etPairSetSearchField)

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


    private fun addTextWatcher(targetEditText: EditText) {
        targetEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                showBtnClear(s.toString().isEmpty())
                pairSetPresenter.filter(s.toString())
            }
        })
    }


    private fun preparePairsetRecycler(targetRecyclerView: RecyclerView) {
        pairSetAdapter = PairSetAdapter()
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
                        vibratePhone(50)
                        pairSetPresenter.onSwipedLeft(position)
                    }

                    ItemTouchHelper.RIGHT -> {
                        vibratePhone(50)
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

                val itemView = viewHolder.itemView
                val itemViewHeight = (itemView.bottom - itemView.top).toFloat()
                val itemViewWidth = itemViewHeight / 3

                val paint = Paint().apply {
                    isAntiAlias = true
                    color = Color.WHITE
                    style = Paint.Style.FILL
                }

                // swiping left
                if (dX < 0 && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    paint.color =
                        ResourcesCompat.getColor(resources, R.color.dt3_main_light_grey_100, null)
                    val background = RectF(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )

                    canvas.drawRect(background, paint)
                    val iconDeleteDrawable: Drawable? =
                        ResourcesCompat.getDrawable(resources, R.drawable.icon_delete_blue, null)
                    val iconDeleteBitmap = drawableToBitmap(iconDeleteDrawable as Drawable)


                    val iconDestination = RectF(
                        itemView.right.toFloat() - 2 * itemViewWidth,
                        itemView.top.toFloat() + itemViewWidth,
                        itemView.right.toFloat() - itemViewWidth,
                        itemView.bottom.toFloat() - itemViewWidth
                    )

                    if (iconDeleteBitmap != null) {
                        canvas.drawBitmap(iconDeleteBitmap, null, iconDestination, paint)
                    }

                }

                /* method called again with dx restriction for left swipe */
                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX / 7.0f,
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

    override fun showStartTestDialog(chosenPairSet: PairSet) {
        val startTestDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val startTestDialogView =
            this.layoutInflater.inflate(R.layout.dialog_start_test, null, false)
        startTestDialogBuilder.setView(startTestDialogView)
        val startTestDialog = startTestDialogBuilder.create()
        var dismissedWithAction = false
        startTestDialog.setOnDismissListener {
            if (!dismissedWithAction) {
                pairSetAdapter.notifyDataSetChanged()
            }
        }

        val startTestDialogBinding = DialogStartTestBinding.bind(startTestDialogView)

        startTestDialogBinding.tvStartTestDialogTitle.text =
            getString(R.string.dt_start_test_dialog_title)
        val testArray = resources.getStringArray(R.array.dt_test_names_array)
        val testArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_test_item, testArray)
        startTestDialogBinding.autoCompleteTestCase.setAdapter(testArrayAdapter)

        //positive btn
        startTestDialogBinding.btnStartTest.setOnClickListener {
            dismissedWithAction = true
            val shufflePairset = startTestDialogBinding.shufflePairSetCheckBox.isChecked
            val chosenTest = startTestDialogBinding.autoCompleteTestCase.text.toString()
            val toTestIntent = Intent(this.activity, TestActivity::class.java)
            toTestIntent.apply {
                putExtra("shuffle", shufflePairset)
                putExtra("test", chosenTest)
                putExtra("pairset", chosenPairSet)
            }
            startActivity(toTestIntent)
//            startTestDialog.dismiss()
        }

        //negative btn
        startTestDialogBinding.btnCancelStartTest.setOnClickListener {
            startTestDialog.dismiss()
        }

        startTestDialog.show()
    }

    override fun showAddNewPairSetDialog() {
        val addPairSetDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val addPairSetDialogView =
            this.layoutInflater.inflate(R.layout.dialog_add_pair_set, null, false)
        addPairSetDialogBuilder.setView(addPairSetDialogView)
        val addPairSetDialog = addPairSetDialogBuilder.create()

        var inputPairSetName = ""
        var inputPairSetDetails = ""

        addPairSetDialogBinding = DialogAddPairSetBinding.bind(addPairSetDialogView)
        addPairSetDialogBinding.tvAddPairSetDialogTitle.text =
            getString(R.string.dt_add_pairset_dialog_title)

        addPairSetDialogBinding.inputLayoutNewPairSetName.editText?.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                inputPairSetName = s.toString().capitalize(Locale.ROOT).trim()
            }
        })

        addPairSetDialogBinding.inputLayoutNewPairSetDetails.editText?.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                inputPairSetDetails = s.toString().capitalize(Locale.ROOT).trim()
            }
        })
        // positive btn
        addPairSetDialogBinding.btnAddPairSet.setOnClickListener {
            if (inputPairSetName.isEmpty()) {
                addPairSetDialogBinding.inputLayoutNewPairSetName.editText?.apply {
                    error = getString(R.string.dt_add_pairset_dialog_on_empty_title_error)
                }
            } else {
                if (inputPairSetDetails.isEmpty()) inputPairSetDetails =
                    getString(R.string.add_pair_set_dialog_no_details)
                pairSetPresenter.addNewPairSet(inputPairSetName, inputPairSetDetails)
                addPairSetDialog.dismiss()
            }
        }

        //negative btn
        addPairSetDialogBinding.btnCancelAddWordSet.setOnClickListener {
            addPairSetDialog.dismiss()
        }
        addPairSetDialog.show()
    }


    override fun updateRecyclerOnAdded(pairSetList: MutableList<PairSet>) {
        pairSetAdapter.apply {
            submitList(pairSetList)
            notifyItemInserted(0)
        }
        pairSetPresenter.providePairSetListCount()
        pairSetRecyclerView.scrollToPosition(0)
    }


    override fun showRemovePairSetDialog(name: String, description: String, position: Int) {
        val removeDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val removeDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null)
        removeDialogBuilder.setView(removeDialogView)
        val removeDialog = removeDialogBuilder.create()
        var dismissedWithAction = false
        removeDialog.setOnDismissListener {
            if (!dismissedWithAction)
                pairSetAdapter.notifyDataSetChanged()
        }

        removeDialogBinding = DialogRemoveItemBinding.bind(removeDialogView)
        removeDialogBinding.tvRemoveDialogTitle.text = name
        removeDialogBinding.tvRemoveDialogMessage.text = getString(R.string.remove_dialog_message)

        removeDialogBinding.btnCancelRemove.setOnClickListener {
            removeDialog.dismiss()
        }

        removeDialogBinding.btnRemovePair.setOnClickListener {
            pairSetPresenter.removePairSetAtPosition(position)
            dismissedWithAction = true
            removeDialog.dismiss()
        }
        removeDialog.show()

    }

    override fun updateRecyclerOnRemoved(updatedPairSetList: MutableList<PairSet>, position: Int) {
        pairSetAdapter.apply {
            submitList(updatedPairSetList)
            notifyItemRemoved(position)
        }
        pairSetPresenter.providePairSetListCount()
    }

    override fun obtainFilteredList(filteredList: MutableList<PairSet>) {
        pairSetAdapter.filterList(filteredList)
        pairSetAdapter.notifyDataSetChanged()
        recyclerLayoutAnimation(pairSetRecyclerView, R.anim.layout_fall_down_anim)
    }

    override fun putPairSetIntoIntent(chosenPairSet: PairSet) {
        val toEditorIntent = Intent(activity, PairsetEditorActivity::class.java)
        toEditorIntent.putExtra(TO_EDITOR_SELECTED_WORD_SET, chosenPairSet)
        startActivity(toEditorIntent)
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
    }

    override fun onDestroy() {
        pairSetPresenter.detachView(this)
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
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