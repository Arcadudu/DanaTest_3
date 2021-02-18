package ru.arcadudu.danatest_v030.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.databinding.ActivityMainBinding
import ru.arcadudu.danatest_v030.fragments.*
import ru.arcadudu.danatest_v030.pairSetFragment.PairSetFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var wordSetFragment: PairSetFragment
    private lateinit var settingsFragment: SettingsFragment

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        wordSetFragment = PairSetFragment()
        settingsFragment = SettingsFragment()
        setFragment(wordSetFragment)

        bottomNavigationView = binding.myNavigationBar.apply {
            selectedItemId = R.id.wordset_item
            setOnNavigationItemSelectedListener { item ->
                val selectedFragment: Fragment =
                    when (item.itemId) {
                        R.id.profile_item -> profileFragment
                        R.id.wordset_item -> wordSetFragment
                        R.id.settings_item -> settingsFragment
                        else -> homeFragment
                    }
                setFragment(selectedFragment)
                true
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}