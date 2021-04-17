package ru.arcadudu.danatest_v030.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.View

class DtDialogFactory(private var activity: Activity, dialogStyleResourceId:Int) {

    private var requiredDialogBuilder = AlertDialog.Builder(activity, dialogStyleResourceId)


    fun getDialogInstance(dialogViewForBinding:View): AlertDialog {
        return requiredDialogBuilder.setView(dialogViewForBinding).create()
    }

    fun getDialogViewForBinding(layoutResourceId: Int): View {
        return activity.layoutInflater.inflate(layoutResourceId, null, false)
    }

}

// private fun getDtDialogInstance(context:Context, layoutResourceId: Int): AlertDialog {
//        val requiredDialogBuilder = AlertDialog.Builder(context, R.style.dt_CustomAlertDialog)
//        val requiredDialogView = this.layoutInflater.inflate(layoutResourceId, null, false)
//        requiredDialogBuilder.setView(requiredDialogView)
//        return requiredDialogBuilder.create()
//    }