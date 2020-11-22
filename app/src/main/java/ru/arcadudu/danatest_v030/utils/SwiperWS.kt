package ru.arcadudu.danatest_v030.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.arcadudu.danatest_v030.R
import ru.arcadudu.danatest_v030.models.WordSet

abstract class SwiperWS(context: Context) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP and ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT and ItemTouchHelper.RIGHT
) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.icon_delete_blue)
    private val editIcon = ContextCompat.getDrawable(context, R.drawable.icon_edit_blue)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}