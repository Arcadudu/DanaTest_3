package ru.arcadudu.danatest_v030.test.shuffle_fragment

import android.os.Bundle
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.activities.tests.ShuffleActivity
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.translate_fragment.TranslateFragment


class ShuffleFragment : MvpAppCompatFragment(), ShuffleFragmentView {

    companion object {
        fun getShuffleFragmentInstance(args: Bundle?): ShuffleFragment =
            ShuffleFragment().apply { arguments = args }
    }

    @InjectPresenter
    lateinit var shufflePresenter: ShuffleFragmentPresenter
}