package com.vtsb.hipago.presentation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vtsb.hipago.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater)

//        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//
//        val sp_autoConnect = preferences.getBoolean(SharedPreferencesKeys.AUTO_RECONNECT, false)
//
//        // https://medium.com/@anoopg87/set-start-destination-for-navhostfragment-dynamically-b072a29bfe49
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
//        val navInflater = navHostFragment!!.navController.navInflater
//
//        val navGraph = navInflater.inflate(R.navigation.navigation_graph)
//        val navController = navHostFragment.navController
//
//        if (!sp_autoConnect) {
//            navGraph.startDestination = R.id.insertFragment
//        }
//        navController.graph = navGraph

    }
}