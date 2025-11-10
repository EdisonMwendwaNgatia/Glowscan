package com.example.clearcanvas.data

import com.example.clearcanvas.R

data class SkincareProduct(
    val name: String,
    val imageResId: Int 
)

object SkincareData {
    val skinTypes = listOf("Oily", "Dry", "Combination", "Sensitive", "Normal")

    val productMap = mapOf(
        "Oily" to listOf(
            SkincareProduct("Salicylic Acid Cleanser", R.drawable.salicylic_cleanser),
            SkincareProduct("Charcoal Clay Mask", R.drawable.charcoal_mask),
            SkincareProduct("Gel-based Sunscreen", R.drawable.gel_sunscreen),
            SkincareProduct("Benzoyl Peroxide", R.drawable.benzoyl_peroxide)
        ),
        "Dry" to listOf(
            SkincareProduct("Hyaluronic Acid Serum", R.drawable.hyaluronic_serum),
            SkincareProduct("Coconut Milk Cleanser", R.drawable.coconut_cleanser),
            SkincareProduct("Aloe Vera Moisturizer", R.drawable.aloe_moisturizer),
            SkincareProduct("Ceramide Lotion", R.drawable.ceramide_lotion),
        ),
        "Combination" to listOf(
            SkincareProduct("Balancing Gel Cleanser", R.drawable.balancing_cleanser),
            SkincareProduct("Dual-Zone Moisturizer", R.drawable.dual_moisturizer),
            SkincareProduct("Green Tea Toner", R.drawable.green_tea_toner),
            SkincareProduct("Exfoliating Pads", R.drawable.exfoliating_pads)
        ),
        "Sensitive" to listOf(
            SkincareProduct("Fragrance-Free Cleanser", R.drawable.fragrance_free_cleanser),
            SkincareProduct("Soothing Chamomile Toner", R.drawable.chamomile_toner),
            SkincareProduct("Aloe Rescue Mask", R.drawable.aloe_mask)

        ),
        "Normal" to listOf(
            SkincareProduct("Daily Hydrating Lotion", R.drawable.hydrating_lotion),
            SkincareProduct("Gentle Foaming Cleanser", R.drawable.foaming_cleanser),
            SkincareProduct("Cucumber Mist Spray", R.drawable.cucumber_mist),
            SkincareProduct("Glow Enhancer Cream", R.drawable.glow_cream)
        )
    )
}