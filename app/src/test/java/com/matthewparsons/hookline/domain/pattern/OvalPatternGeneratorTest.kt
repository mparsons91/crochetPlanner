package com.matthewparsons.hookline.domain.pattern

import com.google.common.truth.Truth.assertThat
import com.matthewparsons.hookline.domain.model.HookSize
import com.matthewparsons.hookline.domain.model.Length
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.StepKind
import com.matthewparsons.hookline.domain.model.YarnWeight
import org.junit.Test

class OvalPatternGeneratorTest {

    private fun mediumOval(lengthIn: Double, widthIn: Double) = PatternInput(
        shape = Shape.Oval(Length.inches(lengthIn), Length.inches(widthIn)),
        yarn = YarnWeight.MEDIUM,
        hook = HookSize(5.5),
    )

    @Test
    fun `6x4 medium-yarn oval has foundation chain of 7`() {
        // (6 - 4) × 3.125 = 6.25 → 6 stitches; chain = 6 + 1 = 7
        val pattern = OvalPatternGenerator.generate(mediumOval(6.0, 4.0))

        assertThat(pattern.startingChain.count).isEqualTo(7)
    }

    @Test
    fun `oval round 1 has 2 times center plus 2 stitches`() {
        val pattern = OvalPatternGenerator.generate(mediumOval(6.0, 4.0))

        val firstRound = pattern.steps.first { it.kind == StepKind.ROUND }
        // centerStraight = 6 → 2*6 + 2 = 14
        assertThat(firstRound.stitchCount).isEqualTo(14)
    }

    @Test
    fun `oval rounds increase strictly`() {
        val pattern = OvalPatternGenerator.generate(mediumOval(8.0, 4.0))
        val roundCounts = pattern.steps
            .filter { it.kind == StepKind.ROUND }
            .map { it.stitchCount }

        assertThat(roundCounts).isInOrder()
        assertThat(roundCounts.zipWithNext().all { (a, b) -> b > a }).isTrue()
    }

    @Test
    fun `oval rejects width greater than length`() {
        runCatching {
            Shape.Oval(length = Length.inches(2.0), width = Length.inches(4.0))
        }.let { assertThat(it.isFailure).isTrue() }
    }

    @Test
    fun `wrong shape throws`() {
        val bad = PatternInput(
            shape = Shape.Circle(Length.inches(2.0)),
            yarn = YarnWeight.MEDIUM,
            hook = HookSize(5.5),
        )

        runCatching { OvalPatternGenerator.generate(bad) }.let {
            assertThat(it.isFailure).isTrue()
        }
    }
}
