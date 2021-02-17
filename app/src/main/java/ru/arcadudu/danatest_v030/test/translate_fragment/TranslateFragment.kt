package ru.arcadudu.danatest_v030.test.translate_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.FragmentTestTranslateBinding

private const val PAIR_SET_TO_TEST_TAG = "wordSetToTestTag"

class TranslateFragment : MvpAppCompatFragment(), TranslateFragmentView {

    @InjectPresenter
    lateinit var translatePresenter: TranslateFragmentPresenter

    private lateinit var translateBinding: FragmentTestTranslateBinding


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

        translatePresenter.extractIncomingPairSet(
            activity?.intent,
            ru.arcadudu.danatest_v030.utils.PAIR_SET_TO_TEST_TAG
        )


    }
}