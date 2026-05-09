package com.matthewparsons.hookline.domain.pattern

import com.google.common.truth.Truth.assertThat
import com.matthewparsons.hookline.domain.model.HookSize
import com.matthewparsons.hookline.domain.model.Length
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.StepKind
import com.matthewparsons.hookline.domain.model.YarnWeight
import org.junit.Test

class CirclePatternGeneratorTest {

    private fun mediumCircle(diameterInches: Double) = PatternInput(
        shape = Shape.Circle(Length.inches(diameterInches)),
        yarn = YarnWeight.MEDIUM,
        hook = HookSize(5.5),
    )

    @Test
    fun `4-inch medium-yarn sc circle has 6 rounds and 126 stitches`() {
        // radius 2", rows-per-inch 3.125 → 6.25 → 6 rounds
        // 6 × 6 × 7 / 2 = 126 stitches total
        val pattern = CirclePatternGenerator.generate(mediumCircle(4.0))

        assertThat(pattern.totalBaseStitches).isEqualTo(126)
        // foundation + 6 rounds = 7 steps
        assertThat(pattern.steps).hasSize(7)
    }

    @Test
    fun `circle starting chain count is 0 for magic ring`() {
        val pattern = CirclePatternGenerator.generate(mediumCircle(4.0))

        assertThat(pattern.startingChain.count).isEqualTo(0)
        assertThat(pattern.startingChain.description).contains("Magic ring")
    }

    @Test
    fun `circle round stitch counts follow N, 2N, 3N pattern`() {
        val pattern = CirclePatternGenerator.generate(mediumCircle(4.0))

        val roundCounts = pattern.steps
            .filter { it.kind == StepKind.ROUND }
            .map { it.stitchCount }

        assertThat(roundCounts).containsExactly(6, 12, 18, 24, 30, 36).inOrder()
    }

    @Test
    fun `tiny circle still produces at least one round`() {
        val pattern = CirclePatternGenerator.generate(mediumCircle(0.1))

        val roundCount = pattern.steps.count { it.kind == StepKind.ROUND }
        assertThat(roundCount).isAtLeast(1)
    }

    @Test
    fun `larger diameter produces more rounds`() {
        val small = CirclePatternGenerator.generate(mediumCircle(4.0))
        val large = CirclePatternGenerator.generate(mediumCircle(10.0))

        val smallRounds = small.steps.count { it.kind == StepKind.ROUND }
        val largeRounds = large.steps.count { it.kind == StepKind.ROUND }

        assertThat(largeRounds).isGreaterThan(smallRounds)
    }

    @Test
    fun `wrong shape throws`() {
        val bad = PatternInput(
            shape = Shape.Rectangle(Length.inches(2.0), Length.inches(2.0)),
            yarn = YarnWeight.MEDIUM,
            hook = HookSize(5.5),
        )

        runCatching { CirclePatternGenerator.generate(bad) }.let {
            assertThat(it.isFailure).isTrue()
        }
    }
}
