package com.andromeda.immicart.delivery.checkout

import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.andromeda.immicart.R
import com.andromeda.immicart.delivery.DeliveryCart
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_personal_cart.*
import kotlinx.android.synthetic.main.personal_cart_item.view.*
import java.text.DecimalFormat

class DeliveryCartFragment : Fragment() {
    private lateinit var adapter: PersonalShoppingCartAdapter



    companion object {
        fun newInstance() = DeliveryCartFragment()
    }

    private lateinit var deliveryCartViewModel: DeliveryCartViewModel
    lateinit var cartItems: List<DeliveryCart>

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

        deliveryCartViewModel = ViewModelProviders.of(activity!!).get(DeliveryCartViewModel::class.java)

        initializeRecyclerView()
        deliveryCartViewModel.allDeliveryItems().observe(activity!!, Observer { items ->
            // Update the cached copy of the words in the adapter.
            items?.let {
                //                cartItems = items
//                badge.text = it.count().toString()
//                adapter.setCartItems(it)



                if (items.isEmpty()) {
                    rv_personal_cart.setVisibility(View.GONE);
                    empty_items_text_view.setVisibility(View.VISIBLE);
                    checkoutlayout_disabled.visibility = View.VISIBLE
                    checkoutlayout.visibility = View.GONE
                }
                else {
                    rv_personal_cart.setVisibility(View.VISIBLE);
                    empty_items_text_view.setVisibility(View.GONE);
                    checkoutlayout.visibility = View.VISIBLE
                    checkoutlayout_disabled.visibility = View.GONE

                }

                cartItems = items
                adapter.submitList(it)

                var total = 0
                it.forEach {
                    val quantity = it.quantity
                    val unitPrice = it.offerPrice.toInt()
                    val subtotal = quantity * unitPrice
                    total += subtotal
                }

                val formatter = DecimalFormat("#,###,###");
                val totalFormattedString = formatter.format(total);

                total_price_tv.setText("KES " + totalFormattedString)
            }
        })

        checkoutlayout.setOnClickListener {
//            val fragment = DeliveryDetailsFragment()
//            (activity!! as DeliveryCartActivity).performFragmnetTransaction(fragment)
//            val intent: Intent = Intent(activity, PlcaeOrderActivity::class.java)
//            startActivity(intent)

            findNavController().navigate(R.id.action_deliveryCartFragment_to_deliveryDetailsFragment)
        }


        back_action.setOnClickListener {
            activity?.let {
                it.finish()
            }
        }

    }

    private fun cartItemClicked(cartItem : DeliveryCart, newQuantity: Int) {
//        Toast.makeText(this, "Clicked: ${cartItem.name}", Toast.LENGTH_LONG).show()
        //TODO Change Quantity
        Log.d(TAG, "newQuantity : $newQuantity , $cartItem ")

        deliveryCartViewModel.updateQuantity(cartItem.key, newQuantity)
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

        adapter = PersonalShoppingCartAdapter(
            activity!!,
            { cartItem: DeliveryCart, newQuantity: Int ->
                cartItemClicked(
                    cartItem,
                    newQuantity
                )
            },
            { cartItem: DeliveryCart -> removeItemClicked(cartItem) })
        rv_personal_cart?.layoutManager = linearLayoutManager

        adapter.registerAdapterDataObserver(emptyObserver)
        rv_personal_cart?.adapter = adapter

    }

    private val emptyObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            val adapter = rv_personal_cart.getAdapter()
            if (adapter != null && empty_items_text_view != null) {
                if (adapter!!.getItemCount() == 0) {
                    empty_items_text_view.setVisibility(View.VISIBLE)
                    rv_personal_cart.setVisibility(View.GONE)
                } else {
                    empty_items_text_view.setVisibility(View.GONE)
                    rv_personal_cart.setVisibility(View.VISIBLE)
                }
            }
        }
    }

    private fun removeItemClicked(cartItem: DeliveryCart) {
        //Change Quantity via Room
//        Toast.makeText(this, "Removed: ${cartItem.name}", Toast.LENGTH_LONG).show()

        val id = cartItem.itemId
        deliveryCartViewModel.deleteById(id)
    }


}




class PersonalShoppingCartAdapter(var context: Context, val changeQuantityClickListener: (DeliveryCart, Int) -> Unit, val removeItemClickListener: (DeliveryCart) -> Unit) :
    ListAdapter<DeliveryCart, PersonalShoppingCartAdapter.ViewHolder>(
        ProductsDiffCallback()
    ) {

    lateinit var changeCartItemNumberPopUp: PopupWindow

    /*
    * Later, we need to move this to the binding library
    * And apply MVVM architecture
    * */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.personal_cart_item, parent, false)
        return ViewHolder(layout)
    }

