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
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.WsEditorActivity
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.databinding.FragmentWordSetBinding
import ru.arcadudu.danatest_v030.interfaces.TransferToEditor
import ru.arcadudu.danatest_v030.models.WordSet
import ru.arcadudu.danatest_v030.utils.getTimeWordSet
import java.util.*
import kotlin.collections.ArrayList

private lateinit var wordSetAdapter: WordSetAdapter
private var wordSetList: MutableList<WordSet> = mutableListOf()
private lateinit var favoriteWordSet: WordSet
private lateinit var recyclerView: RecyclerView
private lateinit var etSearchField: EditText
private lateinit var btnAddNewWordSet: ImageView
private lateinit var btnClearSearchField: ImageView

private const val TAG = "fragment"

//view binding
private lateinit var binding: FragmentWordSetBinding

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
        binding = FragmentWordSetBinding.bind(view)
        ///////////////////////////////////////////////


        packList()

        recyclerView = binding.wordSetRecycler
        recyclerView
            .apply {
                wordSetAdapter = WordSetAdapter(this@WordSetFragment)
                adapter = wordSetAdapter
                wordSetAdapter.submitList(wordSetList)
                layoutManager = LinearLayoutManager(activity)

                val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                divider.setDrawable(
                    resources.getDrawable(
                        R.drawable.divider_drawable,
                        activity?.theme
                    )
                )
                addItemDecoration(divider)

            }

        initSwiper(recyclerView)


        etSearchField = binding.etWsFragSearchfield
        btnClearSearchField = binding.btnSearchClose
        btnClearSearchField.apply {
            visibility = View.GONE
            setOnClickListener {
                if (visibility == View.VISIBLE) {
                    etSearchField.setText("")
                }
            }
        }
        etSearchField.hint = "Поиск набора"
        etSearchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    btnClearSearchField.apply {
                        visibility = View.VISIBLE
                        isEnabled = true
                    }
                } else {
                    btnClearSearchField.apply {
                        visibility = View.GONE
                        isEnabled = false
                    }
                }
                filter(s.toString())
            }
        })


        var added = 0
        btnAddNewWordSet = binding.ivWSFragAddIcon
        btnAddNewWordSet.setOnClickListener {
//            todo: open modal window for adding new wordSet
            added++
//            Toast.makeText(activity, "you added new wordSet", Toast.LENGTH_SHORT).show()
            wordSetList.add(
                1, getTimeWordSet()
//                WordSet(
//                    name = "new Item $added",
//                    description = "This is an item tt you added pushing plus button"
//                )
            )
            wordSetAdapter.notifyItemInserted(1)
            val layoutManager = recyclerView.layoutManager
            layoutManager?.scrollToPosition(0)
        }


    }


    private fun initSwiper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder?.adapterPosition == 0) return 0
                return super.getMovementFlags(recyclerView, viewHolder)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition

                return if (fromPosition != 0 && toPosition != 0) {
                    Collections.swap(wordSetList, fromPosition, toPosition)
                    wordSetAdapter.notifyItemMoved(fromPosition, toPosition)
                    val layoutManager = recyclerView.layoutManager
                    layoutManager?.scrollToPosition(toPosition)
                    true
                } else {
                    false
                }
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
        val chosenItem: WordSet = wordSetList[position]
        val chosenItemName = chosenItem.name
        val builder = AlertDialog.Builder(context)
        builder.apply {
            val itemName = wordSetList[position].name
            setTitle("Удаление набора")
            setMessage("Вы действительно хотите удалить $itemName\nиз коллекции?")

            setPositiveButton("Удалить", DialogInterface.OnClickListener { _, _ ->
                wordSetAdapter.removeItem(position)
                Snackbar.make(
                    recyclerView,
                    "$chosenItemName удалено",
                    3000
                ).setBackgroundTint(resources.getColor(R.color.plt_active_blue, activity?.theme))
                    .setAction("Отмена", View.OnClickListener {
                        wordSetList.add(position, chosenItem)
                        wordSetAdapter.notifyItemInserted(position)
                    })
                    .show()

            })

            setNegativeButton("Отмена", DialogInterface.OnClickListener { _, _ ->
                wordSetAdapter.notifyDataSetChanged()
            })


        }


        val dialog = builder.create()
        dialog.show()

        val btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        btnCancel.apply {
//            textSize = resources.getDimension(R.dimen.dialog_button_text_size)
            setTextAppearance(R.style.MaterialAlertDialog_MaterialComponents_Title_Text)

        }

        val btnOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        btnOk.apply {
//            textSize = resources.getDimension(R.dimen.dialog_button_text_size)
            setTextAppearance(R.style.MaterialAlertDialog_MaterialComponents_Title_Text)
        }


    }

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

    private fun packList() {
        val string = getString(R.string.dummy_text)
        favoriteWordSet = WordSet(
            isFavorites = true,
            name = "Избранный набор",
            description = "Сюда попадают избранные Вами пары из других наборов"
        )

        wordSetList = mutableListOf()
        wordSetList.add(0, favoriteWordSet)
        for (i in 1..20) wordSetList.add(WordSet(name = "WordSet $i", description = string))
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


}