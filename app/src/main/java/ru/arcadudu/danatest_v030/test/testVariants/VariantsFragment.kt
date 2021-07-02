package ru.arcadudu.danatest_v030.test.testVariants

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textview.MaterialTextView
import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.DialogRemoveItemBinding
import ru.arcadudu.danatest_v030.databinding.DialogTestResultBinding
import ru.arcadudu.danatest_v030.databinding.FragmentTestVariantsBinding
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
import java.util.*

class VariantsFragment : MvpAppCompatFragment(), VariantsFragmentView, TestAdapterCallback,
    OnSnapPositionChangeListener {

    companion object {
        fun getVariantsFragmentInstance(args: Bundle?): VariantsFragment =
            VariantsFragment().apply { arguments = args }
    }

    @InjectPresenter
    lateinit var variantsPresenter: VariantsFragmentPresenter


    private lateinit var variantsBinding: FragmentTestVariantsBinding

    private lateinit var incomingPairset: Pairset

    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvCounterLine: MaterialTextView
    private lateinit var questVariantsRecycler: RecyclerView
    private lateinit var testAdapter: TranslateTestAdapter
    private lateinit var mistakeListAdapter: MistakeListAdapter
    private lateinit var answerToggleGroup: MaterialButtonToggleGroup
    private lateinit var btnAnswer1: MaterialButton
    private lateinit var btnAnswer2: MaterialButton
    private lateinit var btnAnswer3: MaterialButton
    private lateinit var btnAnswer4: MaterialButton
    private lateinit var checkedButton: MaterialButton
    private lateinit var btnGiveMeHint: Button

    private lateinit var variantsSnapHelper: PagerSnapHelper

    private var currentSnapPosition = 0
    private var shufflePairset = false
    private var snapHelperAttached = false
    private var enableHintForPairset = false
    private var useAllExistingPairsetsValuesAsVariants = false
    private var variantList: MutableList<String> = mutableListOf()

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test_variants, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        variantsBinding = FragmentTestVariantsBinding.bind(view)

        sharedPreferences = requireContext().getSharedPreferences(
            IS_RESULT_DIALOG_RESTORED_ON_RESUME,
            Context.MODE_PRIVATE
        )

        incomingPairset = arguments?.getSerializable("pairSet") as Pairset
        shufflePairset = arguments?.getBoolean("shuffle", false)!!
        enableHintForPairset = arguments?.getBoolean("enableHints", false)!!
        useAllExistingPairsetsValuesAsVariants = arguments?.getBoolean("useAllPairsets", false)!!

        variantsPresenter.apply {
            captureContext(requireContext())
            captureTestSettings(useAllExistingPairsetsValuesAsVariants)
            obtainTestedPairSet(incomingPairset)
        }

        toolbar = variantsBinding.variantsToolbar
        prepareToolbar(targetToolbar = toolbar)


        btnGiveMeHint = variantsBinding.btnGiveMeHintVariants
        btnGiveMeHint.apply {
            setOnClickListener {
                variantsPresenter.provideHintForCurrentPosition(currentSnapPosition)
            }
            this.visibility = if (enableHintForPairset) View.VISIBLE else View.GONE
        }

        questVariantsRecycler = variantsBinding.variantsQuestRecycler
        prepareRecycler(targetRecycler = questVariantsRecycler)

        tvCounterLine = variantsBinding.tvVariantsCounterLine

        answerToggleGroup = variantsBinding.answerButtonToggleGroup
        btnAnswer1 = variantsBinding.answerButton1
        btnAnswer2 = variantsBinding.answerButton2
        btnAnswer3 = variantsBinding.answerButton3
        btnAnswer4 = variantsBinding.answerButton4
        answerToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                checkedButton =
                    when (checkedId) {
                        R.id.answer_button1 -> btnAnswer1
                        R.id.answer_button2 -> btnAnswer2
                        R.id.answer_button3 -> btnAnswer3
                        else -> btnAnswer4
                    }

                checkedButton.isChecked = false

                variantsPresenter.checkAnswerAndDismiss(
                    checkedButton.text.toString(),
                    currentSnapPosition
                )
            }
        }

        progressBar = variantsBinding.variantsTestProgressbar
        variantsPresenter.getProgressMax()
    }


    private fun prepareRecycler(targetRecycler: RecyclerView) {
        testAdapter = TranslateTestAdapter()
        testAdapter.adapterCallback(this)

        variantsSnapHelper = PagerSnapHelper()
        targetRecycler.apply {
            setHasFixedSize(true)
            isHorizontalScrollBarEnabled = false
            adapter = testAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            attachSnapHelperWithListener(
                snapHelper = variantsSnapHelper,
                onSnapPositionChangeListener = this@VariantsFragment
            )
        }
        snapHelperAttached = true

        if (shufflePairset) variantsPresenter.provideShuffledPairList()
        else variantsPresenter.provideOrderedPairList()

        HorizontalOverScrollBounceEffectDecorator(RecyclerViewOverScrollDecorAdapter(targetRecycler))
    }

    override fun showOnTestResultDialog() {
        val onTestResultDialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)
        val onTestResultDialogView =
            this.layoutInflater.inflate(R.layout.dialog_test_result, null, false)
        onTestResultDialogBuilder.setView(onTestResultDialogView)
        val onTestResultDialog = onTestResultDialogBuilder.create()

        variantsPresenter.applyTestPassReward(
            variantsPresenter.provideMistakes(),
            variantsPresenter.provideHintUseCount(),
            requireContext()
        )

        var dismissedWithAction = false
        onTestResultDialog.setOnDismissListener {
            sharedPreferences.edit().putBoolean(IS_RESULT_DIALOG_SHOWN, false).apply()

            if (!dismissedWithAction) (activity as? TestActivityView)?.onFragmentBackPressed()
        }

        questVariantsRecycler.visibility = View.GONE
        answerToggleGroup.visibility = View.GONE
        btnGiveMeHint.visibility = View.GONE

        val onTestResultDialogBinding = DialogTestResultBinding.bind(onTestResultDialogView)
        onTestResultDialogBinding.apply {
            tvResultPairSetName.text = toolbar.title.toString()

            val mistakesTotal = variantsPresenter.provideMistakes()
            val hintsUsed = variantsPresenter.provideHintUseCount()

            // no mistakes
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


                // has mistakes
            } else {

                tvResultMistakesCount.apply {
                    text = getString(
                        R.string.dt_on_test_result_dialog_mistake_count_line_has_mistakes,
                        mistakesTotal
                    )
                }

                mistakeListContainer.visibility = View.VISIBLE
                mistakeListRecycler.visibility = View.VISIBLE
                ibShowOrHideMistakes.visibility = View.VISIBLE

                if (mistakesTotal <= 3)
                    mistakeListRecycler.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT

                var mistakeListContainerIsShown = true
                ibShowOrHideMistakes.setOnClickListener {

                    mistakeListContainer.isVisible = mistakeListContainerIsShown
                    mistakeListRecycler.isVisible = mistakeListContainerIsShown
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
                val mistakenPairList = variantsPresenter.provideMistakenPairList()
                val wrongAnswerList = variantsPresenter.provideWrongAnswerList()

                mistakeListAdapter.submitMistakenPairListAndWrongAnswerList(
                    mistakenPairList,
                    wrongAnswerList
                )
            }

            when (hintsUsed) {
                0 -> tvResultHintUseCount.visibility = View.GONE
                else -> tvResultHintUseCount.text = getString(
                    R.string.dt_on_test_result_dialog_hint_used_count_line,
                    variantsPresenter.provideHintUseCount()
                )
            }

            //restart button
            btnTestResultDialogRestartTest.text =
                getString(R.string.dt_on_test_result_dialog_restart_test)
            btnTestResultDialogRestartTest.setOnClickListener {
                variantsPresenter.restartVariantsTest(
                    shufflePairset,
                    useAllExistingPairsetsValuesAsVariants
                )
                questVariantsRecycler.visibility = View.VISIBLE
                answerToggleGroup.visibility = View.VISIBLE
                btnGiveMeHint.visibility = View.VISIBLE

                variantsPresenter.getVariantsForCurrentPosition(currentSnapPosition)
                dismissedWithAction = true
                onTestResultDialog.dismiss()
            }

            //to pairset list screen button
            btnTestResultDialogToPairsets.text =
                getString(R.string.dt_on_test_result_dialog_back_to_pairset_screen)
            btnTestResultDialogToPairsets.setOnClickListener {
                dismissedWithAction = true
                /*variantsPresenter.applyTestPassReward(mistakesTotal, hintsUsed, requireContext())*/
                (activity as? TestActivityView)?.onFragmentBackPressed()
                onTestResultDialog.dismiss()
            }

        }


        // if app will stop and then resume this boolean will help
        // to avoid unnecessary test restart
        sharedPreferences.edit().putBoolean(IS_RESULT_DIALOG_SHOWN, true).apply()

        onTestResultDialog.show()

    }

    private fun prepareToolbar(targetToolbar: MaterialToolbar) {
        variantsPresenter.getToolbarTitle()
        targetToolbar.apply {
            inflateMenu(R.menu.test_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.exit_test -> {
                        (activity as? TestActivityView)?.onFragmentBackPressed()
                        true
                    }
                    R.id.refresh_test -> {
                        variantsPresenter.onRestartButton()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val isResultDialogIsShown = sharedPreferences.getBoolean("IS_RESULT_DIALOG_SHOWN", false)
        if (!isResultDialogIsShown) {
            variantsPresenter.restartVariantsTest(
                shufflePairset,
                useAllExistingPairsetsValuesAsVariants
            )
            variantsPresenter.getVariantsForCurrentPosition(currentSnapPosition)
        }


    }

    override fun setProgressMax(originPairListCount: Int) {
        progressBar.max = originPairListCount
    }

    override fun showOnRestartDialog(pairsetName: String, pairsetPairCount: Int) {
        val restartDialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)
        val restartDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null)
        restartDialogBuilder.setView(restartDialogView)
        val restartDialog = restartDialogBuilder.create()
        val restartDialogBinding = DialogRemoveItemBinding.bind(restartDialogView)
        restartDialogBinding.apply {
            tvRemoveDialogTitle.text = getString(R.string.dt_restart_test_dialog_title, pairsetName)
            tvRemoveDialogMessage.text = getString(R.string.dt_restart_test_dialog_message)
            tvRemovePairsetDialogPairCounter.text = pairsetPairCount.toString()
        }
        val btnCancelRestart = restartDialogBinding.btnCancelRemove
        btnCancelRestart.text = getString(R.string.dt_restart_test_dialog_negative_btn)
        val btnRestartTest = restartDialogBinding.btnRemovePair
        btnRestartTest.text = getString(R.string.dt_restart_test_dialog_positive_btn)
        btnRestartTest.setTextColor(
            ResourcesCompat.getColor(resources, R.color.dt3_brand_100, activity?.theme)
        )
        btnCancelRestart.setOnClickListener {
            restartDialog.dismiss()
        }

        btnRestartTest.setOnClickListener {
            variantsPresenter.restartVariantsTest(
                shufflePairset,
                useAllExistingPairsetsValuesAsVariants
            )
            variantsPresenter.getVariantsForCurrentPosition(currentSnapPosition)
            restartDialog.dismiss()
        }
        restartDialog.show()
    }

    override fun updateCounterLine(
        testedPairSetName: String,
        pairListCount: Int,
        pairListOriginalCount: Int
    ) {
        tvCounterLine.text = getString(
            R.string.dt_test_variants_fragment_counter_body,
            pairListCount,
            pairListOriginalCount
        )
            .capitalize(Locale.ROOT).trim()
    }

    override fun initPairList(testedPairList: MutableList<Pair>) {
        testAdapter.apply {
            submitData(testedPairList)
            notifyDataSetChanged()
        }
    }

    override fun updateAnsweredProgress(answeredPairCount: Int, duration: Long) {
        ObjectAnimator.ofInt(progressBar, "progress", answeredPairCount).setDuration(duration)
            .start()
        progressBar.progress = answeredPairCount
    }

    //method listening to questCardRecycler item touch


    override fun showVariants(keySetCut: MutableList<String>) {
        variantList.clear()
        variantList = keySetCut
        variantList.shuffle()
        btnAnswer1.text = variantList[0].trim().toLowerCase(Locale.ROOT)
        btnAnswer2.text = variantList[1].trim().toLowerCase(Locale.ROOT)
        btnAnswer3.text = variantList[2].trim().toLowerCase(Locale.ROOT)
        btnAnswer4.text = variantList[3].trim().toLowerCase(Locale.ROOT)
        answerToggleGroup.isPressed = false
    }

    override fun updateRecyclerOnRemoved(
        updatedPairList: MutableList<Pair>,
        answerPosition: Int
    ) {
        testAdapter.apply {
            submitData(updatedPairList)
            notifyItemRemoved(answerPosition)

            if (answerPosition != 0) {
                questVariantsRecycler.smoothScrollToPosition(answerPosition - 1)
                currentSnapPosition = answerPosition - 1
            }
            variantsPresenter.getVariantsForCurrentPosition(currentSnapPosition)

        }
    }

    override fun toResultFragment(backUpPairset: Pairset, mistakeCount: Int) {
        (activity as? TestActivityView)?.onTestReadyForResult(backUpPairset, mistakeCount)
    }

    override fun getHintForCurrentPosition(pairKey: String) {
        for (i in 0 until answerToggleGroup.childCount) {
            val button = answerToggleGroup[i] as MaterialButton
            if (button.text == pairKey.toLowerCase(Locale.ROOT).trim()) {
                button.isPressed = true
            }
        }

    }

    override fun setToolbarTitle(testedPairSetName: String) {
        toolbar.title = testedPairSetName
    }

    override fun onSnapPositionChange(position: Int) {
        currentSnapPosition = position
        variantsPresenter.getVariantsForCurrentPosition(position)
    }


    override fun onAdapterItemClick() {
        if (snapHelperAttached) toScrollMode(questVariantsRecycler)
        else toTestMode(questVariantsRecycler)

    }

    /* Enables snap scrolling behavior
     and makes visible answer field editText with
     according test-related elements */

    private fun toTestMode(targetRecyclerView: RecyclerView) {
        snapHelperAttached = true
        targetRecyclerView.apply {
            attachSnapHelperWithListener(
                variantsSnapHelper,
                onSnapPositionChangeListener = this@VariantsFragment
            )
            isHorizontalScrollBarEnabled = false

        }

        progressBar.visibility = View.VISIBLE
        tvCounterLine.visibility = View.VISIBLE
        answerToggleGroup.visibility = View.VISIBLE
        btnGiveMeHint.visibility = View.VISIBLE
    }


    /* Disables snap, enabling faster scrolling
     and hides most of test-related elements,
     leaving only itemList, restart and endTest buttons.*/

    private fun toScrollMode(targetRecyclerView: RecyclerView) {
        snapHelperAttached = false
        variantsSnapHelper.attachToRecyclerView(null)
        progressBar.visibility = View.GONE
        tvCounterLine.visibility = View.GONE
        answerToggleGroup.visibility = View.GONE
        btnGiveMeHint.visibility = View.GONE
        targetRecyclerView.isHorizontalScrollBarEnabled = true
    }
}