package com.example.madlevel4task1.database

import android.content.Context
import com.example.madlevel4task1.models.Product

// repository is used so that we don't need to get an instance of the database and create the productDAO each time we want to access the database
class ProductRepository(context: Context) {

    private val productDao: ProductDAO

    init {
        val database = ShoppingListRoomDatabase.getDatabase(context)
        productDao = database!!.productDao()
    }

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
    }

}
