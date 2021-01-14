package ru.arcadudu.danatest_v030.fragments

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.WsEditorActivity
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.databinding.DialogAddWordSetBinding
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.databinding.FragmentWordSetBinding
import ru.arcadudu.danatest_v030.interfaces.TransferToEditor
import ru.arcadudu.danatest_v030.models.WordSet
import ru.arcadudu.danatest_v030.utils.getTimeWordSet
import java.util.*

private lateinit var fragmentWordSetBinding: FragmentWordSetBinding
private lateinit var dialogRemoveWordSetBinding: DialogRemoveItemBinding
private lateinit var dialogAddWordSetBinding: DialogAddWordSetBinding

private lateinit var favoriteWordSet: WordSet
private lateinit var etWordSetSearchField: EditText
private lateinit var btnAddNewWordSet: ImageView
private lateinit var btnClearSearchField: ImageView
private lateinit var recyclerView: RecyclerView

private lateinit var wordSetAdapter: WordSetAdapter
private const val TAG = "cycle"

private const val TO_EDITOR_SELECTED_WORD_SET = "selectedWordSet"
private var ivBtnClearIsShownAndEnabled = false

private var wordSetList: MutableList<WordSet> = mutableListOf()

class WordSetFragment : Fragment(), TransferToEditor, WordSetAdapter.OnItemSwipedListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        fragmentWordSetBinding = FragmentWordSetBinding.bind(view)
        ///////////////////////////////////////////////

        packWordSetList(wordSetList)

        recyclerView = fragmentWordSetBinding.wordSetRecycler
        prepareWordSetRecycler(recyclerView, wordSetList)
        initRecyclerSwiper(recyclerView)

        etWordSetSearchField = fragmentWordSetBinding.etWsFragSearchfield
        etWordSetSearchField.hint = getString(R.string.word_set_search_field_hint)
        addTextWatcher(etWordSetSearchField)

        btnClearSearchField = fragmentWordSetBinding.btnSearchClose
        showBtnClear(btnClearSearchField, false)
        btnClearSearchField.setOnClickListener {
            if (ivBtnClearIsShownAndEnabled) etWordSetSearchField.text = null
            etWordSetSearchField.hint = getString(R.string.word_set_search_field_hint)
        }

        btnAddNewWordSet = fragmentWordSetBinding.ivWSFragAddIcon
        btnAddNewWordSet.setOnClickListener {
            showAddWordSetAlertDialog()
        }

    }

    private fun showBtnClear(imageView: ImageView, showAndEnable: Boolean) {
        imageView.visibility = if (showAndEnable) View.VISIBLE else View.GONE
        ivBtnClearIsShownAndEnabled = showAndEnable
    }

    private fun addTextWatcher(targetEditText: EditText) {
        targetEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val isStringEmpty = s.toString().isNotEmpty()
                showBtnClear(imageView = btnClearSearchField, showAndEnable = isStringEmpty)
                filter(s.toString())
            }
        })
    }


    private fun prepareWordSetRecycler(
        targetRecyclerView: RecyclerView,
        targetWordSetList: MutableList<WordSet>,
    ) {
        wordSetAdapter = WordSetAdapter(this)
        wordSetAdapter.submitList(targetWordSetList)
        val horizontalDivider = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        horizontalDivider.setDrawable(resources.getDrawable(R.drawable.divider_drawable, null))

        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = wordSetAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(horizontalDivider)
        }
    }


    private fun packWordSetList(targetWordSetList: MutableList<WordSet>) {
        targetWordSetList.clear()
        val dummyDescription = getString(R.string.dummy_text)
        var wordSetNameCount = 0
        repeat(20) {
            wordSetNameCount++
            targetWordSetList.add(
                WordSet(
                    name = "WordSet $wordSetNameCount",
                    description = dummyDescription
                )
            )
        }

        favoriteWordSet = WordSet(
            isFavorites = true,
            name = "Избранный набор",
            description = "Сюда попадают избранные Вами пары из других наборов"
        )
        targetWordSetList.add(0, favoriteWordSet)

        targetWordSetList.add(1, getTimeWordSet())
    }


    private fun initRecyclerSwiper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder.bindingAdapterPosition == 0) return 0 // for favorite wordSet
                return super.getMovementFlags(recyclerView, viewHolder)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                val isFavoriteWordSet = (fromPosition == 0 || toPosition == 0)

                if (!isFavoriteWordSet) {
                    Collections.swap(wordSetList, fromPosition, toPosition)
                    wordSetAdapter.notifyItemMoved(fromPosition, toPosition)
                }
                return isFavoriteWordSet
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        wordSetAdapter.notifyDataSetChanged()
                        showRemoveAlertDialog(position)
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showAddWordSetAlertDialog() {
        val addWordSetDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val layoutInflater = this.layoutInflater
        val addWordSetDialogView = layoutInflater.inflate(R.layout.dialog_add_word_set, null, false)
        addWordSetDialogBuilder.setView(addWordSetDialogView)
        val addWordSetDialog = addWordSetDialogBuilder.create()
        var inputWordSetName = ""
        var inputWordSetDetails = ""

        dialogAddWordSetBinding = DialogAddWordSetBinding.bind(addWordSetDialogView)
        dialogAddWordSetBinding.tvAddWordSetDialogTitle.text =
            getString(R.string.add_word_set_dialog_title)
        dialogAddWordSetBinding.etNewWordSetName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                inputWordSetName = s.toString()
            }
        })
        dialogAddWordSetBinding.etNewWordSetDetails.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                inputWordSetDetails = s.toString()
            }

        })

        dialogAddWordSetBinding.btnAddWordSet.setOnClickListener {
            if (inputWordSetName.isBlank()) {
                dialogAddWordSetBinding.etNewWordSetName.hint =
                    "Название набора не может быть пустым"
                dialogAddWordSetBinding.etNewWordSetName.setHintTextColor(
                    resources.getColor(
                        R.color.plt_error_red,
                        null
                    )
                )
            } else {
                if (inputWordSetDetails.isBlank()) {
                    inputWordSetDetails = "Без описания"
                }
                wordSetList.add(
                    1,
                    WordSet(
                        name = inputWordSetName.capitalize(),
                        description = inputWordSetDetails.capitalize()
                    )
                )
                wordSetAdapter.notifyItemInserted(1)
                addWordSetDialog.dismiss()
            }

        }

        dialogAddWordSetBinding.btnCancelAddWordSet.setOnClickListener {
            addWordSetDialog.dismiss()
        }

        addWordSetDialog.show()
    }

    override fun showRemoveAlertDialog(position: Int) {
        val removeDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val layoutInflater = this.layoutInflater
        val removeDialogView = layoutInflater.inflate(R.layout.dialog_remove_item, null)
        removeDialogBuilder.setView(removeDialogView)
        val removeDialog = removeDialogBuilder.create()
        val chosenWordSet = wordSetList[position]

        dialogRemoveWordSetBinding = DialogRemoveItemBinding.bind(removeDialogView)

        dialogRemoveWordSetBinding.tvRemoveDialogTitle.text = chosenWordSet.name
        dialogRemoveWordSetBinding.tvRemoveDialogMessage.text =
            getString(R.string.remove_dialog_warning)
        dialogRemoveWordSetBinding.btnRemoveWordSet.setOnClickListener {
            wordSetAdapter.removeItem(position)
            removeDialog.dismiss()
        }
        dialogRemoveWordSetBinding.btnCancelRemove.setOnClickListener {
            wordSetAdapter.notifyDataSetChanged()
            removeDialog.dismiss()
        }

        removeDialog.show()
    }


    private fun filter(text: String) {
        val filteredList: MutableList<WordSet> = mutableListOf()
        for (item in wordSetList) {
            if (item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                item.description.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        wordSetAdapter.filterList(filteredList)
    }

    override fun clickToEditor(wordSet: WordSet) {
        //todo: startActivityForResult -> into editor and back
        val toEditorIntent = Intent(activity, WsEditorActivity::class.java)
        toEditorIntent.putExtra(TO_EDITOR_SELECTED_WORD_SET, wordSet)
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
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }


}