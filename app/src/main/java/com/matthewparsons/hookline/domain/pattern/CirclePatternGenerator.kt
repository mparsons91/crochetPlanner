package com.matthewparsons.hookline.domain.pattern

import com.matthewparsons.hookline.domain.model.ChainSpec
import com.matthewparsons.hookline.domain.model.Pattern
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.PatternStep
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.StepKind
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Generates a pattern for a flat circle worked in the round from a magic ring.
 *
 * Math (per crochet_context.md §7):
 *   - Round 1: N stitches into the ring (N = baseStitch.firstRoundStitches; 6 for sc).
 *   - Round k (k ≥ 2): k × N stitches; one extra straight stitch between
 *     increases each round.
 *   - Total stitches through round R: N × R(R+1)/2.
 *   - One round adds roughly one stitch-height of radius, so
 *     rounds = round(radius_inches × rows_per_inch).
 */
object CirclePatternGenerator {
    fun generate(input: PatternInput): Pattern {
        require(input.shape is Shape.Circle) {
            "CirclePatternGenerator requires Shape.Circle, got ${input.shape::class.simpleName}"
        }
        val circle = input.shape
        val baseStitch = input.baseStitch
        val gauge = input.gauge
        val abbr = baseStitch.abbreviation

        val radiusInches = circle.diameter.inches / 2.0
        val rounds = max(1, (radiusInches * gauge.rowsPerInch).roundToInt())
        val n0 = baseStitch.firstRoundStitches

        val steps = buildList {
            add(
                PatternStep(
                    kind = StepKind.FOUNDATION,
                    number = 0,
                    instruction = "Make a magic ring (or ch 2 and work into 2nd ch from hook).",
                    stitchCount = 0,
                )
            )
            add(
                PatternStep(
                    kind = StepKind.ROUND,
                    number = 1,
                    instruction = "Round 1: $n0 $abbr into ring; pull tail tight to close ($n0 $abbr).",
                    stitchCount = n0,
                )
            )
            if (rounds >= 2) {
                add(
                    PatternStep(
                        kind = StepKind.ROUND,
                        number = 2,
                        instruction = "Round 2: 2 $abbr in each st around (${2 * n0} $abbr).",
                        stitchCount = 2 * n0,
                    )
                )
            }
            for (r in 3..rounds) {
                val between = r - 2
                val rowTotal = r * n0
                val phrase = if (between == 1) "next st" else "next $between sts"
                add(
                    PatternStep(
                        kind = StepKind.ROUND,
                        number = r,
                        instruction = "Round $r: [$abbr in $phrase, 2 $abbr in next st] $n0 times ($rowTotal $abbr).",
                        stitchCount = rowTotal,
                    )
                )
            }
        }

        val totalBaseStitches = n0 * rounds * (rounds + 1) / 2
        val yarnEstimate = YarnEstimator.estimate(
            baseStitchCount = totalBaseStitches,
            chainCount = 0,
            baseStitch = baseStitch,
            yarn = input.yarn,
        )

        return Pattern(
            input = input,
            startingChain = ChainSpec(
                count = 0,
                description = "Magic ring — no foundation chain.",
            ),
            steps = steps,
            totalBaseStitches = totalBaseStitches,
            yarnEstimate = yarnEstimate,
        )
    }
}
