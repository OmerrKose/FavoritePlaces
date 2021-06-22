package com.example.favoriteplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.favoriteplaces.R
import com.example.favoriteplaces.models.FavoritePlaceModel

class FavoritePlaceDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_place_details)

        var favoritePlaceDetailModel: FavoritePlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            favoritePlaceDetailModel =
                intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS) as FavoritePlaceModel?
        }

        /**
         * If there is a favoritePlace that was added set the page accordingly with the values
         */
        if (favoritePlaceDetailModel != null) {
            setSupportActionBar(findViewById(R.id.toolbarDetailsPage))
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = favoritePlaceDetailModel.title

            // Set the toolbar
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarDetailsPage).setNavigationOnClickListener {
                onBackPressed()
            }

            // Set the image view with the read image information from the model that was created
            findViewById<AppCompatImageView>(R.id.imageViewDetailsPlaceImage).setImageURI(
                Uri.parse(favoritePlaceDetailModel.image)
            )

            // Set the description text view with the read value from the model that was created
            findViewById<TextView>(R.id.textViewDetailsDescription).text = favoritePlaceDetailModel.description

            // Set the location text view with the read value from the model that was created
            findViewById<TextView>(R.id.textViewDetailsLocation).text = favoritePlaceDetailModel.location
        }
    }
}