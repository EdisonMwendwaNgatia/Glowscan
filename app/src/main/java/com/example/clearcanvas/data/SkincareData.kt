package com.example.clearcanvas.data

object SkincareData {

    val skinTypes = listOf("Oily", "Dry", "Combination", "Sensitive", "Normal")

    val productMap = mapOf(
        "Oily" to listOf(
            "Salicylic Acid Cleanser",
            "Charcoal Clay Mask",
            "Oil-Free Moisturizer",
            "Tea Tree Toner",
            "Niacinamide Serum",
            "Gel-based Sunscreen"
        ),
        "Dry" to listOf(
            "Hyaluronic Acid Serum",
            "Shea Butter Cream",
            "Coconut Milk Cleanser",
            "Aloe Vera Moisturizer",
            "Ceramide Lotion",
            "Hydrating Overnight Mask"
        ),
        "Combination" to listOf(
            "Balancing Gel Cleanser",
            "Dual-Zone Moisturizer",
            "Green Tea Toner",
            "Oil Control Serum",
            "Lightweight Sunscreen",
            "Exfoliating Pads"
        ),
        "Sensitive" to listOf(
            "Fragrance-Free Cleanser",
            "Oatmeal Moisturizer",
            "Soothing Chamomile Toner",
            "Aloe Rescue Mask",
            "Micellar Water",
            "Non-comedogenic SPF"
        ),
        "Normal" to listOf(
            "Daily Hydrating Lotion",
            "Vitamin C Serum",
            "Gentle Foaming Cleanser",
            "Cucumber Mist Spray",
            "Balanced pH Toner",
            "Glow Enhancer Cream"
        )
    )
}
