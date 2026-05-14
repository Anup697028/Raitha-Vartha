package com.raitha.vartha.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.raitha.vartha.R
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())

        setupInitialState()
        setupListeners()
    }

    private fun setupInitialState() {
        lifecycleScope.launch {
            val currentLang = prefManager.languageFlow.first()
            if (currentLang == "kn") {
                binding.rbKannada.isChecked = true
            } else {
                binding.rbEnglish.isChecked = true
            }

            val currentTheme = prefManager.themeFlow.first()
            when (currentTheme) {
                "light" -> binding.rbThemeLight.isChecked = true
                "dark" -> binding.rbThemeDark.isChecked = true
                else -> binding.rbThemeSystem.isChecked = true
            }

            prefManager.districtFlow.collect { district ->
                binding.tvCurrentDistrict.text = getString(R.string.district_label, district ?: getString(R.string.not_set))
            }
        }
    }

    private fun setupListeners() {
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val lang = if (checkedId == R.id.rbKannada) "kn" else "en"
            lifecycleScope.launch {
                val current = prefManager.languageFlow.first()
                if (current != lang) {
                    prefManager.saveLanguage(lang)
                    requireActivity().recreate()
                }
            }
        }

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                R.id.rbThemeLight -> "light"
                R.id.rbThemeDark -> "dark"
                else -> "system"
            }
            lifecycleScope.launch {
                prefManager.saveTheme(theme)
            }
        }

        binding.btnChangeDistrict.setOnClickListener {
            findNavController().navigate(R.id.districtFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
