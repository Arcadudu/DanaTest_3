package ru.arcadudu.danatest_v030.test

import com.google.android.material.appbar.MaterialToolbar
import ru.arcadudu.danatest_v030.models.Pairset

interface TestActivityView {
    fun toolbarSupport(toolbar:MaterialToolbar)
    fun onFragmentBackPressed()
    fun onTestReadyForResult(pairset: Pairset, mistakes: Int)
}