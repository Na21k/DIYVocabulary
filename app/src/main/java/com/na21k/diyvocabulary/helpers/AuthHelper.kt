package com.na21k.diyvocabulary.helpers

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.na21k.diyvocabulary.ui.auth.AuthActivity

fun requestAuth(context: Context) {
    val authIntent = Intent(context, AuthActivity::class.java)
    ContextCompat.startActivity(context, authIntent, null)
}
