package com.example.favoriteplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.favoriteplaces.R
import com.example.favoriteplaces.adapters.FavoritePlaceAdapter
import com.example.favoriteplaces.database.DatabaseHandler
import com.example.favoriteplaces.models.FavoritePlaceModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.floatingActionButtonFavoritePlace).setOnClickListener {
            val intent = Intent(this, AddFavoritePlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }

        getFavoritePlacesListFromLocalDatabase()
    }

    /**
     * This function is to set up the recycler view that was created
     * Create layout manager and assign it as recycler view property
     * Use the adapter (FavoritePlaceAdapter) for the recycler view
     */
    private fun setUpFavoritePlaceModel(favoritePlaceList: ArrayList<FavoritePlaceModel>){
        val recyclerViewFavPlaces = findViewById<RecyclerView>(R.id.recyclerViewFavoritePlaceList)
        recyclerViewFavPlaces.layoutManager = LinearLayoutManager(this)

        recyclerViewFavPlaces.setHasFixedSize(true)

        val placesAdapter = FavoritePlaceAdapter(this, favoritePlaceList)
        recyclerViewFavPlaces.adapter = placesAdapter
    }

    /**
     * This function is to read the database and print it as a recycler view
     */
    private fun getFavoritePlacesListFromLocalDatabase() {
        val databaseHandler = DatabaseHandler(this)
        val getFavoriteList: ArrayList<FavoritePlaceModel> = databaseHandler.getFavoritePlace()

        if (getFavoriteList.size > 0) {
            findViewById<RecyclerView>(R.id.recyclerViewFavoritePlaceList).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textViewNoRecordsFound).visibility = View.GONE
            setUpFavoritePlaceModel(getFavoriteList)
        } else {
            findViewById<RecyclerView>(R.id.recyclerViewFavoritePlaceList).visibility = View.GONE
            findViewById<TextView>(R.id.textViewNoRecordsFound).visibility = View.VISIBLE
        }
    }

    /**
     * Function that refreshes the main page when activity is finished on AddFavoritePlaceActivity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getFavoritePlacesListFromLocalDatabase()
            } else {
                Log.e("Activity", "Cancelled or back pressed")
            }
        }
    }

    companion object {
        // Variable to refresh the main page after favorite place addition
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
}