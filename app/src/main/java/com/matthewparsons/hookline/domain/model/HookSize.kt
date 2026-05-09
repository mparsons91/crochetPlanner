package com.matthewparsons.hookline.domain.model

import kotlinx.serialization.Serializable

/**
 * Crochet hook size in millimetres. Hooks are sold against this metric scale
 * (sometimes labelled with a US letter/number); millimetres is the canonical
 * representation.
 */
@JvmInline
@Serializable
value class HookSize(val millimetres: Double) {
    init {
        require(millimetres > 0) { "Hook size must be positive, was $millimetres" }
    }
}
