package com.raitha.vartha.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.raitha.vartha.R
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.databinding.FragmentCropSelectionBinding
import com.raitha.vartha.ui.crops.CropGridAdapter
import com.raitha.vartha.viewmodel.CropsViewModel
import com.raitha.vartha.viewmodel.CropsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CropSelectionFragment : Fragment() {
    private var _binding: FragmentCropSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CropsViewModel by viewModels {
        CropsViewModelFactory(requireContext())
    }

    private lateinit var cropAdapter: CropGridAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCropSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefManager = PreferenceManager(requireContext())

        setupRecyclerView()
        observeData()

        binding.btnFinish.setOnClickListener {
            lifecycleScope.launch {
                prefManager.setOnboardingCompleted(true)
                findNavController().navigate(R.id.action_cropSelectionFragment_to_homeFragment)
            }
        }
    }

    private fun setupRecyclerView() {
        cropAdapter = CropGridAdapter { crop ->
            // Optional: Mark crop as selected
        }
        binding.rvCrops.apply {
            adapter = cropAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredCrops.collectLatest { crops ->
                    cropAdapter.submitList(crops)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
