package com.example.fitpal.utils.extensions
import androidx.room.TypeConverter
import com.example.fitpal.model.Comment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()

        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson<List<String>>(value, listType) // Explicit type
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return gson.toJson(list ?: listOf<String>()) // Handles null properly
    }

    @TypeConverter
    fun fromCommentList(value: String?): List<Comment> {
        if (value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Comment>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toCommentList(list: List<Comment>?): String {
        return gson.toJson(list ?: listOf<Comment>()) // Handles null properly
    }
}