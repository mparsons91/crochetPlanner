package com.matthewparsons.hookline.domain.pattern

import com.google.common.truth.Truth.assertThat
import com.matthewparsons.hookline.domain.model.HookSize
import com.matthewparsons.hookline.domain.model.Length
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.YarnWeight
import org.junit.Test

class PatternEngineTest {

    private fun input(shape: Shape) = PatternInput(
        shape = shape,
        yarn = YarnWeight.MEDIUM,
        hook = HookSize(5.5),
    )

    @Test
    fun `square dispatches to rectangle generator with width equals height`() {
        // 5" × 3.125 = 15.625 → 16
        val pattern = PatternEngine.generate(input(Shape.Square(Length.inches(5.0))))

        assertThat(pattern.totalBaseStitches).isEqualTo(16 * 16)
        assertThat(pattern.startingChain.count).isEqualTo(16 + 1)
    }

    @Test
    fun `each shape returns a non-empty pattern`() {
        val shapes = listOf(
            Shape.Circle(Length.inches(4.0)),
            Shape.Oval(Length.inches(6.0), Length.inches(4.0)),
            Shape.Rectangle(Length.inches(6.0), Length.inches(8.0)),
            Shape.Square(Length.inches(5.0)),
        )

        shapes.forEach { shape ->
            val pattern = PatternEngine.generate(input(shape))
            assertThat(pattern.totalBaseStitches).isGreaterThan(0)
            assertThat(pattern.steps).isNotEmpty()
            assertThat(pattern.yarnEstimate.yards).isGreaterThan(0.0)
        }
    }

    @Test
    fun `centimetres and inches with same physical size produce same pattern`() {
        val inches = PatternEngine.generate(input(Shape.Circle(Length.inches(4.0))))
        val cm = PatternEngine.generate(input(Shape.Circle(Length.centimetres(4.0 * 2.54))))

        assertThat(cm.totalBaseStitches).isEqualTo(inches.totalBaseStitches)
        assertThat(cm.startingChain.count).isEqualTo(inches.startingChain.count)
    }
}
