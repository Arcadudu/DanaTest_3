package ru.arcadudu.danatest_v030.test.testVariants

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter
import moxy.MvpAppCompatActivity
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
import ru.arcadudu.danatest_v030.utils.forceHideKeyboard
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
    private lateinit var etAnswerInputLayout: TextInputLayout
    private lateinit var etAnswerField:MaterialAutoCompleteTextView
    private lateinit var btnConfirmAnswer:ImageView

    private lateinit var variantsSnapHelper: PagerSnapHelper

    private var currentSnapPosition = 0
    private var shufflePairset = false
    private var snapHelperAttached = false

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

        etAnswerInputLayout = variantsBinding.etVariantsFragmentAnswerField

        etAnswerField = etAnswerInputLayout.editText as MaterialAutoCompleteTextView
        etAnswerField.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                (activity as? MvpAppCompatActivity)?.forceHideKeyboard(v)
        }

        btnConfirmAnswer = variantsBinding.btnConfirmVariant




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

    override fun setProgressMax(originPairListCount: Int) {
        progressBar.max = originPairListCount
    }

    override fun showOnRestartDialog(pairsetName: String) {
        val restartDialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
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
            variantsPresenter.restartTranslateTest(shufflePairset)
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
        TODO("Not yet implemented")
    }

    override fun onSnapPositionChange(position: Int) {
        currentSnapPosition = position
        Log.d("rrr", "onSnapPositionChange: currentSnapPosition = $currentSnapPosition ")
        variantsPresenter.getVariantsForCurrentPosition(position)
    }
}