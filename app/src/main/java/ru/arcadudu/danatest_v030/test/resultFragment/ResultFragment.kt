package ru.arcadudu.danatest_v030.test.resultFragment

import androidx.fragment.app.Fragment
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ResultFragment: MvpAppCompatFragment(), ResultFragmentView {

    @InjectPresenter
    lateinit var resultPresenter:ResultFragmentPresenter

}