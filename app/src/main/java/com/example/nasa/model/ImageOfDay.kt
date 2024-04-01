package com.example.nasa.model

import android.os.Parcel
import android.os.Parcelable
import com.example.nasa.database.ImageOfDayDbModel

data class ImageOfDay(
    val url: String,
    val mediaType: String,
    val title: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(mediaType)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageOfDay> {
        val empty = ImageOfDay("", "", "")

        override fun createFromParcel(parcel: Parcel): ImageOfDay {
            return ImageOfDay(parcel)
        }

        override fun newArray(size: Int): Array<ImageOfDay?> {
            return arrayOfNulls(size)
        }
    }
}


fun ImageOfDay.toDbModel(): ImageOfDayDbModel {

    return ImageOfDayDbModel(

        url = this.url,
        mediaType = this.mediaType,
        title = this.title,
    )
}