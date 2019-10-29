package com.andromeda.immicart.shopping_cart.temporary

import java.math.BigDecimal

class PriceCalculations {
    var unitPrice: Double = 0.00
    var itemcount: Int = 0
    var totalUnitPrice: Double = 0.00


    fun totalPrice (priceInBigDecimal: BigDecimal): BigDecimal{
        totalUnitPrice = unitPrice.times(itemcount)

        var priceInBigDecimal:BigDecimal = totalUnitPrice.toBigDecimal()
        return totalPrice(priceInBigDecimal)
    }

    fun save(amountSaved: BigDecimal):BigDecimal{
        var sellingPrice: Double = 0.00
        var discountedPrice: Double = 0.00
        var amountSaved:BigDecimal = (sellingPrice.minus(discountedPrice)).toBigDecimal()
        return save(amountSaved)
    }



}