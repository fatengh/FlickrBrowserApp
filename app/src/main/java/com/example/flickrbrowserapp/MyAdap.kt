package com.example.flickrbrowserapp


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ItemRowBinding

class MyAdap(val activity: MainActivity, private val imgs: ArrayList<Image>): RecyclerView.Adapter<MyAdap.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = imgs[position]

        holder.binding.apply {
            tvTitle.text = photo.title
            Glide.with(activity).load(photo.link).into(ivSmall)
            llItem.setOnClickListener { activity.openImg(photo.link) }
        }
    }

    override fun getItemCount() = imgs.size
}