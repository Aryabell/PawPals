package com.example.pawpals.adoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pawpals.databinding.ItemAdoptionBinding
import com.example.pawpals.model.Dog

class DogAdapter(
    private val list: List<Dog>,
    private val onClick: (Dog) -> Unit
) : RecyclerView.Adapter<DogAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAdoptionBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(dog: Dog) = with(binding) {

            // TEXT
            tvDogName.text = dog.name
            tvAdopterName.text = dog.breed
            tvStatus.text = dog.location
            tvReason.text = "Klik untuk detail"
            tvCreatedAt.text = ""

            // === LOAD IMAGE FROM dog.imageUrl ===
            Glide.with(root.context)
                .load(dog.imageUrl)                // URL dari database
                .into(ivDog)                   // ImageView dari layout

            root.setOnClickListener { onClick(dog) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdoptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
