package com.mego.clothy.framework.room

import com.mego.clothy.data.ItemsDataSource
import com.mego.clothy.domain.Category
import com.mego.clothy.domain.Item

class ItemsRoomDatasource (private val itemsDAO: ItemsDAO): ItemsDataSource {
    override suspend fun addNewCategory(category: Category) =
        itemsDAO.addNewCategory(category)

    override suspend fun editCategory(updatedCategory: Category) =
        itemsDAO.editCategory(updatedCategory)

    override suspend fun deleteCategory(category: Category) =
        itemsDAO.deleteCategory(category)

    override fun getAllCategories() =
        itemsDAO.getAllCategories()

    override suspend fun addItemToCategory(category: Category, item: Item) =
        itemsDAO.addItemToCategory(category, item)

    override suspend fun editItem(category: Category, item: Item) =
        itemsDAO.editItem(category, item)

    override suspend fun deleteItem(item: Item) =
        itemsDAO.deleteItem(item)

    override fun getAllItemsInCategory(category: Category) =
        itemsDAO.getAllItemsInCategory( categoryID = category.id )

}