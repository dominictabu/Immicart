package com.andromeda.immicart.delivery.delivery_schedule;

import java.util.Calendar;
import java.util.Date;

public class DayOfTheWeek {
    Calendar calendar = Calendar.getInstance();
    Date date;

   void today(){
        date = calendar.getTime();
    }
}
