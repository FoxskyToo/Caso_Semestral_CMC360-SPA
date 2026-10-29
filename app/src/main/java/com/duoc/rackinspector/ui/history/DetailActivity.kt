package com.duoc.rackinspector.ui.history

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
import com.duoc.rackinspector.databinding.ActivityDetailBinding
import com.duoc.rackinspector.databinding.DialogAddComponentBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase
    private var currentAssessment: RackAssessment? = null
    private val componentList = mutableListOf<ComponentItem>()
    private lateinit var componentAdapter: ComponentAdapter

    private val categoriesCatalog = arrayOf("Switch", "Servidor", "Patch Panel", "UPS", "Router", "PDU")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        val assessmentId = intent.getIntExtra("ASSESSMENT_ID", -1)

        if (assessmentId != -1) {
            loadAssessmentDetail(assessmentId)
        } else {
            Toast.makeText(this, "Error al cargar el detalle", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBackFromDetail.setOnClickListener {
            finish()
        }

        // Agregar nuevo componente al levantamiento actual
        binding.btnAddDetailComponent.setOnClickListener {
            showAddComponentDialog()
        }

        // Eliminar levantamiento completo
        binding.btnDeleteAssessment.setOnClickListener {
            currentAssessment?.let { assessment ->
                confirmAndDeleteAssessment(assessment)
            }
        }
    }

    private fun loadAssessmentDetail(id: Int) {
        lifecycleScope.launch {
            val item = db.assessmentDao().getAssessmentById(id)
            item?.let {
                currentAssessment = it
                binding.tvDetailTitle.text = "${it.rackName} - ${it.location}"
                binding.tvDetailSubHeader.text = "${it.date} | Usuario: ${it.userEmail}"
                binding.tvDetailLocation.text = "📍 Ubicación: ${it.location}"
                binding.tvDetailStatus.text = "✔️ Estado: ${it.status}"

                if (it.imageUri.isNotEmpty()) {
                    Glide.with(this@DetailActivity)
                        .load(Uri.parse(it.imageUri))
                        .into(binding.ivDetailPhoto)
                }

                // Cargar la lista actual de componentes
                componentList.clear()
                componentList.addAll(it.components)

                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        componentAdapter = ComponentAdapter(
            items = componentList,
            onEditClick = { position, item ->
                showEditComponentDialog(item, position)
            },
            onDeleteClick = { position ->
                deleteComponent(position)
            }
        )
        binding.rvDetailComponents.layoutManager = LinearLayoutManager(this)
        binding.rvDetailComponents.adapter = componentAdapter
    }

    // Diálogo para AGREGAR un nuevo componente
    private fun showAddComponentDialog() {
        val dialogBinding = DialogAddComponentBinding.inflate(layoutInflater)
        dialogBinding.tvDialogTitle.text = "Agregar Componente"

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categoriesCatalog
        )
        dialogBinding.spCategory.adapter = spinnerAdapter

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Agregar") { _, _ ->
                val selectedCategory = dialogBinding.spCategory.selectedItem.toString()
                val countText = dialogBinding.etCount.text.toString().trim()
                val count = countText.toIntOrNull() ?: 1

                val newComponent = ComponentItem(category = selectedCategory, count = count)
                componentList.add(newComponent)
                componentAdapter.notifyItemInserted(componentList.size - 1)

                // Persistir cambios en Room
                saveUpdatedComponentsToDatabase("Componente agregado")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Diálogo para EDITAR un componente existente
    private fun showEditComponentDialog(item: ComponentItem, position: Int) {
        val dialogBinding = DialogAddComponentBinding.inflate(layoutInflater)
        dialogBinding.tvDialogTitle.text = "Editar Componente"

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categoriesCatalog
        )
        dialogBinding.spCategory.adapter = spinnerAdapter

        val spinnerPosition = categoriesCatalog.indexOf(item.category)
        if (spinnerPosition >= 0) {
            dialogBinding.spCategory.setSelection(spinnerPosition)
        }
        dialogBinding.etCount.setText(item.count.toString())

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val selectedCategory = dialogBinding.spCategory.selectedItem.toString()
                val countText = dialogBinding.etCount.text.toString().trim()
                val count = countText.toIntOrNull() ?: 1

                val updatedItem = ComponentItem(category = selectedCategory, count = count)
                componentList[position] = updatedItem
                componentAdapter.notifyItemChanged(position)

                // Persistir cambios en Room
                saveUpdatedComponentsToDatabase("Componente actualizado")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Eliminar un componente de la lista
    private fun deleteComponent(position: Int) {
        if (position in componentList.indices) {
            AlertDialog.Builder(this)
                .setTitle("Eliminar componente")
                .setMessage("¿Deseas quitar este componente del levantamiento?")
                .setPositiveButton("Eliminar") { _, _ ->
                    componentList.removeAt(position)
                    componentAdapter.notifyItemRemoved(position)
                    componentAdapter.notifyItemRangeChanged(position, componentList.size)

                    // Persistir cambios en Room
                    saveUpdatedComponentsToDatabase("Componente eliminado")
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    // Método centralizado para guardar la lista de componentes actualizada en Room
    private fun saveUpdatedComponentsToDatabase(message: String) {
        currentAssessment?.let { assessment ->
            val updatedAssessment = assessment.copy(components = componentList.toList())
            currentAssessment = updatedAssessment
            lifecycleScope.launch {
                db.assessmentDao().updateAssessment(updatedAssessment)
                Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmAndDeleteAssessment(assessment: RackAssessment) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Levantamiento")
            .setMessage("¿Estás seguro de que deseas eliminar permanentemente este levantamiento? Esta acción no se puede deshacer.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Eliminar") { dialog, _ ->
                lifecycleScope.launch {
                    db.assessmentDao().deleteAssessment(assessment)
                    Toast.makeText(this@DetailActivity, "Levantamiento eliminado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}