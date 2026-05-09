package com.matthewparsons.hookline.domain.pattern

import com.google.common.truth.Truth.assertThat
import com.matthewparsons.hookline.domain.model.BaseStitch
import com.matthewparsons.hookline.domain.model.YarnWeight
import org.junit.Test

class YarnEstimatorTest {

    @Test
    fun `100 sc with medium yarn estimates about 4 yards including margin`() {
        // base = 100 * 1.25 * 1.0 = 125"
        // with 15% margin = 143.75"
        // yards = 143.75 / 36 = 3.993...
        val estimate = YarnEstimator.estimate(
            baseStitchCount = 100,
            chainCount = 0,
            baseStitch = BaseStitch.SINGLE_CROCHET,
            yarn = YarnWeight.MEDIUM,
        )

        assertThat(estimate.yards).isWithin(0.01).of(3.993)
        assertThat(estimate.marginFraction).isWithin(0.001).of(0.15)
    }

    @Test
    fun `bulky yarn uses more yarn than medium for same stitch count`() {
        val medium = YarnEstimator.estimate(100, 0, BaseStitch.SINGLE_CROCHET, YarnWeight.MEDIUM)
        val bulky = YarnEstimator.estimate(100, 0, BaseStitch.SINGLE_CROCHET, YarnWeight.BULKY)

        // Bulky diameterScale is 1.5× medium
        assertThat(bulky.yards / medium.yards).isWithin(0.001).of(1.5)
    }

    @Test
    fun `chain stitches contribute additively`() {
        val noChains = YarnEstimator.estimate(100, 0, BaseStitch.SINGLE_CROCHET, YarnWeight.MEDIUM)
        val withChains = YarnEstimator.estimate(100, 50, BaseStitch.SINGLE_CROCHET, YarnWeight.MEDIUM)

        assertThat(withChains.yards).isGreaterThan(noChains.yards)
    }

    @Test
    fun `metres and yards agree on the same physical length`() {
        val estimate = YarnEstimator.estimate(100, 0, BaseStitch.SINGLE_CROCHET, YarnWeight.MEDIUM)

        // 1 yard = 0.9144 metres
        assertThat(estimate.metres / estimate.yards).isWithin(0.001).of(0.9144)
    }

    @Test
    fun `zero stitches produces zero estimate`() {
        val estimate = YarnEstimator.estimate(0, 0, BaseStitch.SINGLE_CROCHET, YarnWeight.MEDIUM)
        assertThat(estimate.yards).isEqualTo(0.0)
    }
}
