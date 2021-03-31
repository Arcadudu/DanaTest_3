package ru.arcadudu.danatest_v030.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.ActivityMainBinding

import ru.arcadudu.danatest_v030.fragments.ProfileFragment
import ru.arcadudu.danatest_v030.fragments.SettingsFragment
import ru.arcadudu.danatest_v030.pairSetFragment.PairSetFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private lateinit var profileFragment: ProfileFragment
    private lateinit var wordSetFragment: PairSetFragment
    private lateinit var settingsFragment: SettingsFragment

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        profileFragment = ProfileFragment()
        wordSetFragment = PairSetFragment()
        settingsFragment = SettingsFragment()
        setFragment(wordSetFragment)

    }


    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}