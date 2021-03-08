package ru.arcadudu.danatest_v030.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import moxy.MvpAppCompatActivity
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.HomeActivity
import ru.arcadudu.danatest_v030.databinding.ActivityTestBinding
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.resultFragment.ResultFragment
import ru.arcadudu.danatest_v030.test.testShuffle.ShuffleFragment
import ru.arcadudu.danatest_v030.test.testTranslate.TranslateFragment
import ru.arcadudu.danatest_v030.test.testVariants.VariantsFragment
import ru.arcadudu.danatest_v030.utils.CONST_PAIR_SET_TO_TEST_TAG

private const val CONST_TRANSLATE_FRAGMENT_ID = "TRANSLATE_FRAGMENT_ID"
private const val CONST_SHUFFLE_FRAGMENT_ID = "SHUFFLE_FRAGMENT_ID"
private const val CONST_VARIANTS_FRAGMENT_ID = "VARIANTS_FRAGMENT_ID"


class TestActivity : MvpAppCompatActivity(), TestActivityView {

    private lateinit var binding: ActivityTestBinding

    private lateinit var incomingPairSet: PairSet
    private lateinit var requestedTestFragmentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val incomingIntent = intent
        incomingPairSet = incomingIntent.getSerializableExtra(CONST_PAIR_SET_TO_TEST_TAG) as PairSet
        requestedTestFragmentId = incomingIntent.getStringExtra("testFragmentId") as String

        val bundle = Bundle()
        bundle.putSerializable("pairSet", incomingPairSet)

        setActiveTestFragment(requestedTestFragmentId, bundle)
    }

    private fun replaceFragment(targetFragment: Fragment) {
        Log.d("replace", "TestActivity: replaceFragment: callback ")
        supportFragmentManager.beginTransaction()

            .setCustomAnimations(
                R.anim.frag_transition_enter_from_right_to_left,
                R.anim.frag_transition_exit_from_right_to_left
            )
            .replace(R.id.test_fragment_container, targetFragment)
            .addToBackStack(null)
            .commit()
    }


    private fun setActiveTestFragment(fragmentId: String, bundle: Bundle) {
        val testFragment =
            when (fragmentId) {
                CONST_TRANSLATE_FRAGMENT_ID -> TranslateFragment.getTranslateFragmentInstance(bundle)
                CONST_SHUFFLE_FRAGMENT_ID -> ShuffleFragment.getShuffleFragmentInstance(bundle)
                CONST_VARIANTS_FRAGMENT_ID -> VariantsFragment.getVariantsFragmentInstance(bundle)
                else -> onBackPressed()
            }

        replaceFragment(targetFragment = testFragment as Fragment)
    }

    override fun toolbarSupport(toolbar: MaterialToolbar) {
        toolbar.apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onFragmentBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onTestReadyForResult(pairSet: PairSet, mistakes: Int) {
        val resultBundle = Bundle()
        resultBundle.apply {
            putSerializable("testedPairSet", pairSet)
            putInt("mistakes", mistakes)
        }
        val resultFragment = ResultFragment.getResultFragmentInstance(resultBundle)
        replaceFragment(resultFragment)

    }
}

