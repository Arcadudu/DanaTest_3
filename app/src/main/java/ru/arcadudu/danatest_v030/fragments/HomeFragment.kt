package ru.arcadudu.danatest_v030.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.tests.TranslateActivity
import ru.arcadudu.danatest_v030.databinding.FragmentHomeBinding

private lateinit var binding: FragmentHomeBinding
private lateinit var card1:LinearLayout

class HomeFragment : Fragment(), View.OnClickListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val binding = FragmentWordSetBinding.bind(view)
        //        wordSetBinding = binding
        binding = FragmentHomeBinding.bind(view)
        card1 = binding.card1
        card1.setOnClickListener{
            val intent = Intent(activity, TranslateActivity::class.java)
            startActivity(intent)
        }


    }


    fun goToTest(target: Activity) {

    }

    override fun onClick(v: View?) {

    }
}