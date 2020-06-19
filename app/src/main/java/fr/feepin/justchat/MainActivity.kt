package fr.feepin.justchat

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
        NavigationUI.setupActionBarWithNavController(this, findNavController(R.id.nav_host_fragment))
    }
}