package com.example.favoriteplaces.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import com.example.favoriteplaces.R
import com.example.favoriteplaces.database.DatabaseHandler
import com.example.favoriteplaces.models.FavoritePlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AddFavoritePlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var myFavoritePlaceDetails: FavoritePlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_favorite_place)

        // Toolbar for the AddFavoritePlaceActivity
        setSupportActionBar(findViewById(R.id.toolbarAddPlace))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<Toolbar>(R.id.toolbarAddPlace).setNavigationOnClickListener {
            onBackPressed()
        }

        // Get the values from the main activity
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            myFavoritePlaceDetails =
                intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS) as FavoritePlaceModel?
        }

        // Wait for user input in date
        dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDateInView()
            }
        updateDateInView() // This function is called if the user does not enter any value for the date, and assign current date

        if (myFavoritePlaceDetails != null) {
            supportActionBar?.title = "Edit Favorite Place"

            findViewById<AppCompatEditText>(R.id.editTextTitle).setText(myFavoritePlaceDetails!!.title)
            findViewById<AppCompatEditText>(R.id.editTextDescription).setText(myFavoritePlaceDetails!!.description)
            findViewById<AppCompatEditText>(R.id.editTextDate).setText(myFavoritePlaceDetails!!.date)
            findViewById<AppCompatEditText>(R.id.editTextLocation).setText(myFavoritePlaceDetails!!.location)
            mLatitude = myFavoritePlaceDetails!!.latitude
            mLongitude = myFavoritePlaceDetails!!.longitude

            saveImageToInternalStorage = Uri.parse(myFavoritePlaceDetails!!.image)

            findViewById<AppCompatImageView>(R.id.imageViewPlace).setImageURI(
                saveImageToInternalStorage
            )
            findViewById<Button>(R.id.buttonSave).text = getString(R.string.update)
        }

        findViewById<AppCompatEditText>(R.id.editTextDate).setOnClickListener(this)
        findViewById<TextView>(R.id.textViewAddImage).setOnClickListener(this)
        findViewById<Button>(R.id.buttonSave).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            // Execute when clicked to the date section
            R.id.editTextDate -> {
                DatePickerDialog(
                    this@AddFavoritePlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // Execute when clicked to add image button
            R.id.textViewAddImage -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(
                    pictureDialogItems
                ) { _, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            // Execute when clicked to save button
            R.id.buttonSave -> {
                /**
                 * Check if the user entered values are empty or not
                 * Title, Description, Location
                 * Date is not checked, it is assigned as current date if not chosen
                 * Then warn the user
                 * If all the inputs are entered correctly then proceed...
                 */
                when {
                    findViewById<AppCompatEditText>(R.id.editTextTitle).text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show()
                    }
                    findViewById<AppCompatEditText>(R.id.editTextDescription).text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    findViewById<AppCompatEditText>(R.id.editTextLocation).text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a location.", Toast.LENGTH_SHORT).show()
                    }
                    /**
                     * If all the credentials are entered correctly,
                     * create model variable using the FavoritePlaceModel
                     * store the created variable in the database using,
                     * DatabaseHandler, then notify the user.
                     */
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please choose an image.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val favoritePlace = FavoritePlaceModel(
                            if (myFavoritePlaceDetails == null) 0 else myFavoritePlaceDetails!!.id,
                            findViewById<AppCompatEditText>(R.id.editTextTitle).text.toString(),
                            saveImageToInternalStorage.toString(),
                            findViewById<AppCompatEditText>(R.id.editTextDescription).text.toString(),
                            findViewById<AppCompatEditText>(R.id.editTextDate).text.toString(),
                            findViewById<AppCompatEditText>(R.id.editTextLocation).text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dataBaseHandler = DatabaseHandler(this) // Create a database object
                        if (myFavoritePlaceDetails == null) {
                            val addFavoritePlace = dataBaseHandler.addFavoritePlace(favoritePlace)
                            if (addFavoritePlace > 0) {
                                setResult(Activity.RESULT_OK)
                                finish() // Finish the activity, return to the main activity
                            }
                        } else {
                            val updateFavoritePlace = dataBaseHandler.updateFavoritePlace(favoritePlace)
                            if (updateFavoritePlace > 0) {
                                setResult(Activity.RESULT_OK)
                                finish() // Finish the activity, return to the main activity
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved Image: ", "Path :: $saveImageToInternalStorage")
                        findViewById<AppCompatImageView>(R.id.imageViewPlace)!!.setImageBitmap(
                            selectedImageBitmap
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddFavoritePlaceActivity, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage =
                    saveImageToInternalStorage(thumbnail)
                Log.e("Saved Image: ", "Path :: $saveImageToInternalStorage")
                findViewById<AppCompatImageView>(R.id.imageViewPlace)!!.setImageBitmap(thumbnail)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    /**
     * A function to update the selected date in the UI with selected format.
     * This function is created because every time we don't need to add format which we have added here to show it in the UI.
     */
    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        findViewById<AppCompatEditText>(R.id.editTextDate).setText(
            sdf.format(cal.time).toString()
        )
    }

    /**
     * A method is used for image selection from GALLERY / PHOTOS of phone storage.
     */
    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    // Here after all the permission are granted launch the gallery to select and image.
                    if (report!!.areAllPermissionsGranted()) {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    /**
     * A method is used  asking the permission for camera and storage and image capturing and selection from Camera.
     */
    private fun takePhotoFromCamera() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    /**
     * This function is to store the image that was taken
     * MODE_PRIVATE to limit the application to read only
     * randomly assign a file name to the image to be saved
     */
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg") // Directory name for the file path

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "FavoritePlaces"
    }
}