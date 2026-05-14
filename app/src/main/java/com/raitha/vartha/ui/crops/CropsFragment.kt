package com.raitha.vartha.ui.crops

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
import com.raitha.vartha.databinding.FragmentCropsBinding
import com.raitha.vartha.viewmodel.CropsViewModel
import com.raitha.vartha.viewmodel.CropsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CropsFragment : Fragment() {
    private var _binding: FragmentCropsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CropsViewModel by viewModels {
        CropsViewModelFactory(requireContext())
    }

    private lateinit var cropAdapter: CropGridAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCropsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupListeners() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            val category = when (checkedId) {
                R.id.chip_cereals -> "CEREALS"
                R.id.chip_pulses -> "PULSES"
                R.id.chip_vegetables -> "VEGETABLES"
                R.id.chip_fruits -> "FRUITS"
                R.id.chip_plantation -> "PLANTATION"
                R.id.chip_commercial -> "COMMERCIAL"
                else -> "All"
            }
            viewModel.setCategory(category)
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        cropAdapter = CropGridAdapter { crop ->
            val action = CropsFragmentDirections.actionCropsFragmentToCropDetailFragment(crop)
            findNavController().navigate(action)
        }
        
        binding.rvCrops.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = cropAdapter
            // Optimization: Fixed size since it's a grid
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.filteredCrops.collectLatest { crops ->
                        cropAdapter.submitList(crops)
                        binding.layoutNoResults.visibility = if (crops.isEmpty() && !viewModel.isLoading.value) View.VISIBLE else View.GONE
                        binding.rvCrops.visibility = if (crops.isNotEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        binding.layoutShimmer.root.visibility = if (isLoading) View.VISIBLE else View.GONE
                        if (isLoading) binding.layoutShimmer.shimmerViewContainer.startShimmer()
                        else binding.layoutShimmer.shimmerViewContainer.stopShimmer()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
