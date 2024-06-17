package com.mego.clothy.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "categories")
data class Category(
    val name: String,
    val iconResourceId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var order: Int = 1
}
