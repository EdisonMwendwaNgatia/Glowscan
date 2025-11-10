package com.example.clearcanvas.data

data class SkinAnalysisResult(
    val skinType: String,
    val confidence: Float,
    val concerns: List<String>,
    val hydrationLevel: Float,
    val textureScore: Float,
    val recommendedProducts: List<SkincareProduct>
) {
    fun toJson(): String {
        return """{
            "skinType": "$skinType",
            "confidence": $confidence,
            "concerns": [${concerns.joinToString { "\"$it\"" }}],
            "hydrationLevel": $hydrationLevel,
            "textureScore": $textureScore
        }"""
    }

    companion object {
        fun fromJson(json: String): SkinAnalysisResult {
            // Simple JSON parsing
            val skinType = extractValue(json, "skinType") ?: "Combination"
            val confidence = extractValue(json, "confidence")?.toFloatOrNull() ?: 0.92f
            val concerns = extractArray(json, "concerns")
            val hydrationLevel = extractValue(json, "hydrationLevel")?.toFloatOrNull() ?: 0.78f
            val textureScore = extractValue(json, "textureScore")?.toFloatOrNull() ?: 0.85f

            return SkinAnalysisResult(
                skinType = skinType,
                confidence = confidence,
                concerns = concerns,
                hydrationLevel = hydrationLevel,
                textureScore = textureScore,
                recommendedProducts = emptyList()
            )
        }

        private fun extractValue(json: String, key: String): String? {
            val pattern = "\"$key\"\\s*:\\s*\"?([^\",}]+)\"?".toRegex()
            return pattern.find(json)?.groupValues?.get(1)
        }

        private fun extractArray(json: String, key: String): List<String> {
            val pattern = "\"$key\"\\s*:\\s*\\[([^]]+)\\]".toRegex()
            val match = pattern.find(json)?.groupValues?.get(1)
            return match?.split(",")?.map { it.trim().removeSurrounding("\"") } ?: emptyList()
        }
    }
}