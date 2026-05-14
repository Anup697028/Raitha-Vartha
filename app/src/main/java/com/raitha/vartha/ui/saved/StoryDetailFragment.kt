package com.raitha.vartha.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.raitha.vartha.databinding.FragmentStoryDetailBinding
import com.raitha.vartha.models.SuccessStory

class StoryDetailFragment : Fragment() {
    private var _binding: FragmentStoryDetailBinding? = null
    private val binding get() = _binding!!
    private val args: StoryDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val story = args.story
        setupUI(story)
    }

    private fun setupUI(story: SuccessStory) {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        
        binding.collapsingToolbar.title = story.farmerName
        binding.tvDetailFarmer.text = story.farmerName
        binding.tvDetailLocation.text = getString(com.raitha.vartha.R.string.location_format, story.district)
        binding.chipDetailCrop.text = story.cropType
        binding.tvDetailIssue.text = story.issueFaced
        binding.tvDetailSolution.text = story.solutionApplied
        binding.tvDetailAi.text = story.aiAdvantage
        binding.tvDetailFullStory.text = story.detailedStory

        val context = binding.ivStoryDetail.context
        val resourceName = story.imageUrl.lowercase().replace(" ", "")
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

        Glide.with(context)
            .load(if (resourceId != 0) resourceId else story.imageUrl)
            .centerCrop()
            .into(binding.ivStoryDetail)

        binding.btnShareStory.setOnClickListener {
            val shareText = getString(
                com.raitha.vartha.R.string.share_story_format,
                story.farmerName,
                story.district,
                story.issueFaced,
                story.yieldIncrease
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, getString(com.raitha.vartha.R.string.share_story_chooser)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
