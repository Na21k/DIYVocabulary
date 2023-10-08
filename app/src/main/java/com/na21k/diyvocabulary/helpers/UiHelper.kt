package com.na21k.diyvocabulary.helpers

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.na21k.diyvocabulary.R

fun showErrorAlertDialog(context: Context, @StringRes errorTextResource: Int) {
    AlertDialog.Builder(context)
        .setIcon(R.drawable.ic_error_24)
        .setTitle(R.string.error_alert_title)
        .setMessage(errorTextResource)
        .setPositiveButton(android.R.string.ok) { _, _ -> }
        .show()
}

fun showErrorSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).show()
}

fun showErrorSnackbar(view: View, @StringRes stringRes: Int) {
    Snackbar.make(view, stringRes, Snackbar.LENGTH_INDEFINITE).show()
}

fun showSnackbar(view: View, @StringRes stringRes: Int) {
    Snackbar.make(view, stringRes, Snackbar.LENGTH_LONG).show()
}

fun showToast(context: Context, @StringRes stringRes: Int, duration: Int) {
    Toast.makeText(context, stringRes, duration).show()
}

fun setTextIfEmpty(editText: EditText, newText: String?) {
    if (editText.text.isEmpty()) {
        editText.setText(newText)
    }
}
