package com.example.pawpals.adoption

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pawpals.api.AdoptionClient
import com.example.pawpals.api.DogResponse
import com.example.pawpals.databinding.FragmentAdoptionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        binding.rvDogs.layoutManager = GridLayoutManager(requireContext(), 2)

        // === LOAD DATA FROM API ===
        loadDogs()

        val addDogLauncher =
            registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == AppCompatActivity.RESULT_OK) {
                    loadDogs() // refresh list
                }
            }

        // === FAB to open Adoption Form ===
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AdoptionOwnerFormActivity::class.java)
            addDogLauncher.launch(intent)
        }


        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title =
            "Paw Adopsi"
    }

    private fun loadDogs() {
        AdoptionClient.instance.getDogs().enqueue(object : Callback<DogResponse> {
            override fun onResponse(call: Call<DogResponse>, response: Response<DogResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {

                    val dogList = response.body()!!.dogs

                    val adapter = DogAdapter(dogList) { dog ->
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("dog", dog)
                        startActivity(intent)
                    }

                    binding.rvDogs.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Failed to load dogs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DogResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
