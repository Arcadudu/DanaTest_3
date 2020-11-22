package ru.arcadudu.danatest_v030.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.WordSet

class EditAddWordSetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add_word_set)

        val title:TextView = findViewById(R.id.chosenTitle)
        val details:TextView = findViewById(R.id.chosenDetails)
        val length:TextView = findViewById(R.id.length)


        val incomingWordSet:WordSet = intent.getSerializableExtra("chosen_item") as WordSet

        title.text = incomingWordSet?.name
        details.text = incomingWordSet?.description
        length.text = incomingWordSet?.listLength.toString()

    }
}