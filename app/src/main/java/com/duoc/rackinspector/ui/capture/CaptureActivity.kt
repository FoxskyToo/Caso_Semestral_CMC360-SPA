package com.duoc.rackinspector.ui.capture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.duoc.rackinspector.databinding.ActivityCaptureBinding
import com.duoc.rackinspector.ui.declaration.DeclarationActivity
import java.io.File

class CaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaptureBinding
    private var selectedImageUri: Uri? = null
    private var tempPhotoUri: Uri? = null

    // Launcher para solicitar permiso de Cámara en tiempo de ejecución
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Se requiere permiso de cámara para tomar fotografías", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para tomar foto con la cámara nativa
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri != null) {
            selectedImageUri = tempPhotoUri
            displayImage(selectedImageUri!!)
        }
    }

    // Launcher para seleccionar de la Galería
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            displayImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndOpen()
        }

        binding.btnSelectGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnContinueToDeclaration.setOnClickListener {
            selectedImageUri?.let { uri ->
                val intent = Intent(this, DeclarationActivity::class.java)
                intent.putExtra("IMAGE_URI", uri.toString())
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        try {
            val storageDir = externalCacheDir ?: cacheDir
            val photoFile = File(storageDir, "temp_rack_photo_${System.currentTimeMillis()}.jpg")
            tempPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            takePhotoLauncher.launch(tempPhotoUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al abrir la cámara: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun displayImage(uri: Uri) {
        // Eliminar tinte del icono por defecto si existiera
        binding.ivPreview.imageTintList = null

        Glide.with(this)
            .load(uri)
            .into(binding.ivPreview)

        binding.btnContinueToDeclaration.isEnabled = true
    }
}