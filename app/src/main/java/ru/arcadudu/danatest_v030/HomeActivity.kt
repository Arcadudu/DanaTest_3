package ru.arcadudu.danatest_v030

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var bottomSheetDetails: ConstraintLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var bsTitle: TextView
    private lateinit var bsInfo: TextView
    private lateinit var bsPerfectBody: TextView
    private lateinit var bsGoodBody: TextView
    private lateinit var bsFailedBody: TextView

    private lateinit var btnStraightInfo: ImageView
    private lateinit var btnFourVarsInfo: ImageView
    private lateinit var btnShuffleInfo: ImageView

    private lateinit var vTransparent: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setTransparentStatusBar()

        btnStraightInfo = findViewById(R.id.iv_straight_translate_info_button)
        btnStraightInfo.setOnClickListener(this)

        btnFourVarsInfo = findViewById(R.id.iv_four_variants_info_button)
        btnFourVarsInfo.setOnClickListener(this)

        btnShuffleInfo = findViewById(R.id.iv_shuffle_info_button)
        btnShuffleInfo.setOnClickListener(this)


        vTransparent = findViewById(R.id.transparent_view)

        // bottom sheet
        bottomSheetDetails = findViewById(R.id.home_bottom_sheet_details)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetDetails)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        btnStraightInfo.isEnabled = true
                        btnFourVarsInfo.isEnabled = true
                        btnShuffleInfo.isEnabled = true
                    }
                    else -> {
                        btnStraightInfo.isEnabled = false
                        btnFourVarsInfo.isEnabled = false
                        btnShuffleInfo.isEnabled = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset <= 0) vTransparent.alpha = slideOffset + 1
            }

        })
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bsTitle = findViewById(R.id.tv_home_bs_title)
        bsInfo = findViewById(R.id.tv_home_bs_details_heavy)

        bsPerfectBody = findViewById(R.id.tv_home_bs_perfect_body)
        bsGoodBody = findViewById(R.id.tv_home_bs_good_body)
        bsFailedBody = findViewById(R.id.tv_home_bs_failed_block_body)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_straight_translate_info_button -> {
                openBottomSheet(
                    title = getString(R.string.translate),
                    info = getString(R.string.home_bs_details_straight_translate)
                )
            }
            R.id.iv_four_variants_info_button -> {
                openBottomSheet(
                    title = getString(R.string.variants),
                    info = getString(R.string.variants_description_light)
                )
            }
            R.id.iv_shuffle_info_button -> {
                openBottomSheet(
                    title = getString(R.string.shuffle),
                    info = getString(R.string.shuffle_description_light)
                )
            }
        }
    }

    private fun setTransparentStatusBar() {
        val windowVal = window
        windowVal.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            )
            setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            navigationBarColor = getColor(android.R.color.transparent)
            statusBarColor = getColor(android.R.color.transparent)
        }


    }

    private fun openBottomSheet(title: String, info: String) {
        bsTitle.text = title
        bsInfo.text = info
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


}