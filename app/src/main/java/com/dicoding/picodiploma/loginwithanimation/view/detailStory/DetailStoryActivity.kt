package com.dicoding.picodiploma.loginwithanimation.view.detailStory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>("addStory")
        story?.let {
            binding.titleTextView.text = it.name
            binding.descTextView.text = it.description
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.imageView)
        }
    }
}
