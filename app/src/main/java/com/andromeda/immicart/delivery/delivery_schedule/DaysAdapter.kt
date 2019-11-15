package com.andromeda.immicart.delivery.delivery_schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.andromeda.immicart.R
import java.util.*

class DaysAdapter(var context: Context) : Adapter<DaysAdapter.DaysViewHolder>() {

    var deliveryDays = listOf<DeliveryDay>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {


        val layout = LayoutInflater.from(context).inflate(R.layout.delivery_schedule, parent, false)
        return DaysViewHolder(layout)

    }

    override fun getItemCount(): Int {
        return deliveryDays.size

    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        var day = deliveryDays[position]


    }


    class DaysViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }
    fun  dayOfWeek(){
        val calendar = Calendar.getInstance()
        var day = calendar.get(Calendar.DAY_OF_WEEK)


    }
}