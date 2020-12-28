package ru.arcadudu.danatest_v030.activities.tests

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.*
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairSelectorAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityShuffleTranslateBinding
import ru.arcadudu.danatest_v030.interfaces.IProgress
import ru.arcadudu.danatest_v030.interfaces.OnSnapPositionChangeListener
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import ru.arcadudu.danatest_v030.utils.attachSnapHelperWithListener
import ru.arcadudu.danatest_v030.utils.getFakeWordSet

private lateinit var binding: ActivityShuffleTranslateBinding

private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var progressBar: ProgressBar
private lateinit var ivMakeFav: ImageView
private lateinit var etAnswer: EditText
private lateinit var ivDoneBtn: ImageView

private lateinit var recycler: RecyclerView
private lateinit var layoutManager: LinearLayoutManager
private lateinit var pagerSnapHelper: PagerSnapHelper
private lateinit var snapView: View


private lateinit var myAdapter: PairSelectorAdapter

private lateinit var wordSet: WordSet
private lateinit var pairList: MutableList<Pair>

class TranslateActivity : AppCompatActivity(), IProgress,
    OnSnapPositionChangeListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShuffleTranslateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //getting chosen wordSet + extracting pairList
        wordSet = getFakeWordSet()
        pairList = wordSet.getPairList()


        Log.d("progress", "onCreate: fakeWordset pairList = ${wordSet.getPairList()}")


        progressBar = binding.progressHorizontal
        Log.d("progress", "onCreate: pairListSize = ${wordSet.pairListSize}")


        toolbar = binding.testToolbar
        toolbar.apply {
            setSupportActionBar(this)
            title = getString(R.string.translate)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navigationIcon = resources.getDrawable(R.drawable.icon_arrow_back_blue, theme)
            setNavigationOnClickListener {
                onBackPressed()
            }
        }


        etAnswer = binding.etAnswer
        ivDoneBtn = binding.ivDoneBtn
        ivDoneBtn.visibility = View.GONE
        ivDoneBtn.isEnabled = false
        var contains = false
        ivDoneBtn.setOnClickListener {
            if (it.visibility == View.VISIBLE && it.isEnabled) {
                when (contains) {
                    true -> Toast.makeText(this, "Неприемлимые символы", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        etAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                ivDoneBtn.visibility = if (s.toString().isNotEmpty()) View.VISIBLE else View.GONE
                if (s.toString().isNotEmpty()) {
                    ivDoneBtn.visibility = View.VISIBLE
                    ivDoneBtn.isEnabled = true
                    contains = if (s.toString().toLowerCase().contains(" ")) {
                        ivDoneBtn.setImageDrawable(
                            resources.getDrawable(
                                R.drawable.icon_done_error_red,
                                theme
                            )
                        )
                        true
                    } else {
                        ivDoneBtn.setImageDrawable(resources.getDrawable(R.drawable.icon_done_active_blue))
                        false

                    }
                } else {
                    ivDoneBtn.visibility = View.GONE
                    ivDoneBtn.isEnabled = false

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        recycler = binding.questRecyclerview
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)



        recycler.apply {
            myAdapter = PairSelectorAdapter()
            myAdapter.submitData(pairList, context)
            adapter = myAdapter
            layoutManager =
                LinearLayoutManager(this@TranslateActivity, LinearLayoutManager.HORIZONTAL, false)

            pagerSnapHelper = PagerSnapHelper()
            attachSnapHelperWithListener(
                snapHelper = pagerSnapHelper,
                onSnapPositionChangeListener = this@TranslateActivity
            )

        }


    }

    private fun setToolbarSubtitle(toolbar: Toolbar, position: Int) {
        val subtitle = "${wordSet.name} - $position/${pairList.count()}"
        toolbar.subtitle = subtitle
    }

    private fun loggingSnapPairs(position: Int) {
        Log.d("snap", "loggingSnapPairs: ${pairList[position].toString()} ")
    }

    fun setHint(editText: EditText, position: Int){
        val pair:Pair = pairList[position]
        editText.hint = pair.value
    }

    override fun setTestProgress(done: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", done)
            .setDuration(400).start()
        progressBar.progress = done
        Log.d("progress", "setTestProgress: done = $done")
    }



    override fun onSnapPositionChange(position: Int) {
        Log.d("snap", "onSnapPositionChange: $position")
        setToolbarSubtitle(toolbar, position + 1)
        loggingSnapPairs(position)
//        setHint(editText = etAnswer, position = position)
    }


}