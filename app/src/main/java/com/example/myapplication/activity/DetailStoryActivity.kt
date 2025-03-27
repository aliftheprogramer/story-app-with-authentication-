package com.example.myapplication.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val imageUrl = intent.getStringExtra("imageUrl")

        // Show ProgressBar
        binding.progressBar.visibility = View.VISIBLE

        // Load data
        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = description
        Glide.with(this).load(imageUrl).into(binding.tvDetailImage)

        // Hide ProgressBar and show content
        binding.progressBar.visibility = View.GONE
        binding.tvDetailImage.visibility = View.VISIBLE
        binding.tvDetailName.visibility = View.VISIBLE
        binding.tvDetailDescription.visibility = View.VISIBLE
    }
}