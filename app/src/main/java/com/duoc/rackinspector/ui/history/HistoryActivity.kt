package com.duoc.rackinspector.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.duoc.rackinspector.adapters.HistoryAdapter
import com.duoc.rackinspector.data.local.AppDatabase
import com.duoc.rackinspector.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        binding.rvHistoryList.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadHistory() // Carga y refresca los datos actualizados de Room
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            val assessments = db.assessmentDao().getAllAssessments()
            if (assessments.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvHistoryList.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvHistoryList.visibility = View.VISIBLE

                val adapter = HistoryAdapter(assessments) { assessment ->
                    val intent = Intent(this@HistoryActivity, DetailActivity::class.java)
                    intent.putExtra("ASSESSMENT_ID", assessment.id)
                    startActivity(intent)
                }
                binding.rvHistoryList.adapter = adapter
            }
        }
    }
}