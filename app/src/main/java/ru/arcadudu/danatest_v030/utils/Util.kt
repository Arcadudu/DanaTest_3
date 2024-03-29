package ru.arcadudu.danatest_v030.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.models.Pairset
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

fun getFirstVisitDefaultPairsetList():MutableList<Pairset>{
    val defaultPairsetList:MutableList<Pairset> = mutableListOf()
    return defaultPairsetList.apply {
        add(0, getDefaultPairsetTime())
        add(0, getDefaultPairsetFurniture())
        add(0, getDefaultPairsetGreatBritain())
        add(0, getDefaultPairsetHumanFace())
        add(0, getDefaultPairsetHumanBody())
        add(0, getDefaultPairsetHouseBasic())

    }
}









