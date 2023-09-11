package com.na21k.diyvocabulary

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivityViewModel(application: Application) : BaseViewModel(application) {

    private val mAuth = Firebase.auth

    val user: FirebaseUser?
        get() = mAuth.currentUser
    val isUserSignedIn: Boolean
        get() = user != null
}
