package ru.arcadudu.danatest_v030.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.ActivityWsEditorBinding
import ru.arcadudu.danatest_v030.models.WordSet

private lateinit var binding:ActivityWsEditorBinding
private lateinit var btnEdit:ImageView
private lateinit var tvTitle:TextView
private lateinit var tvDetails:TextView

class WsEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWsEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ////////////////////

        tvTitle = binding.tvEditorTitle
        tvDetails = binding.editorDetails
        btnEdit = binding.ivEditorEditDetailsIcon

        val incomingWordSet: WordSet = intent.getSerializableExtra("selected_wordset") as WordSet

        tvTitle.text = incomingWordSet.name
        tvDetails.text = incomingWordSet.description


        if(incomingWordSet.isFavorites){
           btnEdit.apply {
               isEnabled = false
               isVisible = false
           }
            tvTitle.setOnClickListener{
                Snackbar.make(view, "Невозможно изменить название избранного набора", Snackbar.LENGTH_LONG).show()
            }
            tvDetails.setOnClickListener{
                Snackbar.make(view, "Невозможно изменить описание избранного набора", Snackbar.LENGTH_LONG).show()
            }
        }



    }
}