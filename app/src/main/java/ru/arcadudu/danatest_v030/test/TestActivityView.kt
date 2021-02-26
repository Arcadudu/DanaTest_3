package ru.arcadudu.danatest_v030.test

import ru.arcadudu.danatest_v030.models.PairSet

interface TestActivityView {
    fun onFragmentBackPressed()
    fun onTestReadyForResult(pairSet: PairSet, mistakes: Int)
}