//    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), removeItemClickListener)

    }

    internal fun showChangeCartItemNumberPopUp(context: Context, p: Point, currentQuantity: String, quantityTv: TextView, clickListener: (DeliveryCart, Int) -> Unit, cart: DeliveryCart) {
        // Inflate the popup_layout.xml
        //        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.);
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.add_to_cart_pop_up, null)

        // Creating the PopupWindow
        changeCartItemNumberPopUp = PopupWindow(context)
        changeCartItemNumberPopUp.setContentView(layout)
        changeCartItemNumberPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT)
        changeCartItemNumberPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
        changeCartItemNumberPopUp.setFocusable(true)
        changeCartItemNumberPopUp.setElevation(20f)


        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        //        int OFFSET_X = -20;
        //        int OFFSET_Y = 95;

        val OFFSET_X = 0
        val OFFSET_Y = 0

        // Clear the default translucent background
        changeCartItemNumberPopUp.setBackgroundDrawable(BitmapDrawable())

        // Displaying the popup at the specified location, + offsets.
        changeCartItemNumberPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y)


        // Getting a reference to Close button, and close the popup when clicked.
        val add = layout.findViewById<View>(R.id.add_quantity) as ImageButton
        val quantityText = layout.findViewById<View>(R.id.quantityTv) as TextView
        quantityText.text = currentQuantity
        var currentQuantityInt: Int = currentQuantity.toInt()
        val removeBtn = layout.findViewById<View>(R.id.reduceQuantityBtn) as ImageButton

        if (currentQuantityInt <= 1) {
            (removeBtn as ImageButton).setImageResource(R.drawable.ic_delete_primary_color_24dp)

        } else {
            (removeBtn as ImageButton).setImageResource(R.drawable.ic_remove_primary_color_24dp)

        }

        add.setOnClickListener {

            currentQuantityInt++
            quantityText.text = currentQuantityInt.toString()
            (removeBtn as ImageButton).setImageResource(R.drawable.ic_remove_primary_color_24dp)

        }


        removeBtn.setOnClickListener {
            if (currentQuantityInt > 1) {
                currentQuantityInt--
                quantityText.text = currentQuantityInt.toString()

            } else {
                (removeBtn as ImageButton).setImageResource(R.drawable.ic_delete_primary_color_24dp)
                currentQuantityInt--
                changeCartItemNumberPopUp.dismiss()


            }

        }

        changeCartItemNumberPopUp.setOnDismissListener {
            quantityTv.text = currentQuantityInt.toString()
            clickListener(cart, currentQuantityInt)

        }


//         val newQuantity = (quantityTv.text).toString().toInt()
//         return currentQuantityInt



    }



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(cartItem: DeliveryCart, removeItemClickListener: (DeliveryCart) -> Unit) {

            itemView.itemName.text = cartItem.name
            itemView.itemDescription.text = "2kg"

            val productImage = cartItem.image_url
//            TODO update the names?

            val Oldprice = cartItem.quantity * cartItem.normalPrice
            val _formatter = DecimalFormat("#,###,###");
            val OldpriceFormattedString = _formatter.format(Oldprice.toInt());
            itemView.itemOldPrice.text =  "KES " + OldpriceFormattedString
            itemView.itemOldPrice.paintFlags = itemView.itemOldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG

            val price_ = cartItem.quantity * cartItem.offerPrice
            val formatter = DecimalFormat("#,###,###");
            val priceFormattedString = formatter.format(price_.toInt());

            itemView.itemNewPrice.text = "KES " + priceFormattedString

            itemView.itemNumber.setText(cartItem.quantity.toString())
            Glide.with(itemView.context).load(productImage).into(itemView.cartImage)

            itemView.itemNumber.setOnClickListener {
                val currentQuantity = itemView.itemNumber.text
                val location = IntArray(2)
                itemView.itemNumber!!.getLocationOnScreen(location)
                val x = location[0]
                val y = location[1]
                val point = Point(x, y)
                showChangeCartItemNumberPopUp(
                    itemView.context,
                    point,
                    currentQuantity as String,
                    itemView.itemNumber,
                    changeQuantityClickListener,
                    cartItem
                )
//                val newQuantity = (itemView.quantity_tv.text).toString().toInt()
//                Log.d(TAG, "newQuantity textView: ${newQuantity}")

            }

            itemView.deleteText.setOnClickListener{
//                Log.d(TAG, "Remove Item Clicked")
                removeItemClickListener(cartItem)
            }


//            How to access an edittext

//            itemView.etAmount.text = cartItem.amount

        }

    }
}

class ProductsDiffCallback : DiffUtil.ItemCallback<DeliveryCart>() {
    override fun areItemsTheSame(oldItem: DeliveryCart, newItem: DeliveryCart): Boolean {
        return oldItem?.itemId == newItem?.itemId
    }

    override fun areContentsTheSame(oldItem: DeliveryCart, newItem: DeliveryCart): Boolean {
        return oldItem == newItem
    }
}
