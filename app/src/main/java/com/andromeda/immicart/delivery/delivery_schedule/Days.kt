package com.andromeda.immicart.delivery.delivery_schedule

import android.annotation.TargetApi
import android.os.Build
import java.util.*

class Days {
    @TargetApi(Build.VERSION_CODES.O)
    fun today(date:Date):String{
        val current = Calendar.DAY_OF_WEEK
    }

    fun otherDay(date: Date){}
}