package ru.arcadudu.danatest_v030.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.WordSet

class WsEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ws_editor)

        val title:TextView = findViewById(R.id.tv_editor_title)
        val details:TextView = findViewById(R.id.editor_details)

        val incomingWordSet: WordSet = intent.getSerializableExtra("selected_wordset") as WordSet

        title.text = incomingWordSet.name
        details.text = incomingWordSet.description


    }
}