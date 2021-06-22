package com.example.favoriteplaces.models

import android.os.Parcel
import android.os.Parcelable

/**
 * This is the data class created to store the user inputs into the application
 * Parcelable --> change it to variable we can use in putExtra and transfer
 * information from activity to activity. Instead of using Serializable for the data class
 * used Parcelable since it is considerably faster.
 */
data class FavoritePlaceModel(
    val id: Int,
    val title: String?,
    val image: String?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoritePlaceModel> {
        override fun createFromParcel(parcel: Parcel): FavoritePlaceModel {
            return FavoritePlaceModel(parcel)
        }

        override fun newArray(size: Int): Array<FavoritePlaceModel?> {
            return arrayOfNulls(size)
        }
    }
}