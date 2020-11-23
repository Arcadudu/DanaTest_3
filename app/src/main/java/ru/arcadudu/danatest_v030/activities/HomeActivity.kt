package ru.arcadudu.danatest_v030.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.fragments.*
import ru.arcadudu.danatest_v030.interfaces.ClickableItem
import ru.arcadudu.danatest_v030.models.WordSet

class HomeActivity : AppCompatActivity(){

    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var wordSetFragment: WordSetFragment
    private lateinit var statsFragment: StatsFragment
    private lateinit var settingsFragment: SettingsFragment

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        wordSetFragment = WordSetFragment()
        statsFragment = StatsFragment()
        settingsFragment = SettingsFragment()

        setFragment(homeFragment)


        bottomNavigationView = findViewById(R.id.my_navigation_bar)
        bottomNavigationView.selectedItemId = R.id.home_item
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val selectedFragment: Fragment =
                when (item.itemId) {
                    R.id.home_item -> homeFragment
                    R.id.profile_item -> profileFragment
                    R.id.wordset_item -> wordSetFragment
                    R.id.stats_item -> statsFragment
                    R.id.settings_item -> settingsFragment
                    else -> homeFragment
                }
            setFragment(selectedFragment)
            true
        }


    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_fragment_container, fragment)
            .addToBackStack(null)
            .commit()

    }


}