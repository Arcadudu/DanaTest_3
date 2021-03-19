package ru.arcadudu.danatest_v030.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class DtSwipeDecorator(viewHolder: RecyclerView.ViewHolder, private val context: Context) {

    private val itemView = viewHolder.itemView
    private val height = (itemView.bottom - itemView.top).toFloat()
    private val width = height / 3.0f


    fun getSwipeBitmap(@DrawableRes iconId: Int): Bitmap? {
        val iconDrawable: Drawable? =
            ResourcesCompat.getDrawable(context.resources, iconId, context.theme)
        return drawableToBitmap(iconDrawable as Drawable)
    }

    fun getSwipePaint(@ColorRes colorResourceId: Int): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(context.resources, colorResourceId, context.theme)
        }
    }

    fun getSwipeBackgroundRectF(horizontalOffset: Float): RectF {
        val rectLeft =
            if (horizontalOffset < 0) itemView.right.toFloat() + horizontalOffset else itemView.left.toFloat()
        val rectRight =
            if (horizontalOffset < 0) itemView.right.toFloat() else itemView.left.toFloat() + horizontalOffset

        return RectF(
            rectLeft,
            itemView.top.toFloat(),
            rectRight,
            itemView.bottom.toFloat()
        )
    }

    fun getSwipeIconDestinationRectF(horizontalOffset: Float): RectF {
        val rectLeft =
            if (horizontalOffset < 0) itemView.right.toFloat() - 2 * width else itemView.left.toFloat() + width
        val rectRight =
            if (horizontalOffset < 0) itemView.right.toFloat() - width else itemView.left.toFloat() + 2 * width
        return RectF(
            rectLeft,
            itemView.top.toFloat() + width,
            rectRight,
            itemView.bottom.toFloat() - width
        )
    }



}