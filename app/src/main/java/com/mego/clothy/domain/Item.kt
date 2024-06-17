package com.mego.clothy.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity (
    tableName = "items",
    foreignKeys = [ ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"]) ] )
data class Item(

    @ColumnInfo(name = "category_id")
    val categoryID : Long,
    val imagePath : String,
    var formality : String = Formality.UNSPECIFIED.name,
    var suitableWeather: String = Weather.UNSPECIFIED.name,
    var colorHex: String
    )
{
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
    @ColumnInfo(name = "added_date_time")
    var addedDateTime = LocalDateTime.now()
    var lastWornDate: LocalDateTime? = null
    @Ignore
    var isSelected = false // selection for action mode
    var liked = false
    var brand: String = ""
    var notes: String = ""
}