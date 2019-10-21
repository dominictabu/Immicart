package com.andromeda.immicart.shopping_cart.temporary


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.andromeda.immicart.R
import com.andromeda.immicart.Scanning.persistence.Cart
import com.andromeda.immicart.shopping_cart.view_model.PersonalCartViewModel
import kotlinx.android.synthetic.main.fragment_personal_cart.*
import androidx.recyclerview.widget.DividerItemDecoration
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.Drawable
import android.content.res.TypedArray
import java.text.DecimalFormat


class PersonalCartFragment : Fragment() {
    private lateinit var adapter: PersonalShoppingCartAdapter



    private lateinit var personalCartFragmentViewModel: PersonalCartViewModel
    lateinit var cartItems: List<Cart>
    var personalCartItems: ArrayList<Cart> = ArrayList()

    var TAG = "PersonalCartFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_cart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        personalCartFragmentViewModel = ViewModelProviders.of(this).get(PersonalCartViewModel::class.java)

        initializeRecyclerView()
        personalCartFragmentViewModel.allScannedItems.observe(this, Observer { items ->
            // Update the cached copy of the words in the adapter.
            items?.let {
//                cartItems = items
//                badge.text = it.count().toString()
//                adapter.setCartItems(it)
                cartItems = items
                adapter.submitList(it)

                var total = 0
                it.forEach {
                    val quantity = it.quantity
                    val unitPrice = it.price.toInt()
                    val subtotal = quantity * unitPrice
                    total += subtotal
                }

                val formatter = DecimalFormat("#,###,###");
                val totalFormattedString = formatter.format(total);

                total_price_tv.setText("KES " + totalFormattedString)





            }
        })

    }

    private fun cartItemClicked(cartItem : Cart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")

        personalCartFragmentViewModel.updateQuantity(cartItem.primaryKeyId, newQuantity)
    }

    fun initializeRecyclerView() {

        val ATTRS = intArrayOf(android.R.attr.listDivider)
        val linearLayoutManager = LinearLayoutManager(activity!!)

        val a = activity!!.obtainStyledAttributes(ATTRS)
        val divider = a.getDrawable(0)
        val inset = resources.getDimensionPixelSize(com.andromeda.immicart.R.dimen.cart_margin_divider_result)
        val insetDivider = InsetDrawable(divider, inset, 0, 0, 0)
        a.recycle()
        val dividerItemDecoration = DividerItemDecoration(
            rv_personal_cart.getContext(),
            linearLayoutManager.getOrientation()
        )
        dividerItemDecoration.setDrawable(insetDivider)
        rv_personal_cart.addItemDecoration(dividerItemDecoration)

        adapter = PersonalShoppingCartAdapter(activity!!, {cartItem : Cart, newQuantity: Int -> cartItemClicked(cartItem, newQuantity)}, {cartItem : Cart -> removeItemClicked(cartItem)})
        rv_personal_cart.adapter = adapter
        rv_personal_cart.layoutManager = linearLayoutManager
    }

    private fun removeItemClicked(cartItem: Cart) {
        //Change Quantity via Room
//        Toast.makeText(this, "Removed: ${cartItem.name}", Toast.LENGTH_LONG).show()

        val id = cartItem.primaryKeyId
        personalCartFragmentViewModel.deleteById(id)

    }


}
