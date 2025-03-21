package com.example.fitpal.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitpal.Comment
import com.example.fitpal.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue


@Entity
data class Post(
    @PrimaryKey val id: String,
    val author: String,
    val title: String,
    val text: String,
    val image: String,
    var likes: List<String>,
    var comments: List<Comment>,
    val uploadDate: String,
    val lastUpdated: Long? = null
){

    companion object {

        var lastUpdated: Long
            get() = MyApplication.Globals.context?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0
            set(value) {
                MyApplication.Globals.context
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.apply {
                        edit()
                            .putLong(LOCAL_LAST_UPDATED, value)
                            .apply()
                    }
            }


        const val ID_KEY = "id"
        const val AUTHOR_KEY = "author"
        const val TITLE_KEY = "title"
        const val TEXT_KEY = "text"
        const val IMAGE_KEY = "image"
        const val LIKES_KEY = "likes"
        const val COMMENTS_KEY = "comments"
        const val UPLOAD_DATE_KEY = "uploadDate"
        const val LAST_UPDATED = "lastUpdated" //timestamp of the last update
        const val LOCAL_LAST_UPDATED = "locaStudentLastUpdated" //timestamp of the local last update

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val author = json[AUTHOR_KEY] as? String ?: ""
            val title = json[TITLE_KEY] as? String ?: ""
            val text = json[TEXT_KEY] as? String ?: ""
            val image = json[IMAGE_KEY] as? String ?: ""
            val likes = json[LIKES_KEY] as? List<String> ?: intArrayOf()
            val uploadDate = json[UPLOAD_DATE_KEY] as? String ?: ""
            val comments = json[COMMENTS_KEY] as? List<Comment> ?: intArrayOf()
            val timeStamp = json[LAST_UPDATED] as? Timestamp
            val lastUpdatedLongTimestamp = timeStamp?.toDate()?.time
            return Post(
                id = id,
                author = author,
                title = title,
                text = text,
                image = image,
                likes = likes as List<String>,
                comments = comments as List<Comment>,
                uploadDate = uploadDate,
                lastUpdated = lastUpdatedLongTimestamp
            )
        }
    }

    val json: Map<String, Any>
        get() = hashMapOf(
            ID_KEY to id,
            AUTHOR_KEY to author,
            TITLE_KEY to title,
            TEXT_KEY to text,
            IMAGE_KEY to image,
            LIKES_KEY to likes,
            COMMENTS_KEY to comments,
            UPLOAD_DATE_KEY to uploadDate,
            LAST_UPDATED to FieldValue.serverTimestamp(),
            LOCAL_LAST_UPDATED to FieldValue.serverTimestamp()
        )

}
