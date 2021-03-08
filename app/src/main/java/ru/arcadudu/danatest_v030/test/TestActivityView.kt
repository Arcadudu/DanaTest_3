package ru.arcadudu.danatest_v030.test

import com.google.android.material.appbar.MaterialToolbar
import ru.arcadudu.danatest_v030.models.PairSet

interface TestActivityView {
    fun toolbarSupport(toolbar:MaterialToolbar)
    fun onFragmentBackPressed()
    fun onTestReadyForResult(pairSet: PairSet, mistakes: Int)
}