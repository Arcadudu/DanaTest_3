package ru.arcadudu.danatest_v030.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.WsEditorActivity
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.databinding.DialogRemoveWordSetBinding
import ru.arcadudu.danatest_v030.databinding.FragmentWordSetBinding
import ru.arcadudu.danatest_v030.interfaces.RemovableItem
import ru.arcadudu.danatest_v030.interfaces.TransferToEditor
import ru.arcadudu.danatest_v030.models.WordSet
import ru.arcadudu.danatest_v030.utils.getDummyWordSet
import java.util.*
import kotlin.collections.ArrayList

private lateinit var wordSetAdapter: WordSetAdapter
private var wordSetList: MutableList<WordSet> = mutableListOf()
private lateinit var favoriteWordSet: WordSet
private lateinit var recyclerView: RecyclerView
private lateinit var etSearchField: EditText
private lateinit var btnAddNewWordSet: ImageView
private lateinit var btnClearSearchField: ImageView

private var ivBtnClearIsShownAndEnabled = false
private const val TAG = "fragment"


private lateinit var fragmentWordSetBinding: FragmentWordSetBinding
private lateinit var dialogRemoveWordSetBinding: DialogRemoveWordSetBinding

class WordSetFragment : Fragment(), TransferToEditor, WordSetAdapter.OnItemSwipedListener,
    RemovableItem {

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

        etSearchField = fragmentWordSetBinding.etWsFragSearchfield
        etSearchField.hint = getString(R.string.wordset_search_field_hint)
        addTextWatcher(etSearchField)

        btnClearSearchField = fragmentWordSetBinding.btnSearchClose
        showBtnClear(btnClearSearchField, false)
        btnClearSearchField.setOnClickListener {
            if (ivBtnClearIsShownAndEnabled) {
                etSearchField.text = null
            }
            etSearchField.hint = getString(R.string.wordset_search_field_hint)
        }

        btnAddNewWordSet = fragmentWordSetBinding.ivWSFragAddIcon
        btnAddNewWordSet.setOnClickListener {
            /*todo: open modal dialog for adding new WordSet
            *  when dialog openes adapters first scrolls to 0 position*/

//            wordSetList.add(
//                1, getTimeWordSet()
//            )
//            wordSetAdapter.notifyItemInserted(1)

            recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun showBtnClear(imageView: ImageView, showAndEnable: Boolean) {
        imageView.visibility = if (showAndEnable) View.VISIBLE else View.GONE
        imageView.isEnabled = showAndEnable
        ivBtnClearIsShownAndEnabled = showAndEnable


    }

    private fun addTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val isStringIsEmpty = s.toString().isNotEmpty()
                showBtnClear(imageView = btnClearSearchField, showAndEnable = isStringIsEmpty)
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
        val divider = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_drawable, null))

        targetRecyclerView.apply {
            setHasFixedSize(true)
            adapter = wordSetAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(divider)
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

        targetWordSetList.add(1, getDummyWordSet())
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
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        showRemoveAlertDialog(position)
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun showRemoveAlertDialog(position: Int) {
        val removeDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val layoutInflater = this.layoutInflater
        val removeDialogView = layoutInflater.inflate(R.layout.dialog_remove_word_set, null)
        removeDialogBuilder.setView(removeDialogView)
        val removeDialog = removeDialogBuilder.create()
        val chosenWordSet = wordSetList[position]

        dialogRemoveWordSetBinding = DialogRemoveWordSetBinding.bind(removeDialogView)

        dialogRemoveWordSetBinding.dialogMessage.text =
            "Вы действительно хотите\nудалить ${chosenWordSet.name} ?"

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

    //    override fun showRemoveAlertDialog(position: Int) {
//        val chosenItem: WordSet = wordSetList[position]
//        val chosenItemName = chosenItem.name
//        val dialogBuilder = AlertDialog.Builder(context)
//        dialogBuilder.apply {
//            val itemName = wordSetList[position].name
//            setTitle("Удаление набора")
//            setMessage("Вы действительно хотите удалить $itemName\nиз коллекции?")
//
//            setPositiveButton("Удалить", DialogInterface.OnClickListener { _, _ ->
//                wordSetAdapter.removeItem(position)
//                Snackbar.make(
//                    recyclerView,
//                    "$chosenItemName удалено",
//                    3000
//                ).setBackgroundTint(resources.getColor(R.color.plt_active_blue, activity?.theme))
//                    .setAction("Отмена", View.OnClickListener {
//                        wordSetList.add(position, chosenItem)
//                        wordSetAdapter.notifyItemInserted(position)
//                    })
//                    .show()
//
//            })
//
//            setNegativeButton("Отмена", DialogInterface.OnClickListener { _, _ ->
//                wordSetAdapter.notifyDataSetChanged()
//            })
//
//
//        }
//
//        val removeAlertDialog = dialogBuilder.create()
//        removeAlertDialog.show()
//
//        val btnCancel = removeAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//        btnCancel.apply {
////            textSize = resources.getDimension(R.dimen.dialog_button_text_size)
//            setTextAppearance(R.style.MaterialAlertDialog_MaterialComponents_Title_Text)
//
//        }
//
//        val btnOk = removeAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
//        btnOk.apply {
////            textSize = resources.getDimension(R.dimen.dialog_button_text_size)
//            setTextAppearance(R.style.MaterialAlertDialog_MaterialComponents_Title_Text)
//        }
//
//
//    }

    private fun filter(text: String) {
        val filteredList: MutableList<WordSet> = mutableListOf()
        for (item in wordSetList) {
            if (item.name.toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT)) || item.description.toLowerCase(Locale.ROOT)
                    .contains(text.toLowerCase(Locale.ROOT))
            ) {
                filteredList.add(item)
            }
        }
        wordSetAdapter.filterList(filteredList)
    }

    override fun clickToEditor(wordSet: WordSet) {
        //todo: startActivityForResult -> into editor and back
        val intent = Intent(activity, WsEditorActivity::class.java)
        var bundle = Bundle()
        intent.putExtra("key", ArrayList(wordSetList))
        intent.putExtra("selected_wordset", wordSet)
        startActivity(intent)
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

    override fun obtainRemovableItemName(itemName: String) {
        TODO("Not yet implemented")
    }

    override fun removeRemovableItem(wordSet: WordSet) {
        TODO("Not yet implemented")
    }


}