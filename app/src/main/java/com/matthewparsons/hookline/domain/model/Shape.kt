package com.matthewparsons.hookline.domain.model

/**
 * The shape the user wants to crochet. MVP supports circle, oval, rectangle,
 * and square.
 */
sealed interface Shape {
    val displayName: String

    data class Circle(val diameter: Length) : Shape {
        override val displayName: String get() = "Circle"
    }

    data class Oval(val length: Length, val width: Length) : Shape {
        init {
            require(length.inches > width.inches) {
                "Oval length must be greater than width"
            }
        }

        override val displayName: String get() = "Oval"
    }

    data class Rectangle(val width: Length, val height: Length) : Shape {
        override val displayName: String get() = "Rectangle"
    }

    data class Square(val side: Length) : Shape {
        override val displayName: String get() = "Square"
    }
}
