package ru.arcadudu.danatest_v030.test.variants_fragment

import android.os.Bundle
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.translate_fragment.TranslateFragment

class VariantsFragment: MvpAppCompatFragment(), VariantsFragmentView{

  companion object {
        fun getVariantsFragmentInstance(args: Bundle?): VariantsFragment =
            VariantsFragment().apply { arguments = args }
    }


    @InjectPresenter
    lateinit var variantsPresenter:VariantsFragmentPresenter
}