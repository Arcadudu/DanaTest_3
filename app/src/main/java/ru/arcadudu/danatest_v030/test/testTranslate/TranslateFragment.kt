package ru.arcadudu.danatest_v030.test.testTranslate

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
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

    private lateinit var toolbar: MaterialToolbar
    private lateinit var questRecycler: RecyclerView
    private lateinit var translateAdapter: TranslateTestAdapter
    private lateinit var etAnswerInputLayout: TextInputLayout
    private lateinit var etAnswerField: TextInputEditText
    private lateinit var btnConfirmAnswer: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var translateSnapHelper: PagerSnapHelper

    private lateinit var incomingPairSet: PairSet

    private var currentSnapPosition = 0
    private var snapHelperAttached = false


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

        etAnswerInputLayout = translateBinding.etTranslateFragmentAnswerField
        etAnswerField = etAnswerInputLayout.editText as TextInputEditText
        btnConfirmAnswer = translateBinding.ivConfirmAnswer

        etAnswerField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                showConfirmButton(s.toString().isNotEmpty())
            }
        })

        btnConfirmAnswer.setOnClickListener {
            val answer = etAnswerField.text.toString().trim().toLowerCase(Locale.ROOT)
            val answerPosition = currentSnapPosition
            etAnswerField.text = null
            translatePresenter.checkAnswerAndDismiss(
                inputAnswer = answer,
                answerPosition = answerPosition
            )
            //todo presenter onProgressChange()
        }

        progressBar = translateBinding.translateTestProgressbar
        translatePresenter.getProgressMax()


    }


    private fun showConfirmButton(answerIsNotEmpty: Boolean) {
        btnConfirmAnswer.visibility = if (answerIsNotEmpty) View.VISIBLE else View.GONE
    }

    private fun prepareToolbar(targetToolbar: Toolbar) {
        targetToolbar.apply {
            inflateMenu(R.menu.test_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.exit_test -> {
                        Log.d("toolbar", "onOptionsItemSelected: exit pressed ")
                        (activity as? TestActivityView)?.onFragmentBackPressed()
                        true
                    }

                    R.id.refresh_test -> {

                        translatePresenter.onRestartButton()
                        true
                    }
                    else -> false
                }
            }
        }
        translatePresenter.provideDataForToolbar()
    }


    private fun prepareRecycler(targetRecycler: RecyclerView) {
        translateAdapter = TranslateTestAdapter()
        translateAdapter.translateAdapterCallback(this)
        translateSnapHelper = PagerSnapHelper()
        targetRecycler.apply {
            setHasFixedSize(true)
            adapter = translateAdapter
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            attachSnapHelperWithListener(
                snapHelper = translateSnapHelper,
                onSnapPositionChangeListener = this@TranslateFragment
            )
            scrollBarSize = 5
        }
        snapHelperAttached = true
        translatePresenter.providePairSetList()
    }


    override fun onSnapPositionChange(position: Int) {
        currentSnapPosition = position
    }


    override fun showOnRestartDialog(pairSetName: String) {
        val restartDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val restartDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null)
        restartDialogBuilder.setView(restartDialogView)
        val restartDialog = restartDialogBuilder.create()

        val restartDialogBinding = DialogRemoveItemBinding.bind(restartDialogView)
        restartDialogBinding.tvRemoveDialogTitle.text =
            getString(R.string.dt_restart_test_dialog_title, pairSetName)
        restartDialogBinding.tvRemoveDialogMessage.text =
            getString(R.string.dt_restart_test_dialog_message)

        val btnCancelRestart = restartDialogBinding.btnCancelRemove
        btnCancelRestart.text = getString(R.string.dt_restart_test_dialog_negative_btn)

        val btnRestartTest = restartDialogBinding.btnRemovePair
        btnRestartTest.text = getString(R.string.dt_restart_test_dialog_positive_btn)
        btnRestartTest.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.dt3_brand_violet_100,
                activity?.theme
            )
        )

        btnCancelRestart.setOnClickListener {
            restartDialog.dismiss()
        }

        btnRestartTest.setOnClickListener {
            translatePresenter.restartTranslateTest()
            restartDialog.dismiss()
        }

        restartDialog.show()
    }

    override fun updateToolbar(
        testedPairSetName: String,
        pairListCount: Int,
        pairListOriginalCount: Int
    ) {
        toolbar.subtitle = getString(
            R.string.dt_test_translate_fragment_toolbar_subtitle,
            testedPairSetName.capitalize(Locale.ROOT).trim(),
            pairListCount,
            pairListOriginalCount
        )
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
        translateAdapter.apply {
            submitData(updatedPairList)
            notifyItemRemoved(answerPosition)
        }

    }

    override fun updateAnsweredProgress(answeredPairCount: Int, duration: Long) {
        ObjectAnimator.ofInt(progressBar, "progress", answeredPairCount).setDuration(duration)
            .start()
        progressBar.progress = answeredPairCount
    }

    override fun setProgressMax(originalPairListCount: Int) {
        progressBar.max = originalPairListCount
    }

    override fun toResultFragment(backUpPairSet: PairSet, mistakeCount: Int) {
        (activity as? TestActivityView)?.onTestReadyForResult(backUpPairSet, mistakeCount)
    }

    override fun updateRecyclerOnRestart(testedPairList: MutableList<Pair>) {
        translateAdapter.apply {
            submitData(testedPairList)
            notifyDataSetChanged()
        }
    }

    override fun detachSnapHelperFromRecyclerView() {
        if (snapHelperAttached) {
            translateSnapHelper.attachToRecyclerView(null)
            etAnswerInputLayout.visibility = View.GONE

        } else {
            questRecycler.attachSnapHelperWithListener(
                snapHelper = translateSnapHelper,
                onSnapPositionChangeListener = this@TranslateFragment
            )
            etAnswerInputLayout.visibility = View.VISIBLE
        }
        snapHelperAttached = !snapHelperAttached


    }


}