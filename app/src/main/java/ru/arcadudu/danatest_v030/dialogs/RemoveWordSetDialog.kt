package ru.arcadudu.danatest_v030.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class RemoveWordSetDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
////            val builder = AlertDialog.Builder(it)
////            builder.setMessage("Удалить набор .... ?")
////                .setPositiveButton("Ок", DialogInterface.OnClickListener{dialog, id ->
////
////                }
//        }
        return super.onCreateDialog(savedInstanceState)
    }
}