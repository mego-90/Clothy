package com.mego.clothy.data

import com.mego.clothy.domain.Category
import com.mego.clothy.domain.Item
import kotlinx.coroutines.flow.Flow

interface ItemsDataSource {

    suspend fun addNewCategory( category: Category ) : Long
    suspend fun editCategory( updatedCategory: Category )
    suspend fun deleteCategory( category: Category )
    fun getAllCategories() : Flow<List<Category>>

    suspend fun addItemToCategory( category: Category, item: Item )
    suspend fun editItem( category: Category, item: Item )
    suspend fun deleteItem(item : Item )
    fun getAllItemsInCategory(category : Category ): Flow<List<Item>>

}