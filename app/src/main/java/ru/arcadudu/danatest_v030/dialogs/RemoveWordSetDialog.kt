package ru.arcadudu.danatest_v030.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.DialogRemoveWordSetBinding
import ru.arcadudu.danatest_v030.fragments.WordSetFragment
import ru.arcadudu.danatest_v030.interfaces.RemovableItem

private lateinit var dialogRemoveWordSetBinding: DialogRemoveWordSetBinding

private lateinit var tvDialogMessage: TextView
private lateinit var btnRemoveItem: Button
private lateinit var btnCancelRemove: Button
private lateinit var removableItem: RemovableItem

class RemoveWordSetDialog : DialogFragment() {



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        removableItem = WordSetFragment()

        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.dialog_remove_word_set, null)
        val removeDialog = AlertDialog.Builder(context).create()
        dialogRemoveWordSetBinding = DialogRemoveWordSetBinding.bind(dialogView)

        tvDialogMessage = dialogRemoveWordSetBinding.dialogMessage
        // todo: transfer class logic into wordSetFragment

        btnRemoveItem = dialogRemoveWordSetBinding.btnRemoveWordSet


        btnCancelRemove = dialogRemoveWordSetBinding.btnCancelRemove
        btnCancelRemove.setOnClickListener{
            removeDialog.dismiss()
        }
        return super.onCreateDialog(savedInstanceState)
    }
}