package com.example.favoriteplaces

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.lang.Exception
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
        findViewById<TextView>(R.id.textViewAddImage).setOnClickListener(this)
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
            R.id.textViewAddImage -> {
                val pictureDialog = AlertDialog.Builder(this)
                val pictureDialogItems = arrayOf("Select from Gallery", "Take from Camera")
                pictureDialog.setTitle("Select Action")
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(
                            this@AddFavoritePlace,
                            "Camera selection coming...",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    /*
    * This function is to allow user to pick an image from the photo gallery
    * 2 types of permission is being asked, READ and WRITE,
    * if permissions are not given by the user than direct to
    * --> showRationaleDialogForPermission
    */
    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    Toast.makeText(
                        this@AddFavoritePlace,
                        "Storage permissions are granted. Please select an image from the gallery.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    /*
    * Function to display the user an alert box
    * when rejected to give permission for the gallery
    * direct user to the device settings
    */
    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this).setMessage(
            "You have rejected the permission." +
                    "Please open it from the device settings in order to choose a photo from the gallery."
        ).setPositiveButton("GO TO SETTINGS")
        { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }.show()
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