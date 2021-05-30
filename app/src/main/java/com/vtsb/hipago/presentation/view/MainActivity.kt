package com.vtsb.hipago.presentation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.vtsb.hipago.R
import com.vtsb.hipago.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val binding = ActivityMainBinding.inflate(layoutInflater)
        //val navHostFragment = binding.navHostFragment as NavHostFragment


        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)

        navController.graph = navGraph

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