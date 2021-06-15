package com.example.favoriteplaces

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*

class AddFavoritePlace : AppCompatActivity(), View.OnClickListener {

    private val myFormat = "dd.MM.yyyy"
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_favorite_place)

        val toolbar = findViewById<Toolbar>(R.id.toolbarAddPlace)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        /*
        * Set the date by clicking to the date input,
        * after choosing the date update the input box accordingly
        */
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        findViewById<AppCompatEditText>(R.id.editTextDate).setOnClickListener(this)
    }

    /*
    * When clicked open the date picker view and wait for the input
    */
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.editTextDate -> {
                DatePickerDialog(
                    this@AddFavoritePlace,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    /*
    * When called update the date input box
    * with the entered data from the data picker
    * myFormat("dd.MM.yyyy")
    */
    private fun updateDateInView() {
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        findViewById<AppCompatEditText>(R.id.editTextDate).setText(
            simpleDateFormat.format(calendar.time).toString()
        )
    }
}