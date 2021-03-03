package ru.arcadudu.danatest_v030.pairSetFragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairSetAdapter
import ru.arcadudu.danatest_v030.databinding.DialogAddPairSetBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.databinding.FragmentPairSetBinding
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.pairSetEditorActivity.PairSetEditorActivity
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

    private lateinit var toolbar: Toolbar

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

        val context = context
        if (context != null) {
            pairSetPresenter.captureContext(context)
        }

        pairSetPresenter.initiatePairSetList()

        pairSetRecyclerView = fragmentWordSetBinding.wordSetRecycler
        prepareWordSetRecycler(pairSetRecyclerView)
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
            pairSetPresenter.onAddNewPairSet()
        }

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


    private fun prepareWordSetRecycler(targetRecyclerView: RecyclerView) {
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
            ItemTouchHelper.LEFT
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
                        pairSetAdapter.notifyDataSetChanged()
                        pairSetPresenter.onSwipedLeft(position)
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

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
            getString(R.string.add_pair_set_dialog_title)


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

        addPairSetDialogBinding.btnAddPairSet.setOnClickListener {
            if (inputPairSetName.isEmpty()) {
                addPairSetDialogBinding.inputLayoutNewPairSetName.editText?.apply {
                    error = "Название набора не может быть пустым"
                }
            } else {
                if (inputPairSetDetails.isEmpty()) inputPairSetDetails =
                    getString(R.string.add_pair_set_dialog_no_details)
                pairSetPresenter.addNewPairSet(inputPairSetName, inputPairSetDetails)
                addPairSetDialog.dismiss()

            }
        }

        addPairSetDialogBinding.btnCancelAddWordSet.setOnClickListener {
            addPairSetDialog.dismiss()
        }

        addPairSetDialog.show()
    }

    override fun updateRecyclerOnAdded(pairSetList: MutableList<PairSet>) {
        pairSetAdapter.apply {
            submitList(pairSetList)
            notifyItemInserted(1)
        }
        pairSetPresenter.providePairSetListCount()
        pairSetRecyclerView.scrollToPosition(1)
    }


    override fun showRemovePairSetDialog(name: String, description: String, position: Int) {
        Log.d("swipe", "showAddWordSetAlertDialog: method called")
        val removeDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val removeDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null)
        removeDialogBuilder.setView(removeDialogView)
        val removeDialog = removeDialogBuilder.create()

        removeDialogBinding = DialogRemoveItemBinding.bind(removeDialogView)
        removeDialogBinding.tvRemoveDialogTitle.text = name
        removeDialogBinding.tvRemoveDialogMessage.text = getString(R.string.remove_dialog_message)

        removeDialogBinding.btnCancelRemove.setOnClickListener {
            pairSetAdapter.notifyDataSetChanged()
            removeDialog.dismiss()
        }

        removeDialogBinding.btnRemovePair.setOnClickListener {
            pairSetPresenter.removePairSetAtPosition(position)
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
    }

    override fun putPairSetIntoIntent(chosenPairSet: PairSet) {
        val toEditorIntent = Intent(activity, PairSetEditorActivity::class.java)
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