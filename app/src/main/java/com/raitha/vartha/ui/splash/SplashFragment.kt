package com.raitha.vartha.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.raitha.vartha.R
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefManager = PreferenceManager(requireContext())
        
        lifecycleScope.launch {
            // Wait for 2 seconds for splash visibility
            delay(2000)
            // Take only the first emission to avoid multiple navigation triggers
            val completed = prefManager.onboardingFlow.first()
            if (completed) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_languageFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
