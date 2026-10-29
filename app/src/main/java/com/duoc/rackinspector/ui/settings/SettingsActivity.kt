package com.duoc.rackinspector.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.duoc.rackinspector.databinding.ActivitySettingsBinding
import com.duoc.rackinspector.ui.auth.LoginActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("rack_inspector_prefs", Context.MODE_PRIVATE)

        // Estado del Modo Oscuro
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Información de la App
        binding.cardAppInfo.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("RackInspector v1.0")
                .setMessage("Aplicación para levantamiento y registro móvil de infraestructura tecnológica en Data Centers.")
                .setPositiveButton("Aceptar", null)
                .show()
        }

        // Cambiar contraseña (simulado)
        binding.cardChangePassword.setOnClickListener {
            Toast.makeText(this, "Opción disponible en la siguiente versión", Toast.LENGTH_SHORT).show()
        }

        // Cerrar sesión
        binding.cardLogout.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}