package ru.arcadudu.danatest_v030.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.ActivityMainBinding

import ru.arcadudu.danatest_v030.pairSetFragment.PairsetFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ActivityMainBinding.inflate(layoutInflater).root
        setContentView(view)
        setFragment(PairsetFragment())
    }


    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}