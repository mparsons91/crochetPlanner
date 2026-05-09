package com.matthewparsons.hookline.domain.pattern

import com.google.common.truth.Truth.assertThat
import com.matthewparsons.hookline.domain.model.HookSize
import com.matthewparsons.hookline.domain.model.Length
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.StepKind
import com.matthewparsons.hookline.domain.model.YarnWeight
import org.junit.Test

class RectanglePatternGeneratorTest {

    private fun mediumRect(widthIn: Double, heightIn: Double) = PatternInput(
        shape = Shape.Rectangle(Length.inches(widthIn), Length.inches(heightIn)),
        yarn = YarnWeight.MEDIUM,
        hook = HookSize(5.5),
    )

    @Test
    fun `6 by 8 medium-yarn sc rectangle is 19 stitches wide and 25 rows tall`() {
        // 6 × 3.125 = 18.75 → 19; 8 × 3.125 = 25 → 25
        val pattern = RectanglePatternGenerator.generate(mediumRect(6.0, 8.0))

        assertThat(pattern.totalBaseStitches).isEqualTo(19 * 25)
        // foundation + 25 rows
        assertThat(pattern.steps).hasSize(26)
    }

    @Test
    fun `starting chain is base stitches plus turning chain`() {
        val pattern = RectanglePatternGenerator.generate(mediumRect(6.0, 8.0))

        // 19 wide + 1 turning ch for sc = 20
        assertThat(pattern.startingChain.count).isEqualTo(20)
    }

    @Test
    fun `every row uses the same width`() {
        val pattern = RectanglePatternGenerator.generate(mediumRect(6.0, 8.0))
        val rowCounts = pattern.steps
            .filter { it.kind == StepKind.ROW }
            .map { it.stitchCount }
            .distinct()

        assertThat(rowCounts).containsExactly(19)
    }

    @Test
    fun `tiny rectangle still produces at least 1 row and 1 stitch wide`() {
        val pattern = RectanglePatternGenerator.generate(mediumRect(0.05, 0.05))

        assertThat(pattern.totalBaseStitches).isAtLeast(1)
        assertThat(pattern.steps.count { it.kind == StepKind.ROW }).isAtLeast(1)
    }

    @Test
    fun `wrong shape throws`() {
        val bad = PatternInput(
            shape = Shape.Circle(Length.inches(2.0)),
            yarn = YarnWeight.MEDIUM,
            hook = HookSize(5.5),
        )

        runCatching { RectanglePatternGenerator.generate(bad) }.let {
            assertThat(it.isFailure).isTrue()
        }
    }
}
