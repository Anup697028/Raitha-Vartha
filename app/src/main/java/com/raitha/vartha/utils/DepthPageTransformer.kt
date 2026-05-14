package com.raitha.vartha.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class DepthPageTransformer : ViewPager2.PageTransformer {
    private val MIN_SCALE = 0.9f
    private val MIN_ALPHA = 0.5f

    override fun transformPage(view: View, position: Float) {
        val pageHeight = view.height
        
        when {
            position < -1 -> { // [-Infinity,-1)
                view.alpha = 0f
            }
            position <= 1 -> { // [-1,1]
                // Scale the page down (between MIN_SCALE and 1)
                val scaleFactor = Math.max(MIN_SCALE, 1 - abs(position))
                
                // Fade the page relative to its size.
                view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)

                // Counteract the default slide transition if needed, but for vertical it's usually fine
                // For vertical ViewPager2, position is vertical offset
                
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
            }
            else -> { // (1,+Infinity]
                view.alpha = 0f
            }
        }
    }
}
