package ru.arcadudu.danatest_v030.test.translate_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.FragmentTestTranslateBinding
import ru.arcadudu.danatest_v030.models.PairSet
import java.io.Serializable

private const val PAIR_SET_TO_TEST_TAG = "wordSetToTestTag"

class TranslateFragment : MvpAppCompatFragment(), TranslateFragmentView {

    companion object {
        fun getTranslateFragmentInstance(args: Bundle?): TranslateFragment =
            TranslateFragment().apply { arguments = args }
    }

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
        Log.d(
            "aaa",
            "Translate Fragment / onViewCreated: arguments content ${arguments?.getSerializable("pairSet") == null}"
        )



        val incomingPairSet = arguments?.getSerializable("pairSet") as PairSet
        Log.d("aaa", "Translate Fragment / onViewCreated: incomingPairSet is ${incomingPairSet.name} ")
        val toolbar:Toolbar = translateBinding.translateToolbar
        toolbar.title = incomingPairSet.name

    }
}