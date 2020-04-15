package com.andromeda.immicart.delivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.choose_store.Store
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductsPageActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductsViewModel

    companion object {
        private val PRODUCT_ID = "PRODUCT_ID"
        private  val CURRENT_STORE = "CURRENT_STORE"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_products_page)
//        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(ProductsViewModel::class.java)


//        val manager = supportFragmentManager
//        val transaction = manager.beginTransaction()
//        transaction.replace(R.id.content_products, ProductsFragment()).commit()

        if(intent.hasExtra(CURRENT_STORE)) {
            val store = intent.getSerializableExtra(CURRENT_STORE) as Store
            viewModel.setCurrentStore(store)

        }
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
