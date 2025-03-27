package com.example.myapplication

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.activity.DetailStoryActivity
import com.example.myapplication.data.AuthRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myapplication.databinding.ActivityMapsBinding
import com.example.myapplication.response.ListStoryItem
import com.example.myapplication.response.StoryResponse
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var token: String
    private lateinit var stories: List<ListStoryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra("TOKEN") ?: ""

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        fetchStoriesWithLocation()

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }
    }

    private fun fetchStoriesWithLocation() {
        authRepository.getStoriesWithLocation(token).enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful && response.body()?.error == false) {
                    stories = response.body()?.listStory ?: emptyList()
                    addMarkers(stories)
                } else {
                    Toast.makeText(this@MapsActivity, "Failed to fetch stories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Toast.makeText(this@MapsActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMarkers(stories: List<ListStoryItem>) {
        for (story in stories) {
            val position = LatLng(story.lat as Double, story.lon as Double)
            mMap.addMarker(MarkerOptions().position(position).title(story.name).snippet(story.description))
        }
        if (stories.isNotEmpty()) {
            val firstStory = stories[0]
            val position = LatLng(firstStory.lat as Double, firstStory.lon as Double)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val story = stories.find { it.name == marker.title }
        if (story != null) {
            val intent = Intent(this, DetailStoryActivity::class.java).apply {
                putExtra("name", story.name)
                putExtra("description", story.description)
                putExtra("imageUrl", story.photoUrl)
            }
            startActivity(intent)
        }
        return true
    }
}