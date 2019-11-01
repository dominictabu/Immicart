package com.andromeda.immicart.moneyMath

import java.math.BigDecimal

class Discounts {
//    The amount of money one is saving by using our platform eg from offers

    var discountedPrice: Double = 0.00
    var sellingPrice: Double = 0.00




    fun discounts():BigDecimal{
        var discount:BigDecimal = (sellingPrice.minus(discountedPrice)).toBigDecimal()

        return discount
    }


}