package com.andromeda.immicart.moneyMath

import java.math.BigDecimal

open class ItemCost  {

    var unitCost: Double = 0.00
    var numberOfItems: Int = 0



    fun itemCost():BigDecimal{
        var itemTotal: BigDecimal =  (unitCost.times(numberOfItems.toDouble())).toBigDecimal()
        return itemTotal

    }

}