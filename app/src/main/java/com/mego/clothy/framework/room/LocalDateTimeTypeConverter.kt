package com.mego.clothy.framework.room

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeTypeConverter {

    @TypeConverter
    fun toStringDateTime(localDateTime: LocalDateTime?) : String? =
        localDateTime?.toString()

    @TypeConverter
    fun toLocalDateTime(dateTimeInString: String?):LocalDateTime? =
        dateTimeInString?.let {
            LocalDateTime.parse(it) }

}