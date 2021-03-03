package ru.arcadudu.danatest_v030.test.testVariants

import android.os.Bundle
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class VariantsFragment: MvpAppCompatFragment(), VariantsFragmentView{

  companion object {
        fun getVariantsFragmentInstance(args: Bundle?): VariantsFragment =
            VariantsFragment().apply { arguments = args }
    }


    @InjectPresenter
    lateinit var variantsPresenter:VariantsFragmentPresenter
}