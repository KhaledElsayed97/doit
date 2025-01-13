package dev.khaled.doit.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    @DocumentId var id:String="",
    var title:String="",
    var description:String="",
    var date:String="",
    var time:String="",
    var isCompleted:Boolean=false,
    var isFavorite:Boolean=false,
    var priority: TaskPriority) : Parcelable

