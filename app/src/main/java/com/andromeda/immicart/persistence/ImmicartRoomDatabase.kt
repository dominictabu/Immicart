package com.andromeda.immicart.Scanning.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.andromeda.immicart.delivery.DeliveryCart
import com.andromeda.immicart.delivery.delivery_location.Place
import com.andromeda.immicart.delivery.choose_store.Store
import com.andromeda.immicart.delivery.choose_store.storeDao
import com.andromeda.immicart.delivery.persistence.CurrentLocation
import com.andromeda.immicart.delivery.persistence.DeliveryDao
import com.andromeda.immicart.delivery.persistence.DeliveryLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(Cart::class, DeliveryCart::class, Store::class, Place::class, CurrentLocation::class), version = 12, exportSchema = false)
public abstract class ImmicartRoomDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao
    abstract fun storeDao(): storeDao
    abstract fun deliveryDao(): DeliveryDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ImmicartRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ImmicartRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImmicartRoomDatabase::class.java,
                    "word_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.cartDao())
                }
            }
        }

        suspend fun populateDatabase(cartDao: CartDao) {
            // Delete all content here.
            cartDao.deleteAll()


            // TODO: Add your own cart!
        }
    }
}