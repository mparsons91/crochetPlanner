package com.matthewparsons.hookline.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GaugeTest {

    @Test
    fun `medium worsted defaults to about 3-1 stitches per inch for sc`() {
        val gauge = Gauge.defaultFor(YarnWeight.MEDIUM, BaseStitch.SINGLE_CROCHET)

        // CYC midpoint for #4 medium is 12.5 sc / 4" = 3.125 spi
        assertThat(gauge.stitchesPerInch).isWithin(0.001).of(3.125)
        // sc rows are roughly square
        assertThat(gauge.rowsPerInch).isWithin(0.001).of(3.125)
    }

    @Test
    fun `lace yarn defaults to about 9 stitches per inch`() {
        val gauge = Gauge.defaultFor(YarnWeight.LACE)
        assertThat(gauge.stitchesPerInch).isWithin(0.1).of(9.25)
    }

    @Test
    fun `dc rows are taller so rows-per-inch is less than stitches-per-inch`() {
        val gauge = Gauge.defaultFor(YarnWeight.MEDIUM, BaseStitch.DOUBLE_CROCHET)

        assertThat(gauge.rowsPerInch).isLessThan(gauge.stitchesPerInch)
        // dc heightFactor = 2, so rows-per-inch is half of stitches-per-inch
        assertThat(gauge.rowsPerInch).isWithin(0.001).of(gauge.stitchesPerInch / 2.0)
    }

    @Test
    fun `gauge rejects non-positive values`() {
        runCatching { Gauge(0.0, 1.0) }.let { assertThat(it.isFailure).isTrue() }
        runCatching { Gauge(1.0, -1.0) }.let { assertThat(it.isFailure).isTrue() }
    }
}
