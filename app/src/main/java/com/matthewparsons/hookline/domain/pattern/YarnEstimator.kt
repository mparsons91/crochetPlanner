package com.matthewparsons.hookline.domain.pattern

import com.matthewparsons.hookline.domain.model.BaseStitch
import com.matthewparsons.hookline.domain.model.YarnEstimate
import com.matthewparsons.hookline.domain.model.YarnWeight

/**
 * Estimates total yarn required for a pattern using:
 *   yarn ≈ (baseStitches × yarnPerStitch + chains × yarnPerChain) × diameterScale × (1 + margin)
 *
 * Per-stitch and per-chain lengths are for medium-weight yarn; we scale linearly
 * by the yarn's diameter relative to medium. Constants come from
 * crochet_context.md §8 and are best-effort estimates — the result is always
 * presented to the user as an estimate.
 */
object YarnEstimator {
    private const val CHAIN_PER_INCH_MEDIUM = 0.5
    const val MARGIN_FRACTION = 0.15

    fun estimate(
        baseStitchCount: Int,
        chainCount: Int,
        baseStitch: BaseStitch,
        yarn: YarnWeight,
    ): YarnEstimate {
        require(baseStitchCount >= 0) { "baseStitchCount must be non-negative" }
        require(chainCount >= 0) { "chainCount must be non-negative" }

        val scale = yarn.diameterScale
        val baseInches =
            baseStitchCount * baseStitch.yarnPerStitchInches * scale +
                chainCount * CHAIN_PER_INCH_MEDIUM * scale
        val withMarginInches = baseInches * (1 + MARGIN_FRACTION)
        return YarnEstimate(
            yards = withMarginInches / 36.0,
            metres = withMarginInches * 0.0254,
            marginFraction = MARGIN_FRACTION,
        )
    }
}
