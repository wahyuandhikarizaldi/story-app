package com.dicoding.picodiploma.loginwithanimation.view.addStory

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.utils.Utils
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.map.MapsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null
    private lateinit var token: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra("TOKEN") ?: ""

        viewModel = ViewModelProvider(this).get(AddStoryViewModel::class.java)

        with(binding) {
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadImage() }
        }

        viewModel.uploadResult.observe(this) { message ->
            showToast(message)
            if (message == "Story created successfully") {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        val utils = Utils()
        currentImageUri = utils.getImageUri(this)
        currentImageUri?.let { launcherIntentCamera.launch(it) }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.imageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        if (binding.locationCheckBox.isChecked) {
            getMyLocation { latitude, longitude ->
                uploadImageWithLocation(latitude, longitude)
            }
        } else {
            uploadImageWithLocation(0.0, 0.0)
        }
    }

    private fun uploadImageWithLocation(latitude: Double, longitude: Double) {
        currentImageUri?.let { uri ->
            val description = binding.descEditText.text.toString()
            viewModel.uploadImage(token, description, uri, latitude, longitude)
        } ?: showToast(getString(R.string.no_image))
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                uploadImage()
            }
        }

    private fun getMyLocation(callback: (Double, Double) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d(MapsActivity.TAG, "Current location: $latitude, $longitude")
                    callback(latitude, longitude)
                } else {
                    Log.d(MapsActivity.TAG, "Location is null")
                }
            }.addOnFailureListener { exception ->
                Log.e(MapsActivity.TAG, "Error getting location: ", exception)
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
