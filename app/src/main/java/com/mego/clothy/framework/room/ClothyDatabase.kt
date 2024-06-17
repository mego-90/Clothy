package com.mego.clothy.framework.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mego.clothy.domain.Category
import com.mego.clothy.domain.Item

@Database ( entities = [Category::class,Item::class], version = 1)
@TypeConverters(LocalDateTimeTypeConverter::class)
abstract class ClothyDatabase :RoomDatabase(){

    abstract val itemsDAO : ItemsDAO

    companion object {
        const val databaseName = "Clothy_Database"
    }
}