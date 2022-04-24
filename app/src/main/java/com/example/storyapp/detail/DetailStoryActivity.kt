package com.example.storyapp.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.maps.MapsActivity

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDetails()
    }

    private fun getDetails() {
        val bundle = intent.extras
        with(binding) {
            name.text = bundle?.getString("name")
            description.text = bundle?.getString("description")
            Glide.with(binding.root.context)
                .load(bundle?.getString("photoUrl"))
                .circleCrop()
                .into(binding.photoUrl)
            lat.text = bundle?.getDouble("lat").toString()
            lon.text = bundle?.getDouble("lon").toString()
        }

        binding.buttonMaps.setOnClickListener{
            val intent = Intent(this@DetailStoryActivity, MapsActivity::class.java)
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            startActivity(intent)
        }


    }
}

