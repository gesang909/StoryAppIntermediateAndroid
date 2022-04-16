package com.example.storyapp.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailStoryBinding

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
        }


    }
}

