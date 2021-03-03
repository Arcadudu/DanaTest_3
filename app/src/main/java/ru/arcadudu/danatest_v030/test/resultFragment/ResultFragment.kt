package ru.arcadudu.danatest_v030.test.resultFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.FragmentTestResultBinding
import ru.arcadudu.danatest_v030.models.PairSet


class ResultFragment: MvpAppCompatFragment(), ResultFragmentView {

    companion object{
        fun getResultFragmentInstance(args:Bundle?):ResultFragment =
            ResultFragment().apply { arguments = args }

    }

//     companion object {
//        fun getTranslateFragmentInstance(args: Bundle?): TranslateFragment =
//            TranslateFragment().apply { arguments = args }
//    }

    private lateinit var resultBinding:FragmentTestResultBinding

    @InjectPresenter
    lateinit var resultPresenter:ResultFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultBinding = FragmentTestResultBinding.bind(view)

        val tvPairSetTitle = resultBinding.tvResultPairSetName
        val tvMistakesCount = resultBinding.tvResultMistakesCount

        val pairSet = arguments?.getSerializable("testedPairSet") as PairSet
        tvPairSetTitle.text = pairSet.name
        val mistakes = arguments?.getInt("mistakes")
        tvMistakesCount.text = mistakes.toString()





    }
}