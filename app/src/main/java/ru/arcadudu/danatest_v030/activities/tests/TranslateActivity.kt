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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.adapters.PairSelectorAdapter
import ru.arcadudu.danatest_v030.databinding.ActivityShuffleTranslateBinding
import ru.arcadudu.danatest_v030.interfaces.IProgress
import ru.arcadudu.danatest_v030.interfaces.OnSnapPositionChangeListener
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.WordSet
import ru.arcadudu.danatest_v030.utils.attachSnapHelperWithListener
import ru.arcadudu.danatest_v030.utils.getTimeWordSet
import java.util.*

private lateinit var translateActivityBinding: ActivityShuffleTranslateBinding

private lateinit var toolbar: Toolbar
private lateinit var progressBar: ProgressBar
private lateinit var ivMakePairFavorite: ImageView
private lateinit var etAnswerField: EditText
private lateinit var ivDoneBtn: ImageView

private lateinit var questRecyclerView: RecyclerView
private lateinit var pairSelectorLayoutManager: LinearLayoutManager
private lateinit var pagerSnapHelper: PagerSnapHelper
private lateinit var pairSelectorAdapter: PairSelectorAdapter

private lateinit var currentWordSet: WordSet
private lateinit var currentPairList: MutableList<Pair>

private var currentSnapPosition = 0
private var ivDoneBtnIsShownAndEnabled = false
private var answerContainsForbiddenLetters = false

class TranslateActivity : AppCompatActivity(), IProgress,
    OnSnapPositionChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        translateActivityBinding = ActivityShuffleTranslateBinding.inflate(layoutInflater)
        val view = translateActivityBinding.root
        setContentView(view)

        //getting chosen wordSet + extracting pairList
        currentWordSet = getTimeWordSet()
        currentPairList = currentWordSet.getPairList()

        progressBar = translateActivityBinding.progressHorizontal

        toolbar = translateActivityBinding.testToolbar
        prepareToolbar(toolbar)

        questRecyclerView = translateActivityBinding.questRecyclerview
        prepareRecyclerView(questRecyclerView, currentPairList)

        etAnswerField = translateActivityBinding.etAnswer
        addTextWatcher(etAnswerField)

        ivDoneBtn = translateActivityBinding.ivDoneBtn
        showIvDoneBtn(imageView = ivDoneBtn, showAndEnable = false)
        ivDoneBtn.setOnClickListener {
            if (ivDoneBtnIsShownAndEnabled) {
                when (answerContainsForbiddenLetters) {
                    true -> Toast.makeText(this, "Неприемлимые символы", Toast.LENGTH_SHORT).show()
                    else -> {
                        checkAnswerCorrectness(
                            userAnswer = etAnswerField.text.toString(),
                            currentSnapPosition
                        )
                        setToolbarSubtitle(toolbar, currentSnapPosition + 1)
                    }
                }
            }
        }

        ivMakePairFavorite = translateActivityBinding.ivMakeFav
        ivMakePairFavorite.setOnClickListener {
            Toast.makeText(
                this,
                "${currentPairList[currentSnapPosition].pairKey} added to fav!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun prepareToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setSupportActionBar(this)
            title = getString(R.string.translate)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.icon_arrow_back_blue, theme)
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun prepareRecyclerView(recyclerView: RecyclerView, pairList: MutableList<Pair>) {
        pairSelectorLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        pagerSnapHelper = PagerSnapHelper()
        pairSelectorAdapter = PairSelectorAdapter()
        pairSelectorAdapter.submitData(pairList, this)
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = pairSelectorAdapter
            layoutManager = pairSelectorLayoutManager
            attachSnapHelperWithListener(
                snapHelper = pagerSnapHelper,
                onSnapPositionChangeListener = this@TranslateActivity
            )
        }

    }

    private fun addTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userAnswer = s.toString().toLowerCase(Locale.ROOT)
                if (userAnswer.isNotEmpty()) {
                    showIvDoneBtn(ivDoneBtn, showAndEnable = true)
                    answerContainsForbiddenLetters = checkAnswerForForbiddenLetters(userAnswer)
                    setIvDoneBtnAppearance(answerContainsForbiddenLetters)
                } else {
                    showIvDoneBtn(ivDoneBtn, showAndEnable = false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    fun showIvDoneBtn(imageView: ImageView, showAndEnable: Boolean) {
        imageView.visibility = if(showAndEnable) View.VISIBLE else View.GONE
        imageView.isEnabled = showAndEnable
        ivDoneBtnIsShownAndEnabled = showAndEnable
    }


    private fun setToolbarSubtitle(toolbar: Toolbar, position: Int) {
        val subtitle = "${currentWordSet.name} - $position/${currentPairList.count()}"
        toolbar.subtitle = subtitle
    }

    private fun loggingSnapPairs(position: Int) {
        Log.d("snap", "loggingSnapPairs: ${currentPairList[position].toString()} ")
    }

    fun setHint(editText: EditText, position: Int) {
        val pair: Pair = currentPairList[position]
        editText.hint = pair.pairValue
    }

    override fun setTestProgress(done: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", done)
            .setDuration(400).start()
        progressBar.progress = done
        Log.d("progress", "setTestProgress: done = $done")
    }

    fun checkAnswerForForbiddenLetters(answer: String): Boolean {
        // here you can define any illegal letters
        val space = " "
        return answer.trim().contains(space)
    }

    private fun checkAnswerCorrectness(userAnswer: String, position: Int): Boolean {
        val pair = currentPairList[position]
        val trueValue = pair.pairKey.trim().toLowerCase(Locale.ROOT)

        val toastText = if (userAnswer.trim()
                .toLowerCase(Locale.ROOT) == trueValue
        ) "Верно!" else "Ошибка!! $trueValue"


        Snackbar.make(
            questRecyclerView,
            toastText,
            3000
        ).setBackgroundTint(resources.getColor(R.color.plt_active_blue, theme))
            .show()

        currentPairList.removeAt(position)
        pairSelectorAdapter.notifyItemRemoved(position)
        etAnswerField.text = null
        return userAnswer.trim().toLowerCase(Locale.ROOT) == trueValue
    }

    fun setIvDoneBtnAppearance(answerContainsForbiddenLetters: Boolean) {
        val drawableId =
            if (answerContainsForbiddenLetters) R.drawable.icon_done_error_red else R.drawable.icon_done_active_blue
        ivDoneBtn.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, theme))
    }

    override fun onSnapPositionChange(position: Int) {
        Log.d("snap", "onSnapPositionChange: $position")
        setToolbarSubtitle(toolbar, position + 1)
        loggingSnapPairs(position)
        currentSnapPosition = position

//        setHint(editText = etAnswer, position = position)
    }


}