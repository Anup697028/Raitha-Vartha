package com.raitha.vartha.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.raitha.vartha.databinding.FragmentSavedBinding
import com.raitha.vartha.models.Tip
import com.raitha.vartha.ui.home.FlashCardAdapter
import com.raitha.vartha.utils.DepthPageTransformer
import com.raitha.vartha.utils.TextToSpeechManager
import com.raitha.vartha.viewmodel.HomeViewModel
import com.raitha.vartha.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireContext())
    }

    private lateinit var tipsAdapter: FlashCardAdapter
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var ttsManager: TextToSpeechManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        ttsManager = TextToSpeechManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
    }

    private fun setupUI() {
        // Setup Tips ViewPager
        tipsAdapter = FlashCardAdapter(
            onSaveClick = { tip -> viewModel.toggleSaveTip(tip) },
            onShareClick = { tip -> shareTip(tip) },
            onListenClick = { text -> ttsManager.speak(text) }
        )
        binding.viewPager.apply {
            adapter = tipsAdapter
            setPageTransformer(DepthPageTransformer())
        }

        // Setup Stories RecyclerView
        storyAdapter = StoryAdapter { story ->
            val action = SavedFragmentDirections.actionSavedFragmentToStoryDetailFragment(story)
            findNavController().navigate(action)
        }
        binding.rvStories.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.savedTips.collectLatest { tips ->
                tipsAdapter.submitList(tips)
                binding.tvEmptyTips.visibility = if (tips.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.allStories.collectLatest { stories ->
                storyAdapter.submitList(stories)
            }
        }
    }

    private fun shareTip(tip: Tip) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "${tip.tipTitle}\n\n${tip.tipDescription}")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Tip"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ttsManager.shutdown()
        _binding = null
    }
}
