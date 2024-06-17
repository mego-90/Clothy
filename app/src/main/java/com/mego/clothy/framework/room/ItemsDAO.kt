package com.mego.clothy.framework.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mego.clothy.domain.Category
import com.mego.clothy.domain.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsDAO {
    @Insert( onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewCategory( category: Category ) : Long
    @Update
    suspend fun editCategory( updatedCategory: Category )
    @Delete
    suspend fun deleteCategory( category: Category )
    @Query("SELECT * FROM categories")
    fun getAllCategories() : Flow<List<Category>>

    @Insert( onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemToCategory( category: Category, item: Item )
    @Update
    suspend fun editItem( category: Category, item: Item )
    @Delete
    suspend fun deleteItem(item : Item )
    @Query("SELECT * FROM items WHERE category_id = :categoryID")
    fun getAllItemsInCategory(categoryID : Long ): Flow<List<Item>>

}