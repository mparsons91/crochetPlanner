package com.matthewparsons.hookline.domain.model

/**
 * Craft Yarn Council standard yarn weights, 0–7.
 *
 * The [diameterScale] is a rough multiplier on per-stitch yarn length relative to
 * medium (#4) worsted weight, used by the yarn estimator. Values from
 * crochet_context.md §8.
 */
enum class YarnWeight(
    val number: Int,
    val displayName: String,
    val diameterScale: Double,
) {
    LACE(0, "Lace", 0.4),
    SUPER_FINE(1, "Super Fine", 0.6),
    FINE(2, "Fine", 0.8),
    LIGHT(3, "Light", 0.9),
    MEDIUM(4, "Medium", 1.0),
    BULKY(5, "Bulky", 1.5),
    SUPER_BULKY(6, "Super Bulky", 2.0),
    JUMBO(7, "Jumbo", 2.5);

    companion object {
        fun fromNumber(n: Int): YarnWeight =
            entries.firstOrNull { it.number == n }
                ?: throw IllegalArgumentException("No YarnWeight for number $n")
    }
}
