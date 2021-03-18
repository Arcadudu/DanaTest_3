package ru.arcadudu.danatest_v030.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R

class DtSwipeDecorator(viewHolder: RecyclerView.ViewHolder, private val context: Context) {

    private val itemView = viewHolder.itemView
    private val height = (itemView.bottom - itemView.top).toFloat()
    private val width = height / 3.0f
    private val paint = Paint()

    init {
        paint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    fun getRedPaint(): Paint {
        paint.color =
            ResourcesCompat.getColor(context.resources, R.color.dt3_error_red_70, context.theme)
        return paint
    }

    fun getVioletPaint(): Paint {
        paint.color =
            ResourcesCompat.getColor(context.resources, R.color.dt3_brand_violet_70, context.theme)
        return paint
    }

    fun getLeftSwipeBackground(horizontalOffset: Float): RectF {
        return RectF(
            itemView.right.toFloat() + horizontalOffset,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
    }

    fun getLeftSwipeBackground(): RectF {
        return RectF(
            itemView.right.toFloat() - 2 * width,
            itemView.top.toFloat() + width,
            itemView.right.toFloat() - width,
            itemView.bottom.toFloat() - width
        )
    }

    fun getLeftSwipeIcon(): Bitmap? {
        val iconDeleteDrawable: Drawable? =
            ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.icon_delete_error_white,
                null
            )
        return drawableToBitmap(iconDeleteDrawable as Drawable)
    }


    fun getRightSwipeBackground(horizontalOffset: Float): RectF {
        return RectF(
            itemView.left.toFloat(),
            itemView.top.toFloat(),
            itemView.left.toFloat() + horizontalOffset,
            itemView.bottom.toFloat()
        )
    }

    fun getRightSwipeIconDestination(): RectF {
        return RectF(
            itemView.left.toFloat() + width,
            itemView.top.toFloat() + width,
            itemView.left.toFloat() + 2 * width,
            itemView.bottom.toFloat() - width
        )
    }

    fun getRightSwipeIcon(): Bitmap? {
        val iconPlayTestDrawable: Drawable? =
            ResourcesCompat.getDrawable(context.resources, R.drawable.icon_play_white, null)

        return drawableToBitmap(iconPlayTestDrawable as Drawable)
    }


}