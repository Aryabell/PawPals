package com.example.pawpals.adoption

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pawpals.adoption.DetailActivity
import com.example.pawpals.databinding.FragmentAdoptionBinding

class AdoptionFragment : Fragment() {

    private var _binding: FragmentAdoptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdoptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // === SETUP RECYCLERVIEW ===
        binding.rvDogs.layoutManager = GridLayoutManager(requireContext(), 2)
        val dogs = SampleDogs.list
        val adapter = DogAdapter(dogs) { dog ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("dog", dog)
            startActivity(intent)
        }
        binding.rvDogs.adapter = adapter

        // === SET ACTION BAR TITLE ===
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title =
            "PawPals: Adopsi Anjing"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
