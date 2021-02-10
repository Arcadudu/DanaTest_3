package ru.arcadudu.danatest_v030.alertDialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.Pair

class AddPairDialogFragment(
    var adapter: Adapter<RecyclerView.ViewHolder>,
    private var list: MutableList<Pair>
) :
    DialogFragment() {

    private lateinit var builder: AlertDialog.Builder
    private lateinit var inflater: LayoutInflater
    private var etKey: EditText
    private var etValue: EditText


    init {
        list = mutableListOf()
        val view = inflater.inflate(R.layout.dialog_add_pair_violet, null)
        etKey = view.findViewById(R.id.et_newPairKey)
        etValue = view.findViewById(R.id.et_newPairValue)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("pair", "onCreateDialog: creating dialog from class")
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            builder = AlertDialog.Builder(it)
            inflater = LayoutInflater.from(activity)
            builder.apply {
                setView(view)
                setTitle("Новая пара")
                setPositiveButton("Добавить", DialogInterface.OnClickListener { _, _ ->
                    val keyString = etKey.text.toString().trim()
                    val valueString = etValue.text.toString().trim()
                    Log.d("pair", "onCreateDialog: keyString is null? ${keyString.isBlank()}")
                    Log.d("pair", "onCreateDialog: valueString is null? ${valueString.isBlank()} ")
                    list.add(0, Pair(keyString, valueString))
                    adapter.notifyItemInserted(0)
                    adapter.notifyDataSetChanged()

                })
                setNegativeButton("Отмена", DialogInterface.OnClickListener { _, _ ->
                    adapter.notifyDataSetChanged()
                    dialog?.cancel()
                })
            }
            Log.d("pair", "onCreateDialog: builder is ready ")
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}


