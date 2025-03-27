package com.example.myapplication.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MapsActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.StoryAdapter
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.response.ListStoryItem
import com.example.myapplication.response.StoryResponse
import com.example.myapplication.viewmodel.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject



@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var  binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val storyViewModel: StoryViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            if(token == null){
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            progressBar = binding.progressBar
            setupRecyclerView()
            fetchPagedStories()
        }
    }


    private fun setupRecyclerView() {
        val adapter = StoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        adapter.addLoadStateListener { loadState ->
            progressBar.isVisible = loadState.refresh is LoadState.Loading
            if (loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0) {
                Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show()
            }
            if (loadState.source.refresh is LoadState.Loading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_add_story -> {
                val intent = Intent(this, AddStoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("TOKEN", sharedPreferences.getString("token", ""))
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        with(sharedPreferences.edit()){
            remove("email")
            remove("name")
            remove("token")
            apply()
        }
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchPagedStories() {
        val adapter = binding.recyclerView.adapter as StoryAdapter
        val token = sharedPreferences.getString("token", "") ?: ""
        storyViewModel.getPagedStories(token).observe(this) { pagingData ->
            lifecycleScope.launch {
                adapter.submitData(pagingData)
            }
        }
    }


}