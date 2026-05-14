package com.raitha.vartha.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.raitha.vartha.R
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.databinding.FragmentLanguageBinding
import kotlinx.coroutines.launch

class LanguageFragment : Fragment() {
    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefManager = PreferenceManager(requireContext())

        binding.btnKannada.setOnClickListener {
            lifecycleScope.launch {
                prefManager.saveLanguage("kn")
                findNavController().navigate(R.id.action_languageFragment_to_districtFragment)
            }
        }

        binding.btnEnglish.setOnClickListener {
            lifecycleScope.launch {
                prefManager.saveLanguage("en")
                findNavController().navigate(R.id.action_languageFragment_to_districtFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
