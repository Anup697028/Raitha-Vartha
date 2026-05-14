package com.raitha.vartha.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.raitha.vartha.R
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.databinding.FragmentDistrictBinding
import kotlinx.coroutines.launch

class DistrictFragment : Fragment() {
    private var _binding: FragmentDistrictBinding? = null
    private val binding get() = _binding!!

    private val districts = listOf(
        "Mysuru", "Mandya", "Hassan", "Shivamogga", 
        "Chikkamagaluru", "Davanagere", "Tumakuru", "Bengaluru Rural"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDistrictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefManager = PreferenceManager(requireContext())

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, districts)
        binding.listView.adapter = adapter

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            lifecycleScope.launch {
                prefManager.saveDistrict(districts[position])
                findNavController().navigate(R.id.action_districtFragment_to_cropSelectionFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
