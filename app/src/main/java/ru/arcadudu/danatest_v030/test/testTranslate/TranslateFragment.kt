package ru.arcadudu.danatest_v030.test.testTranslate

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import moxy.MvpAppCompatActivity
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.databinding.DialogTestResultBinding
import ru.arcadudu.danatest_v030.databinding.FragmentTestTranslateBinding
import ru.arcadudu.danatest_v030.interfaces.OnSnapPositionChangeListener
import ru.arcadudu.danatest_v030.interfaces.TestAdapterCallback
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.Pairset
import ru.arcadudu.danatest_v030.test.MistakeListAdapter
import ru.arcadudu.danatest_v030.test.TestActivityView
import ru.arcadudu.danatest_v030.test.TranslateTestAdapter
import ru.arcadudu.danatest_v030.utils.IS_RESULT_DIALOG_RESTORED_ON_RESUME
import ru.arcadudu.danatest_v030.utils.IS_RESULT_DIALOG_SHOWN
import ru.arcadudu.danatest_v030.utils.attachSnapHelperWithListener
import ru.arcadudu.danatest_v030.utils.forceHideKeyboard
import java.util.*


class TranslateFragment : MvpAppCompatFragment(), TranslateFragmentView, TestAdapterCallback,
    OnSnapPositionChangeListener {

    companion object {
        fun getTranslateFragmentInstance(args: Bundle?): TranslateFragment =
            TranslateFragment().apply { arguments = args }
    }

    @InjectPresenter
    lateinit var translatePresenter: TranslateFragmentPresenter

    private lateinit var translateBinding: FragmentTestTranslateBinding

    private lateinit var toolbar: MaterialToolbar
    private lateinit var questTranslateRecycler: RecyclerView
    private lateinit var translateAdapter: TranslateTestAdapter
    private lateinit var etAnswerInputLayout: TextInputLayout
    private lateinit var etAnswerField: TextInputEditText
    private lateinit var btnConfirmAnswer: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPairCounter: MaterialTextView
    private lateinit var btnGiveMeHint: Button

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var mistakeListAdapter: MistakeListAdapter

    /*on default the fragment doesn't allow questCard recycler to scroll,
    * but if scrolling is desired than uncomment the following SnapHelper initialization
    * and attachment in prepareRecycler() method*/

    private lateinit var translateSnapHelper: PagerSnapHelper

    private lateinit var incomingPairset: Pairset

    private var currentSnapPosition = 0
    private var shufflePairset = false
    private var enableHintForPairset = false
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
        translateBinding = FragmentTestTranslateBinding.bind(view)

        sharedPreferences = requireContext().getSharedPreferences(
            IS_RESULT_DIALOG_RESTORED_ON_RESUME, Context.MODE_PRIVATE
        )

        incomingPairset = arguments?.getSerializable("pairSet") as Pairset
        shufflePairset = arguments?.getBoolean("shuffle", false)!!
        enableHintForPairset = arguments?.getBoolean("enableHints", false)!!

        translatePresenter.obtainTestedPairSet(incomingPairset)

        toolbar = translateBinding.translateToolbar
        prepareToolbar(targetToolbar = toolbar)


        btnGiveMeHint = translateBinding.btnGiveMeHintTranslate
        btnGiveMeHint.apply {
            setOnClickListener {
                translatePresenter.provideHintForCurrentPosition(
                    currentSnapPosition
                )
            }
            this.visibility = if (enableHintForPairset) View.VISIBLE else View.GONE
        }

        questTranslateRecycler = translateBinding.translateQuestRecycler
        prepareRecycler(targetRecycler = questTranslateRecycler)

        etAnswerInputLayout = translateBinding.etTranslateFragmentAnswerField

        etAnswerField = etAnswerInputLayout.editText as TextInputEditText
        etAnswerField.apply {
            requestFocus()
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    (activity as? MvpAppCompatActivity)?.forceHideKeyboard(v)
            }
            doOnTextChanged { text, _, _, _ ->
                showConfirmButton(text.toString().isNotEmpty())
            }
        }


        btnConfirmAnswer = translateBinding.ivConfirmAnswer

        tvPairCounter = translateBinding.tvPairDoneAndTotalCounter

        btnConfirmAnswer.setOnClickListener {
            val answer = etAnswerField.text.toString().trim().toLowerCase(Locale.ROOT)
            etAnswerField.text = null
            translatePresenter.checkAnswerAndDismiss(
                inputAnswer = answer,
                answerPosition = currentSnapPosition
            )
        }

        progressBar = translateBinding.translateTestProgressbar
        translatePresenter.getProgressMax()
    }


    private fun showConfirmButton(answerIsNotEmpty: Boolean) {
        btnConfirmAnswer.visibility = if (answerIsNotEmpty) View.VISIBLE else View.GONE
    }

    private fun prepareToolbar(targetToolbar: Toolbar) {
        translatePresenter.getToolbarTitle()
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


    override fun onStop() {
        super.onStop()
        translatePresenter.onTestStop()
        Log.d("testTranslate", "onStop: callback")
    }

    private fun prepareRecycler(targetRecycler: RecyclerView) {
        translateAdapter = TranslateTestAdapter()
        translateAdapter.adapterCallback(this)

        translateSnapHelper = PagerSnapHelper()
        targetRecycler.apply {
            setHasFixedSize(true)
            isHorizontalScrollBarEnabled = false
            adapter = translateAdapter
            layoutManager = LinearLayoutManager(targetRecycler.context, RecyclerView.HORIZONTAL, false)
            attachSnapHelperWithListener(
                snapHelper = translateSnapHelper,
                onSnapPositionChangeListener = this@TranslateFragment
            )
        }
        snapHelperAttached = true

        if (shufflePairset) {
            translatePresenter.provideShuffledPairList()
        } else {
            translatePresenter.provideOrderedPairList()
        }

        HorizontalOverScrollBounceEffectDecorator(RecyclerViewOverScrollDecorAdapter(targetRecycler))
    }

    override fun onSnapPositionChange(position: Int) {
        currentSnapPosition = position
    }

    override fun showOnRestartDialog(pairSetName: String) {
        val restartDialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)
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
                R.color.dt3_brand_100,
                activity?.theme
            )
        )
        btnCancelRestart.setOnClickListener {
            restartDialog.dismiss()
        }
        btnRestartTest.setOnClickListener {
            translatePresenter.restartTranslateTest(shufflePairset)
            restartDialog.dismiss()
        }
        restartDialog.show()
    }

    override fun updateCounterLine(
        testedPairSetName: String,
        pairListCount: Int,
        pairListOriginalCount: Int
    ) {
        tvPairCounter.text = getString(
            R.string.dt_test_translate_fragment_counter_body,
            pairListCount,
            pairListOriginalCount
        ).capitalize(Locale.ROOT)
            .trim()


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

    override fun setToolbarTitle(pairsetTitleForToolbar: String) {
        toolbar.title = pairsetTitleForToolbar
    }

    override fun setProgressMax(originalPairListCount: Int) {
        progressBar.max = originalPairListCount
    }

    override fun toResultFragment(backUpPairset: Pairset, mistakeCount: Int) {
        (activity as? TestActivityView)?.onTestReadyForResult(backUpPairset, mistakeCount)
    }

    override fun updateRecyclerOnRestart(testedPairList: MutableList<Pair>) {
        translateAdapter.apply {
            submitData(testedPairList)
            notifyDataSetChanged()
            onAdapterItemClick()

        }
    }

    override fun getLayoutPosition(layoutPosition: Int) {
        if (!snapHelperAttached) {
            tvPairCounter.text = layoutPosition.toString()
        }
    }

    //method listening to questCardRecycler item touch
    override fun onAdapterItemClick() {
        if (snapHelperAttached) {
            toScrollMode(questTranslateRecycler)
        } else {
            toTestMode(questTranslateRecycler)
        }
    }

    override fun getHintForCurrentPosition(pairKey: String) {
        etAnswerField.setText(pairKey)
    }

    override fun showOnTestResultDialog() {
        val onTestResultDialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)
        val onTestResultDialogView =
            this.layoutInflater.inflate(R.layout.dialog_test_result, null, false)
        onTestResultDialogBuilder.setView(onTestResultDialogView)
        val onTestResultDialog = onTestResultDialogBuilder.create()

        translatePresenter.applyTestPassReward(translatePresenter.provideMistakes(), translatePresenter.provideHintUseCount(), requireContext())

        var dismissedWithAction = false
        onTestResultDialog.setOnDismissListener {
            sharedPreferences.edit().putBoolean(IS_RESULT_DIALOG_SHOWN, false).apply()
            if (!dismissedWithAction) (activity as? TestActivityView)?.onFragmentBackPressed()
        }

        questTranslateRecycler.visibility = View.GONE
        etAnswerInputLayout.visibility = View.GONE
        btnGiveMeHint.visibility = View.GONE

        val onTestResultDialogBinding = DialogTestResultBinding.bind(onTestResultDialogView)
        onTestResultDialogBinding.apply {
            tvResultPairSetName.text = toolbar.title.toString()

            val mistakesTotal = translatePresenter.provideMistakes()

            if (mistakesTotal == 0) {

                tvResultMistakesCount.apply {
                    setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.dt3_brand_100,
                            requireActivity().theme
                        )
                    )
                    text =
                        getString(R.string.dt_on_test_result_dialog_mistake_count_line_no_mistakes)
                }

                mistakeListRecycler.visibility = View.GONE
                mistakeListContainer.visibility = View.GONE
                ibShowOrHideMistakes.visibility = View.GONE

            } else {

                tvResultMistakesCount.text = getString(
                    R.string.dt_on_test_result_dialog_mistake_count_line_has_mistakes,
                    mistakesTotal
                )

                mistakeListRecycler.visibility = View.VISIBLE
                mistakeListContainer.visibility = View.VISIBLE
                ibShowOrHideMistakes.visibility = View.VISIBLE

                if (mistakesTotal <= 3)
                    mistakeListRecycler.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT

                var mistakeListContainerIsShown = true
                ibShowOrHideMistakes.setOnClickListener {

                    mistakeListRecycler.isVisible = mistakeListContainerIsShown
                    mistakeListContainer.isVisible = mistakeListContainerIsShown
                    val iconImageResource =
                        if (mistakeListContainerIsShown)
                            R.drawable.icon_result_dialog_hide_mistake_list
                        else R.drawable.icon_result_dialog_show_mistake_list
                    ibShowOrHideMistakes.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            iconImageResource,
                            requireActivity().theme
                        )
                    )
                    mistakeListContainerIsShown = !mistakeListContainerIsShown
                }

                mistakeListAdapter = MistakeListAdapter()
                mistakeListAdapter.captureContext(requireContext())
                val mLayoutManager = LinearLayoutManager(requireActivity())
                val verticalDivider =
                    DividerItemDecoration(mistakeListRecycler.context, mLayoutManager.orientation)
                mistakeListRecycler.apply {
                    adapter = mistakeListAdapter
                    setHasFixedSize(true)
                    layoutManager = mLayoutManager
                    addItemDecoration(verticalDivider)
                }
                val mistakenPairList = translatePresenter.provideMistakenPairList()
                val wrongAnswerList = translatePresenter.provideWrongAnswerList()
                mistakeListAdapter.submitMistakenPairListAndWrongAnswerList(
                    mistakenPairList,
                    wrongAnswerList
                )
            }

            when (translatePresenter.provideHintUseCount()) {
                0 -> tvResultHintUseCount.visibility = View.GONE
                else -> tvResultHintUseCount.text = getString(
                    R.string.dt_on_test_result_dialog_hint_used_count_line,
                    translatePresenter.provideHintUseCount()
                )
            }

            //restart button
            btnTestResultDialogRestartTest.text =
                getString(R.string.dt_on_test_result_dialog_restart_test)
            btnTestResultDialogRestartTest.setOnClickListener {
                translatePresenter.restartTranslateTest(
                    shufflePairset
                )
                questTranslateRecycler.visibility = View.VISIBLE
                etAnswerInputLayout.visibility = View.VISIBLE
                btnGiveMeHint.visibility = View.VISIBLE

                dismissedWithAction = true
                onTestResultDialog.dismiss()
            }

            //to pairset list screen button
            btnTestResultDialogToPairsets.text =
                getString(R.string.dt_on_test_result_dialog_back_to_pairset_screen)
            btnTestResultDialogToPairsets.setOnClickListener {
                dismissedWithAction = true
                (activity as? TestActivityView)?.onFragmentBackPressed()
                onTestResultDialog.dismiss()
            }

            sharedPreferences.edit().putBoolean(IS_RESULT_DIALOG_SHOWN, true).apply()

            onTestResultDialog.show()
        }
    }


    override fun onResume() {
        super.onResume()

        val isResultDialogIsShown = sharedPreferences.getBoolean("IS_RESULT_DIALOG_SHOWN", false)
        if (!isResultDialogIsShown) {
            translatePresenter.restartTranslateTest(shufflePairset)
        }
    }

  /*   Enables snap scrolling behavior
   and makes visible answer field editText with
   according test-related elements */

     private fun toTestMode(targetRecyclerView: RecyclerView) {
         snapHelperAttached = true
         targetRecyclerView.apply {
             attachSnapHelperWithListener(
                 translateSnapHelper,
                 onSnapPositionChangeListener = this@TranslateFragment
             )
             isHorizontalScrollBarEnabled = false
         }
         etAnswerInputLayout.visibility = View.VISIBLE
         etAnswerInputLayout.editText?.text = null
         tvPairCounter.visibility = View.VISIBLE
         progressBar.visibility = View.VISIBLE
         btnGiveMeHint.visibility = View.VISIBLE
     }


    /* Disables snap and hides most of test-related elements,
         leaving only itemList, restart and endTest buttons.*/

    private fun toScrollMode(targetRecyclerView: RecyclerView) {
        snapHelperAttached = false
        (activity as? MvpAppCompatActivity)?.forceHideKeyboard(etAnswerInputLayout)
        translateSnapHelper.attachToRecyclerView(null)
        showConfirmButton(true)
        etAnswerInputLayout.visibility = View.GONE
        btnConfirmAnswer.visibility = View.GONE
        progressBar.visibility = View.GONE
        tvPairCounter.visibility = View.GONE
        targetRecyclerView.isHorizontalScrollBarEnabled = true
        btnGiveMeHint.visibility = View.GONE
    }


}