package ru.arcadudu.danatest_v030.test.testTranslate

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.FragmentTestTranslateBinding
import ru.arcadudu.danatest_v030.interfaces.OnSnapPositionChangeListener
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.TestActivityView
import ru.arcadudu.danatest_v030.utils.attachSnapHelperWithListener
import java.util.*

private const val PAIR_SET_TO_TEST_TAG = "wordSetToTestTag"

class TranslateFragment : MvpAppCompatFragment(), TranslateFragmentView,
    OnSnapPositionChangeListener {

    companion object {
        fun getTranslateFragmentInstance(args: Bundle?): TranslateFragment =
            TranslateFragment().apply { arguments = args }
    }

    @InjectPresenter
    lateinit var translatePresenter: TranslateFragmentPresenter

    private lateinit var translateBinding: FragmentTestTranslateBinding

    private lateinit var toolbar: Toolbar
    private lateinit var questRecycler: RecyclerView
    private lateinit var translateAdapter: TranslateTestAdapter
    private lateinit var etAnswerField: EditText
    private lateinit var btnConfirmAnswer: ImageView
    private lateinit var progressBar:ProgressBar

    private lateinit var incomingPairSet: PairSet

    private var currentSnapPosition = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test_translate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        translateBinding = FragmentTestTranslateBinding.bind(view)

        incomingPairSet = arguments?.getSerializable("pairSet") as PairSet
        translatePresenter.obtainTestedPairSet(incomingPairSet)

        toolbar = translateBinding.translateToolbar
        prepareToolbar(targetToolbar = toolbar)

        questRecycler = translateBinding.translateQuestRecycler
        prepareRecycler(targetRecycler = questRecycler)

        etAnswerField = translateBinding.etTranslateFragmentAnswerField
        btnConfirmAnswer = translateBinding.ivConfirmAnswer
        addTextWatcher(etAnswerField)
        btnConfirmAnswer.setOnClickListener {
            val answer = etAnswerField.text.toString().trim().toLowerCase(Locale.ROOT)
            val answerPosition = currentSnapPosition
            etAnswerField.text = null
            translatePresenter.checkAnswerAndDismiss(
                answer = answer,
                answerPosition = answerPosition
            )
            //todo presenter onProgressChange()
        }

        progressBar = translateBinding.translateTestProgressbar
        translatePresenter.getProgressMax()


    }

    //override fun setTestProgress(done: Int) {
    //        ObjectAnimator.ofInt(progressBar, "progress", done)
    //            .setDuration(400).start()
    //        progressBar.progress = done
    //        Log.d("progress", "setTestProgress: done = $done")
    //    }


    private fun showConfirmButton(answerIsNotEmpty: Boolean) {
        btnConfirmAnswer.visibility = if (answerIsNotEmpty) View.VISIBLE else View.GONE
    }

    private fun prepareToolbar(targetToolbar: Toolbar) {
        targetToolbar.apply {
            navigationIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.icon_arrow_back_violet_light,
                null
            )
            this.setNavigationOnClickListener {
                (activity as? TestActivityView)?.onFragmentBackPressed()
            }
        }
        translatePresenter.provideDataForToolbar()
    }


    private fun prepareRecycler(targetRecycler: RecyclerView) {
        translateAdapter = TranslateTestAdapter()
        translateAdapter.translateAdapterCallback(this)
        targetRecycler.apply {
            setHasFixedSize(true)
            adapter = translateAdapter
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            attachSnapHelperWithListener(
                snapHelper = PagerSnapHelper(),
                onSnapPositionChangeListener = this@TranslateFragment
            )
        }

        translatePresenter.providePairSetList()
    }

    private fun addTextWatcher(targetEditText: EditText) {
        targetEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                showConfirmButton(s.toString().isNotEmpty())
            }
        })
    }

    override fun onSnapPositionChange(position: Int) {
        currentSnapPosition = position
    }


    override fun getPairSetName(pairSetName: String) {

    }

    override fun updateToolbar(
        testedPairSetName: String,
        answeredCount: Int,
        pairListOriginalCount: Int
    ) {
        toolbar.subtitle = "$testedPairSetName  $answeredCount/$pairListOriginalCount"
    }

    override fun initPairList(testedPairList: MutableList<Pair>) {
        translateAdapter.apply {
            submitData(testedPairList)
            notifyDataSetChanged()
        }
    }



    override fun updateRecyclerOnRemoved(
        updatedPairList: MutableList<Pair>,
        answerPosition: Int
    ) {
        translateAdapter.submitData(updatedPairList)
        translateAdapter.notifyItemRemoved(answerPosition)
    }

    override fun updateAnsweredProgress(answeredPairCount: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", answeredPairCount)
            .setDuration(320).start()
        progressBar.progress = answeredPairCount
    }

    override fun setProgressMax(originalPairListCount: Int) {
        progressBar.max = originalPairListCount
    }

    override fun toResultFragment(backUpPairSet: PairSet, mistakeCount: Int) {
        (activity as? TestActivityView)?.onTestReadyForResult(backUpPairSet, mistakeCount)
    }


}