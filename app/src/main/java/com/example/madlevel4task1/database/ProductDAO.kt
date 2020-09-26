package com.example.madlevel4task1.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.madlevel4task1.models.Product

@Dao
interface ProductDAO {

    // (suspend means these methods can't be run outside coroutine which ensures that they won’t be called from the main (ui) thread and cause screen freezes
    @Query("SELECT * FROM product_table")
    suspend fun getAllProducts(): List<Product>

    @Insert
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM product_table")
    suspend fun deleteAllProducts()

}