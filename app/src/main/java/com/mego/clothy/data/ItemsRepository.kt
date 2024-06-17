package com.mego.clothy.data

import com.mego.clothy.domain.Category
import com.mego.clothy.domain.Item

class ItemsRepository (private val itemsDataSource: ItemsDataSource) {

    suspend fun addNewCategory( category: Category) =
        itemsDataSource.addNewCategory(category)

    suspend fun editCategory( updatedCategory: Category) =
        itemsDataSource.editCategory(updatedCategory)

    suspend fun deleteCategory( category: Category) =
        itemsDataSource.deleteCategory(category)

    fun getAllCategories() =
        itemsDataSource.getAllCategories()

    suspend fun addItemToCategory(category: Category, item: Item) =
        itemsDataSource.addItemToCategory(category, item)

    suspend fun editItem(category: Category, item: Item) =
        itemsDataSource.editItem(category, item)

    suspend fun deleteItem(item : Item) =
        itemsDataSource.deleteItem(item)

    fun getAllItemsInCategory(category : Category) =
        itemsDataSource.getAllItemsInCategory(category)

}