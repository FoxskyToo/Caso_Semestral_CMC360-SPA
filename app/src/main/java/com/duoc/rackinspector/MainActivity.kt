package com.duoc.rackinspector

import com.duoc.rackinspector.databinding.ActivityMainBinding
import com.duoc.rackinspector.ui.auth.LoginActivity
import com.duoc.rackinspector.ui.capture.CaptureActivity
import com.duoc.rackinspector.ui.history.HistoryActivity
import com.duoc.rackinspector.ui.settings.SettingsActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar nombre del usuario desde SharedPreferences
        val prefs = getSharedPreferences("rack_inspector_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Usuario") ?: "Usuario"
        binding.tvWelcomeUser.text = "¡Hola, $userName!"

        // Navegación a Captura de Imagen (Nuevo levantamiento)
        binding.btnNewAssessment.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java))
        }

        // Navegación a Historial
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Navegación a Ajustes
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Cerrar sesión
        binding.btnLogoutHome.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}