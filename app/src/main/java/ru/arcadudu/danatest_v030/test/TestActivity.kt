package ru.arcadudu.danatest_v030.test

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import moxy.MvpAppCompatActivity
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.HomeActivity
import ru.arcadudu.danatest_v030.databinding.ActivityTestBinding
import ru.arcadudu.danatest_v030.models.PairSet
import ru.arcadudu.danatest_v030.test.testShuffle.ShuffleFragment
import ru.arcadudu.danatest_v030.test.testTranslate.TranslateFragment
import ru.arcadudu.danatest_v030.test.testVariants.VariantsFragment

private const val TRANSLATE_FRAGMENT_ID = "TRANSLATE_FRAGMENT_ID"
private const val SHUFFLE_FRAGMENT_ID = "SHUFFLE_FRAGMENT_ID"
private const val VARIANTS_FRAGMENT_ID = "VARIANTS_FRAGMENT_ID"

private const val WORDSET_TO_TEST_TAG = "wordSetToTestTag"

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
        incomingPairSet = incomingIntent.getSerializableExtra(WORDSET_TO_TEST_TAG) as PairSet
        requestedTestFragmentId = incomingIntent.getStringExtra("testFragmentId") as String

        val bundle = Bundle()
        bundle.putSerializable("pairSet", incomingPairSet)

        setActiveFragment(requestedTestFragmentId, bundle)
    }

    private fun setActiveFragment(fragmentId: String, bundle: Bundle) {
        val targetFragment =
            when (fragmentId) {
                TRANSLATE_FRAGMENT_ID -> TranslateFragment.getTranslateFragmentInstance(bundle)
                SHUFFLE_FRAGMENT_ID -> ShuffleFragment.getShuffleFragmentInstance(bundle)
                VARIANTS_FRAGMENT_ID -> VariantsFragment.getVariantsFragmentInstance(bundle)
                else -> onBackPressed()
            }

        supportFragmentManager.beginTransaction()
            .replace(R.id.test_fragment_container, targetFragment as Fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onFragmentBackPressed() {
        val intent = Intent(this, HomeActivity::class.java )
        startActivity(intent)
    }
}

