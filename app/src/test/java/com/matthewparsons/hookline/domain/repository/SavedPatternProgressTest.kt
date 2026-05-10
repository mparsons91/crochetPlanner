package com.matthewparsons.hookline.domain.repository

import com.google.common.truth.Truth.assertThat
import com.matthewparsons.hookline.domain.model.HookSize
import com.matthewparsons.hookline.domain.model.Length
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.StepKind
import com.matthewparsons.hookline.domain.model.YarnWeight
import com.matthewparsons.hookline.domain.pattern.PatternEngine
import org.junit.Test

class SavedPatternProgressTest {

    private fun savedPattern(completedSteps: Set<Int> = emptySet()): SavedPattern {
        val pattern = PatternEngine.generate(
            PatternInput(
                shape = Shape.Circle(Length.inches(4.0)),
                yarn = YarnWeight.MEDIUM,
                hook = HookSize(5.5),
            )
        )
        return SavedPattern(
            id = "test-id",
            createdAtEpochMs = 0L,
            pattern = pattern,
            completedStepIndices = completedSteps,
        )
    }

    @Test
    fun `empty set is 0 percent and not complete`() {
        val saved = savedPattern()

        assertThat(saved.completedStitchCount).isEqualTo(0)
        assertThat(saved.remainingStitchCount).isEqualTo(saved.pattern.totalBaseStitches)
        assertThat(saved.percentComplete).isEqualTo(0f)
        assertThat(saved.isComplete).isFalse()
    }

    @Test
    fun `all step indices marked is 100 percent and complete`() {
        val all = savedPattern().pattern.steps.indices.toSet()
        val saved = savedPattern(completedSteps = all)

        assertThat(saved.percentComplete).isEqualTo(1f)
        assertThat(saved.remainingStitchCount).isEqualTo(0)
        assertThat(saved.isComplete).isTrue()
    }

    @Test
    fun `foundation alone does not move the percentage`() {
        // Foundation (index 0) has stitchCount = 0 so completing it shouldn't
        // change the stitch-based percentage. The user still gets visual
        // feedback (the checkbox), but progress stays at 0%.
        val sample = savedPattern()
        val foundationIndex = sample.pattern.steps
            .indexOfFirst { it.kind == StepKind.FOUNDATION }
        assertThat(foundationIndex).isAtLeast(0)

        val saved = savedPattern(completedSteps = setOf(foundationIndex))

        assertThat(saved.percentComplete).isEqualTo(0f)
        assertThat(saved.completedStitchCount).isEqualTo(0)
        assertThat(saved.isComplete).isFalse()
    }

    @Test
    fun `every round complete but missing foundation is 100 percent stitches yet not isComplete`() {
        val sample = savedPattern()
        val allRoundsOrRows = sample.pattern.steps.indices
            .filter { sample.pattern.steps[it].kind != StepKind.FOUNDATION }
            .toSet()

        val saved = savedPattern(completedSteps = allRoundsOrRows)

        // 100% by stitches because foundation has zero stitchCount,
        // but isComplete is false until user taps the foundation too.
        assertThat(saved.percentComplete).isEqualTo(1f)
        assertThat(saved.isComplete).isFalse()
    }

    @Test
    fun `completing some stitch-bearing rounds yields a partial percentage`() {
        val sample = savedPattern()
        // Mark just round 1.
        val firstRound = sample.pattern.steps
            .indexOfFirst { it.kind == StepKind.ROUND }
        assertThat(firstRound).isAtLeast(0)

        val saved = savedPattern(completedSteps = setOf(firstRound))
        val expectedCompleted = sample.pattern.steps[firstRound].stitchCount

        assertThat(saved.completedStitchCount).isEqualTo(expectedCompleted)
        assertThat(saved.remainingStitchCount).isEqualTo(
            sample.pattern.totalBaseStitches - expectedCompleted
        )
        assertThat(saved.percentComplete)
            .isWithin(1e-6f)
            .of(expectedCompleted.toFloat() / sample.pattern.totalBaseStitches)
        assertThat(saved.isComplete).isFalse()
    }

    @Test
    fun `out of range indices are ignored by stitch sum`() {
        val saved = savedPattern(completedSteps = setOf(999))

        assertThat(saved.completedStitchCount).isEqualTo(0)
        assertThat(saved.percentComplete).isEqualTo(0f)
    }
}
