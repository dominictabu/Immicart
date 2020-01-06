package com.andromeda.immicart.delivery

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.andromeda.immicart.R
import com.google.android.material.bottomnavigation.BottomNavigationView

import kotlinx.android.synthetic.main.activity_products_page.*

class ProductsPageActivity : AppCompatActivity() {


    companion object {
        private val PRODUCT_ID = "PRODUCT_ID"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_products_page)
//        setSupportActionBar(toolbar)

//        val manager = supportFragmentManager
//        val transaction = manager.beginTransaction()
//        transaction.replace(R.id.content_products, ProductsFragment()).commit()

        setupBottomNavMenu(findNavController(viewId = R.id.my_nav_host_fragment))
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
