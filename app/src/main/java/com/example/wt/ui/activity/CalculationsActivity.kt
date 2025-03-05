package com.example.wt.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wt.R
import com.example.wt.databinding.ActivityCalculationsBinding

class CalculationsActivity : AppCompatActivity() {
    lateinit var binding : ActivityCalculationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCalculationsBinding .inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttoncalculate.setOnClickListener {
            var calculations = Calculations()
            var a = binding.firstvalue.text.toString().toInt()
            var b = binding.secondvalue.text.toString().toInt()
            var result = calculations.add(a,b)

            binding.displaySum.text = result.toString()

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}