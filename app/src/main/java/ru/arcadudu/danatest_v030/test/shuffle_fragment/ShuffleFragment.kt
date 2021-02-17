package ru.arcadudu.danatest_v030.test.shuffle_fragment

import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ShuffleFragment: MvpAppCompatFragment(), ShuffleFragmentView {

    @InjectPresenter
    lateinit var shufflePresenter:ShuffleFragmentPresenter
}