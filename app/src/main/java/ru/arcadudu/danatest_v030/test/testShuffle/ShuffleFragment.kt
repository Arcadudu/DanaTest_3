package ru.arcadudu.danatest_v030.test.testShuffle

import android.os.Bundle
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ShuffleFragment : MvpAppCompatFragment(), ShuffleFragmentView {

    companion object {
        fun getShuffleFragmentInstance(args: Bundle?): ShuffleFragment =
            ShuffleFragment().apply { arguments = args }
    }

    @InjectPresenter
    lateinit var shufflePresenter: ShuffleFragmentPresenter
}