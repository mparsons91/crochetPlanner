package com.matthewparsons.hookline.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class LengthUnit {
    INCHES,
    CENTIMETRES;

    fun toInches(value: Double): Double = when (this) {
        INCHES -> value
        CENTIMETRES -> value / 2.54
    }

    fun toCentimetres(value: Double): Double = when (this) {
        INCHES -> value * 2.54
        CENTIMETRES -> value
    }
}

@Serializable
data class Length(val value: Double, val unit: LengthUnit) {
    init {
        require(value > 0) { "Length must be positive, was $value $unit" }
    }

    val inches: Double get() = unit.toInches(value)
    val centimetres: Double get() = unit.toCentimetres(value)

    companion object {
        fun inches(v: Double) = Length(v, LengthUnit.INCHES)
        fun centimetres(v: Double) = Length(v, LengthUnit.CENTIMETRES)
    }
}
