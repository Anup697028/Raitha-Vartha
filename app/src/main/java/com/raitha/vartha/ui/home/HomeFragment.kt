package com.raitha.vartha.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.raitha.vartha.databinding.FragmentHomeBinding
import com.raitha.vartha.models.Tip
import com.raitha.vartha.utils.DepthPageTransformer
import com.raitha.vartha.utils.TextToSpeechManager
import com.raitha.vartha.viewmodel.HomeViewModel
import com.raitha.vartha.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireContext())
    }

    private lateinit var flashCardAdapter: FlashCardAdapter
    private lateinit var ttsManager: TextToSpeechManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        ttsManager = TextToSpeechManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewPager()
        observeData()
    }

    private fun setupViewPager() {
        flashCardAdapter = FlashCardAdapter(
            onSaveClick = { tip -> viewModel.toggleSaveTip(tip) },
            onShareClick = { tip -> shareTip(tip) },
            onListenClick = { text -> speakTip(text) }
        )
        binding.viewPager.apply {
            adapter = flashCardAdapter
            setPageTransformer(DepthPageTransformer())
            offscreenPageLimit = 3
        }
    }

    private fun shareTip(tip: Tip) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Farming Tip: ${tip.cropName}")
            putExtra(Intent.EXTRA_TEXT, "${tip.tipTitle}\n\n${tip.tipDescription}\n\nShared via Raitha-Vartha")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Tip"))
    }

    private fun speakTip(text: String) {
        ttsManager.speak(text)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect daily tips for the ViewPager
                launch {
                    viewModel.tips.collectLatest { tips ->
                        flashCardAdapter.submitList(tips)
                    }
                }

                // Loading state
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        binding.progressBar.visibility = if (isLoading && flashCardAdapter.itemCount == 0) View.VISIBLE else View.GONE
                    }
                }

                // Collect weather updates for the current district
                launch {
                    viewModel.weather.collectLatest { weather ->
                        weather?.let {
                            binding.weatherCard.tvDistrict.text = it.name
                            binding.weatherCard.tvTemp.text = getString(com.raitha.vartha.R.string.temp_celsius, it.main.temp.toInt())
                            binding.weatherCard.tvHumidity.text = getString(com.raitha.vartha.R.string.humidity_label, it.main.humidity)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ttsManager.shutdown()
        _binding = null
    }
}
