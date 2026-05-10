package com.matthewparsons.hookline.domain.repository

import com.matthewparsons.hookline.domain.model.Pattern

/**
 * A persisted pattern: the generator output [pattern] paired with the storage
 * metadata assigned at save time, plus user progress through the steps.
 */
data class SavedPattern(
    val id: String,
    val createdAtEpochMs: Long,
    val pattern: Pattern,
    val completedStepIndices: Set<Int> = emptySet(),
)

/**
 * Total base stitches counted in the steps the user has marked complete.
 * Steps with `stitchCount = 0` (the foundation chain) contribute 0.
 */
val SavedPattern.completedStitchCount: Int
    get() = completedStepIndices.sumOf { idx ->
        pattern.steps.getOrNull(idx)?.stitchCount ?: 0
    }

val SavedPattern.remainingStitchCount: Int
    get() = (pattern.totalBaseStitches - completedStitchCount).coerceAtLeast(0)

/**
 * Progress as a fraction in `[0, 1]`, based on total base stitches per
 * Phase 4.5 §1. Returns 0 when the pattern has no stitches (degenerate
 * case).
 */
val SavedPattern.percentComplete: Float
    get() {
        val total = pattern.totalBaseStitches
        if (total <= 0) return 0f
        return (completedStitchCount.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

/**
 * "Complete" means every step has been tapped, including the foundation —
 * the user has acknowledged each instruction.
 */
val SavedPattern.isComplete: Boolean
    get() = pattern.steps.isNotEmpty() &&
        completedStepIndices.size == pattern.steps.size
