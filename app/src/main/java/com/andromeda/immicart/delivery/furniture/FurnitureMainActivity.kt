package com.andromeda.immicart.delivery.furniture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.andromeda.immicart.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class FurnitureMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_furniture_main)
        setupBottomNavMenu(findNavController(viewId = R.id.furniture_nav_host_fragment))
    }


    fun performFragmnetTransaction(fragment: Fragment) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.content_products, fragment).commit()
    }


    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav?.setupWithNavController(navController)
    }


    fun navigateToSearch() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.selectedItemId = R.id.searchFragment;

    }
}
