package ru.arcadudu.danatest_v030.activities.tests

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import ru.arcadudu.danatest_v030.adapters.VariantsCardAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityVariantsBinding
import ru.arcadudu.danatest_v030.interfaces.ICheckWord
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import ru.arcadudu.danatest_v030.utils.getWordSet

//private lateinit var binding: ActivityShuffleTranslateBinding
private lateinit var binding: ActivityVariantsBinding
private lateinit var recyclerView: RecyclerView
private lateinit var myAdapter: VariantsCardAdapter

private lateinit var wordSet: WordSet
private lateinit var pairList: MutableList<Pair>

class VariantsActivity : AppCompatActivity() , ICheckWord{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVariantsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        wordSet = getWordSet()
        pairList = wordSet.getPairList()

        recyclerView = binding.varsRecycler
        recyclerView.apply {
            myAdapter = VariantsCardAdapter()
            myAdapter.submitData(wordSet)
            adapter = myAdapter
            layoutManager =
                LinearLayoutManager(this@VariantsActivity, LinearLayoutManager.HORIZONTAL, false)

            PagerSnapHelper().attachToRecyclerView(this)
        }


    }

    override fun checkWord(word: String) {
//        Toast.makeText(this@VariantsActivity, "word is $word", Toast.LENGTH_SHORT).show()
        Log.d("radio", "activity: word is $word ")
    }
}