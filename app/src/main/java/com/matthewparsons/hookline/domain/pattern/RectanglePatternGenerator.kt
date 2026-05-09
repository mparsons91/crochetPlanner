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
 * Generates a pattern for a flat rectangle worked in rows. The square shape
 * delegates here with width = height.
 *
 * Output shape:
 *   - Foundation chain: widthStitches + turningChain
 *   - Row 1: works back across the foundation chain
 *   - Rows 2..N: ch turningChain, turn, work across
 */
object RectanglePatternGenerator {
    fun generate(input: PatternInput): Pattern {
        require(input.shape is Shape.Rectangle) {
            "RectanglePatternGenerator requires Shape.Rectangle, got ${input.shape::class.simpleName}"
        }
        val rect = input.shape
        val baseStitch = input.baseStitch
        val gauge = input.gauge
        val abbr = baseStitch.abbreviation
        val abbrCap = abbr.replaceFirstChar { it.titlecase() }
        val turningChain = baseStitch.turningChain

        val widthStitches = max(1, (rect.width.inches * gauge.stitchesPerInch).roundToInt())
        val heightRows = max(1, (rect.height.inches * gauge.rowsPerInch).roundToInt())
        val foundationChain = widthStitches + turningChain
        val ordinalInsertChain = ordinal(turningChain + 1)

        val steps = buildList {
            add(
                PatternStep(
                    kind = StepKind.FOUNDATION,
                    number = 0,
                    instruction = "Ch $foundationChain ($widthStitches base + $turningChain turning).",
                    stitchCount = 0,
                )
            )
            add(
                PatternStep(
                    kind = StepKind.ROW,
                    number = 1,
                    instruction = "Row 1: $abbrCap in $ordinalInsertChain ch from hook, $abbr in each ch across ($widthStitches $abbr).",
                    stitchCount = widthStitches,
                )
            )
            for (r in 2..heightRows) {
                add(
                    PatternStep(
                        kind = StepKind.ROW,
                        number = r,
                        instruction = "Row $r: Ch $turningChain, turn. $abbrCap in each st across ($widthStitches $abbr).",
                        stitchCount = widthStitches,
                    )
                )
            }
        }

        val totalBaseStitches = widthStitches * heightRows
        val totalChains = foundationChain + (heightRows - 1) * turningChain
        val yarnEstimate = YarnEstimator.estimate(
            baseStitchCount = totalBaseStitches,
            chainCount = totalChains,
            baseStitch = baseStitch,
            yarn = input.yarn,
        )

        return Pattern(
            input = input,
            startingChain = ChainSpec(
                count = foundationChain,
                description = "Ch $foundationChain — $widthStitches base stitches plus $turningChain turning chain.",
            ),
            steps = steps,
            totalBaseStitches = totalBaseStitches,
            yarnEstimate = yarnEstimate,
        )
    }
}

internal fun ordinal(n: Int): String = when (n) {
    1 -> "1st"
    2 -> "2nd"
    3 -> "3rd"
    21 -> "21st"
    22 -> "22nd"
    23 -> "23rd"
    else -> "${n}th"
}
