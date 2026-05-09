package com.matthewparsons.hookline.domain.pattern

import com.matthewparsons.hookline.domain.model.Pattern
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape

/**
 * Single entry point for generating a pattern. Dispatches to the per-shape
 * generator. Square delegates to the rectangle generator with width = height.
 */
object PatternEngine {
    fun generate(input: PatternInput): Pattern = when (val shape = input.shape) {
        is Shape.Circle -> CirclePatternGenerator.generate(input)
        is Shape.Oval -> OvalPatternGenerator.generate(input)
        is Shape.Rectangle -> RectanglePatternGenerator.generate(input)
        is Shape.Square -> RectanglePatternGenerator.generate(
            input.copy(shape = Shape.Rectangle(width = shape.side, height = shape.side))
        )
    }
}
