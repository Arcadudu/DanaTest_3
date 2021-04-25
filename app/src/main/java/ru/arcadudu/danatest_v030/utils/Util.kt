package ru.arcadudu.danatest_v030.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.models.PairSet
import java.text.SimpleDateFormat
import java.util.*


fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    val bitmap =
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun recyclerLayoutAnimation(targetRecyclerView: RecyclerView, animationId: Int) {
    val context = targetRecyclerView.context
    val animationController: LayoutAnimationController =
        AnimationUtils.loadLayoutAnimation(context, animationId)
    targetRecyclerView.layoutAnimation = animationController
    targetRecyclerView.adapter?.notifyDataSetChanged()
    targetRecyclerView.scheduleLayoutAnimation()
}

fun getCreationDate():String{
    val simpleDateFormatExact = SimpleDateFormat("dd MMMM yyyy kk:mm", Locale.getDefault())
    return simpleDateFormatExact.format(Date()).toString()

}

fun getFirstVisitDefaultPairsetList():MutableList<PairSet>{
    val defaultPairsetList:MutableList<PairSet> = mutableListOf()
    var pairSetCount = 0
    repeat(3) {
        pairSetCount++
        defaultPairsetList.add(
            PairSet(
                name = "Набор #$pairSetCount"
            )
        )
    }
    defaultPairsetList.add(0, getTimePairSet())
    defaultPairsetList.add(0, getDummyPairSet())
    return defaultPairsetList
}


//val emptyPairsetDialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)
//        val emptyPairsetDialogView =
//            this.layoutInflater.inflate(R.layout.dialog_on_empty_pairset, null, false)
//        emptyPairsetDialogBuilder.setView(emptyPairsetDialogView)
//        val emptyPairsetDialog = emptyPairsetDialogBuilder.create()






