package ru.arcadudu.danatest_v030.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.WordSetAdapter
import ru.arcadudu.danatest_v030.databinding.DialogRemoveWordSetBinding
import ru.arcadudu.danatest_v030.fragments.WordSetFragment
import ru.arcadudu.danatest_v030.interfaces.RemovableItem
import ru.arcadudu.danatest_v030.models.WordSet

private lateinit var dialogRemoveWordSetBinding: DialogRemoveWordSetBinding

private lateinit var tvDialogMessage: TextView
private lateinit var btnRemoveItem: Button
private lateinit var btnCancelRemove: Button
private lateinit var removableItem: RemovableItem

class RemoveWordSetDialog : DialogFragment() {

    init {
        removableItem = WordSetFragment()
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.dialog_remove_word_set, null)
        dialogRemoveWordSetBinding = DialogRemoveWordSetBinding.bind(dialogView)

        tvDialogMessage = dialogRemoveWordSetBinding.dialogMessage
        btnRemoveItem = dialogRemoveWordSetBinding.btnRemoveWordSet
        btnCancelRemove = dialogRemoveWordSetBinding.btnCancelRemove
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val removeDialog = AlertDialog.Builder(context).create()





        btnCancelRemove.setOnClickListener {
            removeDialog.dismiss()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    fun setDialogMessage(itemName: String) {
        val removeMessageText = "Вы действительно хотите удалить $itemName ?"
        tvDialogMessage.text = removeMessageText
    }

    fun removeItem(position: Int, itemList: MutableList<WordSet>, adapter: WordSetAdapter) {
        itemList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }


}