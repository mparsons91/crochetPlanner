package com.matthewparsons.hookline.domain.model

/**
 * Crochet hook size in millimetres. Hooks are sold against this metric scale
 * (sometimes labelled with a US letter/number); millimetres is the canonical
 * representation.
 */
@JvmInline
value class HookSize(val millimetres: Double) {
    init {
        require(millimetres > 0) { "Hook size must be positive, was $millimetres" }
    }
}
