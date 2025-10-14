package com.example.pawpals.adoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pawpals.R
import com.example.pawpals.databinding.ItemAdoptionBinding

class DogAdapter(
    private val dogs: List<Dog>,
    private val onClick: (Dog) -> Unit
) : RecyclerView.Adapter<DogAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAdoptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dog: Dog) = with(binding) {
            tvName.text = dog.name
            tvBreed.text = dog.breed
            tvLocation.text = dog.location

            val genderIcon = if (dog.gender == Gender.MALE)
                R.drawable.ic_male else R.drawable.ic_female
            ivGender.setImageResource(genderIcon)

            imgDog.load(dog.imageUrl) {
                placeholder(R.drawable.ic_image_placeholder)
                crossfade(true)
            }

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
        holder.bind(dogs[position])
    }

    override fun getItemCount(): Int = dogs.size
}
