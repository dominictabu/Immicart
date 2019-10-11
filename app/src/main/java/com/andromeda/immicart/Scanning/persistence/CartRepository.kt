package com.andromeda.immicart.Scanning.persistence

import androidx.lifecycle.LiveData


/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class CartRepository(private val cartDao: CartDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allScannedItems: LiveData<List<Cart>> = cartDao.getAllCartItems()
//    val allScannedItemsNumber: LiveData<I> = cartDao.getAllCartItems()


    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    suspend fun insert(cart: Cart) {
        cartDao.insert(cart = cart)
    }

    suspend fun deleteById(id: Int) {
        cartDao.deleteById(id = id)
    }
    suspend fun updateQuantity(id: Int, newQuantity: Int) {
        cartDao.updateQuantity(id = id, quantity=newQuantity)
    }
}