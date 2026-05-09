package com.matthewparsons.hookline.domain.model

import kotlinx.serialization.Serializable

/**
 * The shape the user wants to crochet. MVP supports circle, oval, rectangle,
 * and square.
 */
@Serializable
sealed interface Shape {
    val displayName: String

    @Serializable
    data class Circle(val diameter: Length) : Shape {
        override val displayName: String get() = "Circle"
    }

    @Serializable
    data class Oval(val length: Length, val width: Length) : Shape {
        init {
            require(length.inches > width.inches) {
                "Oval length must be greater than width"
            }
        }

        override val displayName: String get() = "Oval"
    }

    @Serializable
    data class Rectangle(val width: Length, val height: Length) : Shape {
        override val displayName: String get() = "Rectangle"
    }

    @Serializable
    data class Square(val side: Length) : Shape {
        override val displayName: String get() = "Square"
    }
}
