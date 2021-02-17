package ru.arcadudu.danatest_v030.test

import android.os.Bundle
import moxy.MvpAppCompatActivity
import ru.arcadudu.danatest_v030.databinding.ActivityTestBinding

class TestActivity : MvpAppCompatActivity() {

    private lateinit var binding:ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        


    }
}