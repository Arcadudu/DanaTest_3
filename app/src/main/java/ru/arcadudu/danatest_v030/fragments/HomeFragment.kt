package ru.arcadudu.danatest_v030.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import ru.arcadudu.danatest_v030.R

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

        val btnStraightInfo: ImageView = view.findViewById(R.id.iv_straight_translate_info_button)
        btnStraightInfo.setOnClickListener(this)

        val btnVariantsInfo: ImageView = view.findViewById(R.id.iv_four_variants_info_button)
        btnVariantsInfo.setOnClickListener(this)

        val btnShuffleInfo: ImageView = view.findViewById(R.id.iv_shuffle_info_button)
        btnShuffleInfo.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_straight_translate_info_button ->
                Toast.makeText(context, "straight translate", Toast.LENGTH_SHORT).show()

            R.id.iv_four_variants_info_button ->
                Toast.makeText(context, "four variants", Toast.LENGTH_SHORT).show()

            R.id.iv_shuffle_info_button ->
                Toast.makeText(context, "shuffle", Toast.LENGTH_SHORT).show()
        }
    }
}