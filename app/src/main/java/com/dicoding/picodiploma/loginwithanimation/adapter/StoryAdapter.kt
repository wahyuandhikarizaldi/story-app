package com.dicoding.picodiploma.loginwithanimation.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.view.detailStory.DetailStoryActivity

class StoryAdapter() : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {

        val story = getItem(position)
        holder.storyTitle.text = story!!.name

        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.storyImage)

        holder.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
            intent.putExtra("addStory", story)

            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                Pair(holder.storyImage, "image"),
                Pair(holder.storyTitle, "title")
            )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardView)
        val storyTitle: TextView = view.findViewById(R.id.titleTextView)
        val storyImage: ImageView = view.findViewById(R.id.imageView)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
