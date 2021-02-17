package ru.arcadudu.danatest_v030.test.variants_fragment

import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class VariantsFragment: MvpAppCompatFragment(), VariantsFragmentView{

    @InjectPresenter
    lateinit var variantsPresenter:VariantsFragmentPresenter
}