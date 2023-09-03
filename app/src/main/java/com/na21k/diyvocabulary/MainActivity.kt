package com.na21k.diyvocabulary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.na21k.diyvocabulary.databinding.ActivityMainBinding
import com.na21k.diyvocabulary.ui.auth.AuthActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_tags
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: BottomNavigationView = mBinding.navView
        navView.setupWithNavController(navController)

        requestAuth()
    }

    private fun requestAuth() {
        val authIntent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }
}
