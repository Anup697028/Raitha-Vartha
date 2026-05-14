package com.raitha.vartha.ui.crops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.databinding.FragmentCropDetailBinding
import com.raitha.vartha.models.Crop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CropDetailFragment : Fragment() {
    private var _binding: FragmentCropDetailBinding? = null
    private val binding get() = _binding!!
    private val args: CropDetailFragmentArgs by navArgs()
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCropDetailBinding.inflate(inflater, container, false)
        prefManager = PreferenceManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val crop = args.crop
        
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                prefManager.languageFlow.collectLatest { lang ->
                    setupUI(crop, lang)
                }
            }
        }
    }

    private fun setupUI(crop: Crop, lang: String) {
        val isKn = lang == "kn"

        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            collapsingToolbar.title = if (isKn) crop.nameKn else crop.name

            // Local drawable mapping
            val context = requireContext()
            val resourceId = context.resources.getIdentifier(crop.imageName, "drawable", context.packageName)

            Glide.with(context)
                .load(if (resourceId != 0) resourceId else crop.imageName)
                .into(ivCropBanner)

            chipCategory.text = if (isKn) crop.categoryKn else crop.category
            chipSeason.text = if (isKn) crop.seasonKn else crop.season
            
            tvOverview.text = if (isKn) crop.overviewKn else crop.overview
            
            tvInvestment.text = if (isKn) crop.investmentKn else crop.investment
            tvRevenue.text = if (isKn) crop.revenueKn else crop.revenue
            tvProfit.text = if (isKn) crop.profitKn else crop.profit
            
            tvSoilType.text = if (isKn) crop.soilTypeKn else crop.soilType
            tvWaterReq.text = if (isKn) crop.waterRequirementKn else crop.waterRequirement
            tvDuration.text = if (isKn) crop.durationKn else crop.duration
            tvHarvestTime.text = if (isKn) crop.harvestTimeKn else crop.harvestTime
            
            val pestsLabel = if (isKn) "ಕೀಟಗಳು: " else "Pests: "
            val pestsText = if (isKn) crop.pestsKn.joinToString(", ") else crop.pests.joinToString(", ")
            tvPests.text = "$pestsLabel$pestsText"
            
            val pesticidesLabel = if (isKn) "ಕೀಟನಾಶಕಗಳು: " else "Pesticides: "
            val pesticidesText = if (isKn) crop.pesticidesKn.joinToString(", ") else crop.pesticides.joinToString(", ")
            tvPesticides.text = "$pesticidesLabel$pesticidesText"
            
            val diseasesLabel = if (isKn) "ರೋಗಗಳು: " else "Diseases: "
            val diseasesText = if (isKn) crop.diseasesKn.joinToString(", ") else crop.diseases.joinToString(", ")
            tvDiseases.text = "$diseasesLabel$diseasesText"
            
            val yieldTipsText = if (isKn) crop.yieldTipsKn.joinToString("\n• ", "• ") else crop.yieldTips.joinToString("\n• ", "• ")
            tvYieldTips.text = yieldTipsText
            
            tvAiAdvice.text = if (isKn) crop.aiRecommendationKn else crop.aiRecommendation
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
