package com.matthewparsons.hookline.domain.model

/**
 * Stitches-per-inch and rows-per-inch at a given yarn/hook combination.
 *
 * Defaults are derived from the Craft Yarn Council standard sc gauge ranges
 * (midpoint of each range, then converted to per-inch). Real gauge varies by
 * crocheter; users may override post-MVP.
 */
data class Gauge(
    val stitchesPerInch: Double,
    val rowsPerInch: Double,
) {
    init {
        require(stitchesPerInch > 0) { "stitchesPerInch must be positive" }
        require(rowsPerInch > 0) { "rowsPerInch must be positive" }
    }

    companion object {
        /**
         * Default gauge for [yarn] worked in [baseStitch], using the CYC sc gauge
         * midpoint and adjusting rows-per-inch by the stitch's height factor.
         */
        fun defaultFor(
            yarn: YarnWeight,
            baseStitch: BaseStitch = BaseStitch.SINGLE_CROCHET,
        ): Gauge {
            val scStitchesPerInch = scStitchesPerInch(yarn)
            return Gauge(
                stitchesPerInch = scStitchesPerInch,
                rowsPerInch = scStitchesPerInch / baseStitch.heightFactor,
            )
        }

        // Midpoint of CYC sc gauge ranges (stitches per 4"), converted to per-inch.
        private fun scStitchesPerInch(yarn: YarnWeight): Double = when (yarn) {
            YarnWeight.LACE -> 37.0 / 4.0          // 32–42
            YarnWeight.SUPER_FINE -> 26.5 / 4.0    // 21–32
            YarnWeight.FINE -> 18.0 / 4.0          // 16–20
            YarnWeight.LIGHT -> 14.5 / 4.0         // 12–17
            YarnWeight.MEDIUM -> 12.5 / 4.0        // 11–14
            YarnWeight.BULKY -> 9.5 / 4.0          // 8–11
            YarnWeight.SUPER_BULKY -> 7.5 / 4.0    // 6–9
            YarnWeight.JUMBO -> 4.0 / 4.0          // <6, use 4
        }
    }
}
