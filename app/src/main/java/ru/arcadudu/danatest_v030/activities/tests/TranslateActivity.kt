package ru.arcadudu.danatest_v030.activities.tests

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.recyclerview.widget.*
import ru.arcadudu.danatest_v030.adapters.TranslateCardAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityShuffleTranslateBinding
import ru.arcadudu.danatest_v030.interfaces.IProgress
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import ru.arcadudu.danatest_v030.utils.getWordSet

private lateinit var binding: ActivityShuffleTranslateBinding
private lateinit var recyclerView: RecyclerView
private lateinit var testAdapter: TranslateCardAdapter
private lateinit var progressBar: ProgressBar

private lateinit var wordSet:WordSet
private lateinit var pairList:MutableList<Pair>
const val multiply = 1000

class TranslateActivity : AppCompatActivity(), IProgress {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShuffleTranslateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //getting chosen wordSet + extracting pairList
        wordSet = getWordSet()
        pairList = wordSet.getPairList()


        Log.d("progress", "onCreate: fakeWordset pairList = ${wordSet.getPairList()}")


        recyclerView = binding.translateShuffleRecycler
        recyclerView.apply {
            testAdapter = TranslateCardAdapter()
            testAdapter.submitData(wordSet)
            adapter = testAdapter
            layoutManager =
                LinearLayoutManager(this@TranslateActivity, LinearLayoutManager.HORIZONTAL, false)
            PagerSnapHelper().attachToRecyclerView(this)
        }

        progressBar = binding.progressHorizontal
        Log.d("progress", "onCreate: pairListSize = ${wordSet.pairListSize}")
        progressBar.max = pairList.size* multiply
        Log.d("progress", "onCreate: progressbar max = ${progressBar.max} ")


    }

    override fun setTestProgress(done: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", done)
            .setDuration(400).start()

        progressBar.progress = done
        Log.d("progress", "setTestProgress: done = $done")
    }
}