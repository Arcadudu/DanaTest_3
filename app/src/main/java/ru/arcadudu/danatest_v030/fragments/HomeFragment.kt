package ru.arcadudu.danatest_v030.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.activities.tests.ShuffleActivity
import ru.arcadudu.danatest_v030.activities.tests.TranslateActivity
import ru.arcadudu.danatest_v030.activities.tests.VariantsActivity
import ru.arcadudu.danatest_v030.databinding.FragmentHomeBinding

private lateinit var binding: FragmentHomeBinding

private lateinit var btnTranslateInfo: ImageView
private lateinit var btnVariantsInfo: ImageView
private lateinit var btnShuffleInfo: ImageView

private lateinit var crdTranslate: CardView
private lateinit var crdVariants: CardView
private lateinit var crdShuffle: CardView

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


        btnTranslateInfo = binding.ivTranslateInfoButton
        btnTranslateInfo.setOnClickListener(this)

        btnVariantsInfo = binding.ivFourVariantsInfoButton
        btnVariantsInfo.setOnClickListener(this)

        btnShuffleInfo = binding.ivShuffleInfoButton
        btnShuffleInfo.setOnClickListener(this)

        crdTranslate = binding.crdTranslateCard
        crdVariants = binding.crdFourVariantsCard
        crdShuffle = binding.crdShuffleCard

        crdTranslate.setOnClickListener{
            val intent = Intent(activity, TranslateActivity::class.java)
            startActivity(intent)
        }

        crdVariants.setOnClickListener{
            val intent = Intent(activity, VariantsActivity::class.java)
            startActivity(intent)
        }

        crdShuffle.setOnClickListener{
            val intent = Intent(activity, ShuffleActivity::class.java)
            startActivity(intent)
        }
    }

    fun goToTest(target: Activity) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_translate_info_button ->
                Toast.makeText(context, "straight translate", Toast.LENGTH_SHORT).show()

            R.id.iv_four_variants_info_button ->
                Toast.makeText(context, "four variants", Toast.LENGTH_SHORT).show()

            R.id.iv_shuffle_info_button ->
                Toast.makeText(context, "shuffle", Toast.LENGTH_SHORT).show()


        }
    }
}