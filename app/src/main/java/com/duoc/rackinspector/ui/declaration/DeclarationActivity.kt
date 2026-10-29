package com.duoc.rackinspector.ui.declaration

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.duoc.rackinspector.adapters.ComponentAdapter
import com.duoc.rackinspector.data.local.AppDatabase
import com.duoc.rackinspector.data.model.ComponentItem
import com.duoc.rackinspector.data.model.RackAssessment
import com.duoc.rackinspector.databinding.ActivityDeclarationBinding
import com.duoc.rackinspector.databinding.DialogAddComponentBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeclarationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeclarationBinding
    private lateinit var db: AppDatabase
    private val componentList = mutableListOf<ComponentItem>()
    private lateinit var adapter: ComponentAdapter
    private var imageUriString: String = ""

    // Catálogo inicial de componentes
    private val categoriesCatalog = arrayOf("Switch", "Servidor", "Patch Panel", "UPS", "Router", "PDU")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeclarationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        imageUriString = intent.getStringExtra("IMAGE_URI") ?: ""

        // Cargar vista previa de la foto tomada o seleccionada
        if (imageUriString.isNotEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUriString))
                .into(binding.ivRackPreview)
        }

        setupRecyclerView()

        binding.btnAddCategory.setOnClickListener {
            showAddComponentDialog(null, -1)
        }

        binding.btnValidateAndSave.setOnClickListener {
            saveAssessment()
        }
    }

    private fun setupRecyclerView() {
        adapter = ComponentAdapter(
            items = componentList,
            onEditClick = { position, item ->
                showAddComponentDialog(item, position)
            },
            onDeleteClick = { position ->
                if (position in componentList.indices) {
                    componentList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, componentList.size)
                }
            }
        )
        binding.rvComponents.layoutManager = LinearLayoutManager(this)
        binding.rvComponents.adapter = adapter
    }

    private fun showAddComponentDialog(existingItem: ComponentItem?, position: Int) {
        val dialogBinding = DialogAddComponentBinding.inflate(layoutInflater)

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categoriesCatalog
        )
        dialogBinding.spCategory.adapter = spinnerAdapter

        if (existingItem != null) {
            dialogBinding.tvDialogTitle.text = "Editar Componente"
            val spinnerPosition = categoriesCatalog.indexOf(existingItem.category)
            if (spinnerPosition >= 0) {
                dialogBinding.spCategory.setSelection(spinnerPosition)
            }
            dialogBinding.etCount.setText(existingItem.count.toString())
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val selectedCategory = dialogBinding.spCategory.selectedItem.toString()
                val countText = dialogBinding.etCount.text.toString().trim()
                val count = countText.toIntOrNull() ?: 1

                if (existingItem == null) {
                    componentList.add(ComponentItem(category = selectedCategory, count = count))
                    adapter.notifyItemInserted(componentList.size - 1)
                } else {
                    componentList[position] = ComponentItem(category = selectedCategory, count = count)
                    adapter.notifyItemChanged(position)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveAssessment() {
        val rackName = binding.etRackName.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        if (rackName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Ingresa la identificación del rack y ubicación", Toast.LENGTH_SHORT).show()
            return
        }

        if (componentList.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un componente observado", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("rack_inspector_prefs", Context.MODE_PRIVATE)
        val userEmail = prefs.getString("user_email", "usuario@duoc.cl") ?: "usuario@duoc.cl"

        val currentDate = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()).format(Date())

        val assessment = RackAssessment(
            rackName = rackName,
            location = location,
            date = currentDate,
            userEmail = userEmail,
            imageUri = imageUriString,
            status = "Validado",
            components = componentList
        )

        lifecycleScope.launch {
            db.assessmentDao().insertAssessment(assessment)
            val intent = Intent(this@DeclarationActivity, ConfirmationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}