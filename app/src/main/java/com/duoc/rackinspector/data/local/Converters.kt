package com.duoc.rackinspector.data.local

import androidx.room.TypeConverter
import com.duoc.rackinspector.data.model.ComponentItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromComponentList(value: List<ComponentItem>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toComponentList(value: String): List<ComponentItem> {
        val listType = object : TypeToken<List<ComponentItem>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }
}