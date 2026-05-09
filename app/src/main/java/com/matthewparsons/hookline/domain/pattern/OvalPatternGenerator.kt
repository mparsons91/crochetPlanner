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
 * Generates a pattern for a flat oval worked in rounds around a starting
 * chain (per crochet_context.md §7).
 *
 *   - Foundation: ch C, where C ≈ (length − width) × stitches_per_inch + 1.
 *     Round 1 works along both sides of this chain with chain-spaces at the
 *     two rounded ends.
 *   - Round 1 stitch count: 2 × centerStraight + 2 (one st in each end ch-sp).
 *   - Subsequent rounds: increase only at the two rounded ends, mirroring the
 *     circle increase pattern locally. We approximate this as a fixed
 *     +baseStitch.firstRoundStitches per round (split evenly between ends).
 *   - Number of rounds ≈ (width / 2) / row_height.
 */
object OvalPatternGenerator {
    fun generate(input: PatternInput): Pattern {
        require(input.shape is Shape.Oval) {
            "OvalPatternGenerator requires Shape.Oval, got ${input.shape::class.simpleName}"
        }
        val oval = input.shape
        val baseStitch = input.baseStitch
        val gauge = input.gauge
        val abbr = baseStitch.abbreviation

        val centerStraightStitches =
            max(1, ((oval.length.inches - oval.width.inches) * gauge.stitchesPerInch).roundToInt())
        val foundationChain = centerStraightStitches + 1

        val rowHeightInches = 1.0 / gauge.rowsPerInch
        val rounds = max(1, ((oval.width.inches / 2.0) / rowHeightInches).roundToInt())
        val perRoundIncrease = baseStitch.firstRoundStitches

        val round1Stitches = 2 * centerStraightStitches + 2

        val steps = buildList {
            add(
                PatternStep(
                    kind = StepKind.FOUNDATION,
                    number = 0,
                    instruction = "Ch $foundationChain.",
                    stitchCount = 0,
                )
            )
            add(
                PatternStep(
                    kind = StepKind.ROUND,
                    number = 1,
                    instruction = "Round 1: $abbr in 2nd ch from hook, $abbr in next ${centerStraightStitches - 1} ch, " +
                        "($abbr, ch 1, $abbr) in last ch. " +
                        "Working along the bottom of the foundation chain: $abbr in next $centerStraightStitches ch, " +
                        "($abbr, ch 1, $abbr) in same first ch. Sl st to first $abbr to join ($round1Stitches $abbr).",
                    stitchCount = round1Stitches,
                )
            )
            var runningTotal = round1Stitches
            for (r in 2..rounds) {
                runningTotal += perRoundIncrease
                add(
                    PatternStep(
                        kind = StepKind.ROUND,
                        number = r,
                        instruction = "Round $r: Work straight along the long sides; on each end, " +
                            "evenly distribute ${perRoundIncrease / 2} increase(s) " +
                            "(total +$perRoundIncrease). Sl st to join ($runningTotal $abbr).",
                        stitchCount = runningTotal,
                    )
                )
            }
        }

        val totalBaseStitches = (1..rounds).sumOf { round ->
            if (round == 1) round1Stitches else round1Stitches + (round - 1) * perRoundIncrease
        }
        val yarnEstimate = YarnEstimator.estimate(
            baseStitchCount = totalBaseStitches,
            chainCount = foundationChain,
            baseStitch = baseStitch,
            yarn = input.yarn,
        )

        return Pattern(
            input = input,
            startingChain = ChainSpec(
                count = foundationChain,
                description = "Ch $foundationChain — work along both sides of this chain in round 1.",
            ),
            steps = steps,
            totalBaseStitches = totalBaseStitches,
            yarnEstimate = yarnEstimate,
        )
    }
}
