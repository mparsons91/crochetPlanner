package com.matthewparsons.hookline.data.local

import com.google.common.truth.Truth.assertThat
import com.matthewparsons.hookline.domain.model.HookSize
import com.matthewparsons.hookline.domain.model.Length
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.YarnWeight
import com.matthewparsons.hookline.domain.pattern.PatternEngine
import com.matthewparsons.hookline.domain.repository.SavedPattern
import kotlinx.serialization.json.Json
import org.junit.Test

class PatternEntityMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun savedPattern(shape: Shape): SavedPattern {
        val pattern = PatternEngine.generate(
            PatternInput(
                shape = shape,
                yarn = YarnWeight.MEDIUM,
                hook = HookSize(5.5),
            )
        )
        return SavedPattern(
            id = "test-id-123",
            createdAtEpochMs = 1_700_000_000_000L,
            pattern = pattern,
        )
    }

    @Test
    fun `round-trip preserves a circle pattern`() {
        val original = savedPattern(Shape.Circle(Length.inches(4.0)))

        val restored = original.toEntity(json).toSavedPattern(json)

        assertThat(restored).isEqualTo(original)
    }

    @Test
    fun `round-trip preserves an oval pattern`() {
        val original = savedPattern(Shape.Oval(Length.inches(6.0), Length.inches(4.0)))

        val restored = original.toEntity(json).toSavedPattern(json)

        assertThat(restored).isEqualTo(original)
    }

    @Test
    fun `round-trip preserves a rectangle pattern`() {
        val original = savedPattern(Shape.Rectangle(Length.inches(6.0), Length.inches(8.0)))

        val restored = original.toEntity(json).toSavedPattern(json)

        assertThat(restored).isEqualTo(original)
    }

    @Test
    fun `round-trip preserves a square pattern`() {
        val original = savedPattern(Shape.Square(Length.inches(5.0)))

        val restored = original.toEntity(json).toSavedPattern(json)

        assertThat(restored).isEqualTo(original)
    }

    @Test
    fun `denormalized columns are populated from the domain pattern`() {
        val saved = savedPattern(Shape.Circle(Length.inches(4.0)))

        val entity = saved.toEntity(json)

        assertThat(entity.shapeName).isEqualTo("Circle")
        assertThat(entity.yarnNumber).isEqualTo(YarnWeight.MEDIUM.number)
        assertThat(entity.totalBaseStitches).isEqualTo(saved.pattern.totalBaseStitches)
        assertThat(entity.estimatedYards).isEqualTo(saved.pattern.yarnEstimate.yards)
    }

    @Test
    fun `centimetres dimensions survive round-trip`() {
        val original = savedPattern(Shape.Circle(Length.centimetres(10.0)))

        val restored = original.toEntity(json).toSavedPattern(json)

        assertThat(restored).isEqualTo(original)
    }
}
