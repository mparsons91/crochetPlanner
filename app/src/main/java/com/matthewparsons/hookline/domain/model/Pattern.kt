package com.matthewparsons.hookline.domain.model

import kotlinx.serialization.Serializable

/**
 * The user's request for a generated pattern. All fields except [shape] have
 * sensible defaults derived from [yarn].
 */
@Serializable
data class PatternInput(
    val shape: Shape,
    val yarn: YarnWeight,
    val hook: HookSize,
    val baseStitch: BaseStitch = BaseStitch.SINGLE_CROCHET,
    val gauge: Gauge = Gauge.defaultFor(yarn, baseStitch),
)

/**
 * The starting chain of a pattern.
 *
 * For shapes worked flat (rectangle, square), [count] includes both the
 * working stitches and the turning chain. For circles worked from a magic
 * ring, [count] is 0.
 */
@Serializable
data class ChainSpec(
    val count: Int,
    val description: String,
)

@Serializable
enum class StepKind {
    FOUNDATION,
    ROW,
    ROUND,
}

/**
 * One step in the pattern: a foundation chain, a row, or a round.
 *
 * [stitchCount] is the number of base stitches added in this step (excluding
 * chain stitches). For ROUNDs in a circle, this is the running total of the
 * round (e.g. 6, 12, 18 …); for ROWs in a rectangle, this is the row width.
 */
@Serializable
data class PatternStep(
    val kind: StepKind,
    val number: Int,
    val instruction: String,
    val stitchCount: Int,
)

/**
 * Estimated yarn required for a pattern, including a 15% margin for tails and
 * weaving in ends.
 */
@Serializable
data class YarnEstimate(
    val yards: Double,
    val metres: Double,
    val marginFraction: Double,
)

/**
 * A fully specified, generated pattern ready for display or persistence.
 */
@Serializable
data class Pattern(
    val input: PatternInput,
    val startingChain: ChainSpec,
    val steps: List<PatternStep>,
    val totalBaseStitches: Int,
    val yarnEstimate: YarnEstimate,
)
