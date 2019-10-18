package com.andromeda.immicart.shopping_cart.temporary


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.Person
import androidx.recyclerview.widget.LinearLayoutManager

import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.fragment_personal_cart.*

class PersonalCartFragment : Fragment() {
    lateinit var adapter: PersonalShoppingCartAdapter
    lateinit var priceCalculations: PriceCalculations

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

//        Use data binding on this
        // update the ui using livedata...................
        return inflater.inflate(R.layout.fragment_personal_cart, container, false)
//        adapter = PersonalShoppingCartAdapter(context = this.requireContext(), cartItems = List<PersonalCart>(init = Int->Per) )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_personal_cart.adapter = adapter
        rv_personal_cart.layoutManager = LinearLayoutManager(context)

        checkoutlayout.setOnClickListener{

        }

    }

}
