package com.na21k.diyvocabulary

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.na21k.diyvocabulary.databinding.ActivityMainBinding
import com.na21k.diyvocabulary.helpers.requestAuth
import com.na21k.diyvocabulary.ui.auth.ProfileActivity

class MainActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainActivityViewModel

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

        mViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()

        if (!mViewModel.isUserSignedIn) requestAuth(this)
        else mViewModel.user.let {} //TODO: load data or smth...
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                openProfile()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}
