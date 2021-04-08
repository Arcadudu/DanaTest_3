package ru.arcadudu.danatest_v030.test.testVariants

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
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
import ru.arcadudu.danatest_v030.databinding.FragmentTestVariantsBinding
import ru.arcadudu.danatest_v030.interfaces.OnSnapPositionChangeListener
import ru.arcadudu.danatest_v030.interfaces.TestAdapterCallback
import ru.arcadudu.danatest_v030.models.Pair
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.TestActivityView
import ru.arcadudu.danatest_v030.test.TranslateTestAdapter
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

    private lateinit var incomingPairSet: PairSet

    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvCounterLine: MaterialTextView
    private lateinit var questVariantsRecycler: RecyclerView
    private lateinit var testAdapter: TranslateTestAdapter
    private lateinit var answerToggleGroup: MaterialButtonToggleGroup
    private lateinit var btnAnswer1: MaterialButton
    private lateinit var btnAnswer2: MaterialButton
    private lateinit var btnAnswer3: MaterialButton
    private lateinit var btnAnswer4: MaterialButton
    private lateinit var checkedButton: MaterialButton

    private lateinit var variantsSnapHelper: PagerSnapHelper

    private var currentSnapPosition = 0
    private var shufflePairset = false
    private var snapHelperAttached = false
    private var variantList: MutableList<String> = mutableListOf()

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

        incomingPairSet = arguments?.getSerializable("pairSet") as PairSet
        shufflePairset = arguments?.getBoolean("shuffle", false)!!
        variantsPresenter.obtainTestedPairSet(incomingPairSet)

        toolbar = variantsBinding.variantsToolbar
        prepareToolbar(targetToolbar = toolbar)

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

    private fun showToast(string: String) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
    }

    private fun prepareRecycler(targetRecycler: RecyclerView) {
        testAdapter = TranslateTestAdapter()
        testAdapter.adapterCallback(this)
        variantsSnapHelper = PagerSnapHelper()
        targetRecycler.apply {
            setHasFixedSize(true)
            isHorizontalScrollBarEnabled = false
            adapter = testAdapter
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            attachSnapHelperWithListener(
                snapHelper = variantsSnapHelper,
                onSnapPositionChangeListener = this@VariantsFragment
            )
        }
        snapHelperAttached = true
        if (shufflePairset) {
            variantsPresenter.provideShuffledPairList()
        } else {
            variantsPresenter.provideOrderedPairList()
        }

        HorizontalOverScrollBounceEffectDecorator(RecyclerViewOverScrollDecorAdapter(targetRecycler))
    }

    private fun prepareToolbar(targetToolbar: MaterialToolbar) {
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
        variantsPresenter.restartVariantsTest(shufflePairset)
    }

    override fun setProgressMax(originPairListCount: Int) {
        progressBar.max = originPairListCount
    }

    override fun showOnRestartDialog(pairsetName: String) {
        val restartDialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)
        val restartDialogView = this.layoutInflater.inflate(R.layout.dialog_remove_item, null)
        restartDialogBuilder.setView(restartDialogView)
        val restartDialog = restartDialogBuilder.create()
        val restartDialogBinding = DialogRemoveItemBinding.bind(restartDialogView)
        restartDialogBinding.apply {
            tvRemoveDialogTitle.text = getString(R.string.dt_restart_test_dialog_title, pairsetName)
            tvRemoveDialogMessage.text = getString(R.string.dt_restart_test_dialog_message)
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
            variantsPresenter.restartVariantsTest(shufflePairset)
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

    override fun onAdapterItemClick() {
        if (snapHelperAttached) {
            toScrollMode(questVariantsRecycler)
        } else {
            toTestMode(questVariantsRecycler)
        }
    }

    override fun showVariants(keySetCut: MutableList<String>) {
        variantList.clear()
        variantList = keySetCut
        variantList.shuffle()
        btnAnswer1.text = variantList[0].capitalize(Locale.ROOT)
        btnAnswer2.text = variantList[1].capitalize(Locale.ROOT)
        btnAnswer3.text = variantList[2].capitalize(Locale.ROOT)
        btnAnswer4.text = variantList[3].capitalize(Locale.ROOT)
    }

    override fun updateRecyclerOnRemoved(
        updatedPairList: MutableList<Pair>,
        answerPosition: Int
    ) {

        testAdapter.apply {
            submitData(updatedPairList)
            notifyItemRemoved(answerPosition)
            variantsPresenter.getVariantsForCurrentPosition(currentSnapPosition)
        }
    }

    override fun onSnapPositionChange(position: Int) {
        currentSnapPosition = position
        Log.d("rrr", "onSnapPositionChange: currentSnapPosition = $currentSnapPosition ")
        variantsPresenter.getVariantsForCurrentPosition(position)

    }

    private fun toTestMode(targetRecyclerView: RecyclerView) {
        snapHelperAttached = true
        targetRecyclerView.apply {
            attachSnapHelperWithListener(
                variantsSnapHelper,
                onSnapPositionChangeListener = this@VariantsFragment
            )
            isHorizontalScrollBarEnabled = false
        }
//        etAnswerInputLayout.visibility = View.VISIBLE
//        autoCompleteAnswerField.text = null
        tvCounterLine.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun toScrollMode(targetRecyclerView: RecyclerView) {
        snapHelperAttached = false
        variantsSnapHelper.attachToRecyclerView(null)
//        etAnswerInputLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        tvCounterLine.visibility = View.GONE
        targetRecyclerView.isHorizontalScrollBarEnabled = true
    }
}