package com.matthewparsons.hookline.domain.model

import kotlinx.serialization.Serializable

/**
 * The fundamental stitch a pattern is built from.
 *
 * - [turningChain]: number of chain stitches at the end of a row to raise the
 *   hook to the height of the next row's stitches.
 * - [turningChainCountsAsStitch]: whether that turning chain occupies the
 *   position of the first stitch in the next row.
 * - [heightFactor]: rough height of one stitch relative to a single crochet.
 *   Used to derive rows-per-inch from stitches-per-inch.
 * - [yarnPerStitchInches]: rough yarn used per stitch with medium-weight yarn.
 *   Scale by [YarnWeight.diameterScale] for other weights.
 * - [firstRoundStitches]: canonical number of stitches in round 1 of a flat
 *   circle worked in this base stitch (6 for sc, 8 for hdc, 12 for dc).
 *
 * Values from crochet_context.md §3 and §8.
 */
@Serializable
enum class BaseStitch(
    val abbreviation: String,
    val displayName: String,
    val turningChain: Int,
    val turningChainCountsAsStitch: Boolean,
    val heightFactor: Double,
    val yarnPerStitchInches: Double,
    val firstRoundStitches: Int,
) {
    SINGLE_CROCHET("sc", "Single crochet", 1, false, 1.0, 1.25, 6),
    HALF_DOUBLE_CROCHET("hdc", "Half double crochet", 2, false, 1.5, 1.75, 8),
    DOUBLE_CROCHET("dc", "Double crochet", 3, true, 2.0, 2.5, 12),
    TREBLE_CROCHET("tr", "Treble crochet", 4, true, 3.0, 3.5, 16),
}
