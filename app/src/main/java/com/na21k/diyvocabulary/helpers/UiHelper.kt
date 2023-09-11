package com.na21k.diyvocabulary.helpers

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.na21k.diyvocabulary.R

fun showErrorAlertDialog(context: Context, @StringRes errorTextResource: Int) {
    AlertDialog.Builder(context)
        .setIcon(R.drawable.ic_error_24)
        .setTitle(R.string.error_alert_title)
        .setMessage(errorTextResource)
        .setPositiveButton(android.R.string.ok) { _, _ -> }
        .show()
}
