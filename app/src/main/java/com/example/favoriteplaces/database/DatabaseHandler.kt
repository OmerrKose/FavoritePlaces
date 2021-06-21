package com.example.favoriteplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.favoriteplaces.models.FavoritePlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database Version
        private const val DATABASE_NAME = "FavoritePlaceDatabase" // Database Name
        private const val TABLE_FAVORITE_PLACE = "FavoritePlaceTable" // Table Name

        // Column Names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createFavoritePlaceTable = ("CREATE TABLE " + TABLE_FAVORITE_PLACE + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_TITLE + " TEXT," +
                KEY_IMAGE + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_DATE + " TEXT," +
                KEY_LOCATION + " TEXT," +
                KEY_LATITUDE + " TEXT," +
                KEY_LONGITUDE + " TEXT)")
        db?.execSQL(createFavoritePlaceTable)
    }

    /**
     * When updating the existing database, delete old one create new one
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITE_PLACE")
        onCreate(db)
    }

    /**
     * This function is to add data to the created data base
     * after adding data to the model insert it as a row
     * than return the result
     */
    fun addFavoritePlace(favoritePlace: FavoritePlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, favoritePlace.title)
        contentValues.put(KEY_IMAGE, favoritePlace.image)
        contentValues.put(KEY_DESCRIPTION, favoritePlace.description)
        contentValues.put(KEY_DATE, favoritePlace.date)
        contentValues.put(KEY_LONGITUDE, favoritePlace.longitude)
        contentValues.put(KEY_LATITUDE, favoritePlace.latitude)
        contentValues.put(KEY_LOCATION, favoritePlace.location)

        // Inserting Row
        val result = db.insert(TABLE_FAVORITE_PLACE, null, contentValues)

        db.close()
        return result
    }

    /**
     * This function is to read the database and get the required elements
     * Create a cursor and move in the database,
     * append the database read values into a newly created variable,
     * append the variable into the array list,
     * return the array list
     * If the database is empty then return an empty array list.
     */

    fun getFavoritePlace(): ArrayList<FavoritePlaceModel> {
        val favoritePlaceList = ArrayList<FavoritePlaceModel>() // Array list to store the database read values and return
        val selectQuery = "SELECT  * FROM $TABLE_FAVORITE_PLACE"
        val database = this.readableDatabase

        try {
            val cursor: Cursor = database.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = FavoritePlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),

                        )

                    favoritePlaceList.add(place)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            database.execSQL(selectQuery)
            return ArrayList() // Return an empty array list if the database is empty
        }
        return favoritePlaceList
    }
}