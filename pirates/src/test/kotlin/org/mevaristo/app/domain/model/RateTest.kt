package org.mevaristo.app.domain.model

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mevaristo.app.domain.model.Rate.TimeSeries.TimeSeriesEntry
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RateTest {
    companion object {
        @JvmStatic
        fun rateProvider(): Stream<Rate> = Stream.of(
            Rate("a", Rate.TimeSeries.of(
                listOf(
                    TimeSeriesEntry(LocalDateTime.of(2000, 2, 2, 0, 0), .0),
                    TimeSeriesEntry(LocalDateTime.of(1999, 2, 2, 0, 0), .0),
                    TimeSeriesEntry(LocalDateTime.of(1999, 2, 2, 0, 0), .0),
                    TimeSeriesEntry(LocalDateTime.of(2001, 2, 2, 0, 0), .0),
                    TimeSeriesEntry(LocalDateTime.of(2005, 2, 2, 0, 0), .0),
                    TimeSeriesEntry(LocalDateTime.of(2003, 2, 2, 0, 0), .0),
                )
            )),
            Rate("b", Rate.TimeSeries.of(
                listOf(
                    TimeSeriesEntry(LocalDateTime.of(2000, 2, 2, 0, 0), .2),
                    TimeSeriesEntry(LocalDateTime.of(2000, 2, 5, 0, 0), .3),
                    TimeSeriesEntry(LocalDateTime.of(2000, 3, 2, 0, 0), .4),
                    TimeSeriesEntry(LocalDateTime.of(2005, 2, 2, 0, 0), .5),
                    TimeSeriesEntry(LocalDateTime.of(2003, 2, 2, 0, 0), .6),
                )
            )),
            Rate("c", Rate.TimeSeries.of(
                listOf(
                    TimeSeriesEntry(LocalDateTime.of(2011, 2, 2, 0, 0), .1),
                    TimeSeriesEntry(LocalDateTime.of(1999, 2, 2, 0, 0), .0),
                    TimeSeriesEntry(LocalDateTime.of(2009, 2, 2, 0, 0), .1),
                    TimeSeriesEntry(LocalDateTime.of(1992, 12, 25, 0, 0), .0),
                    TimeSeriesEntry(LocalDateTime.of(2005, 2, 2, 0, 0), .1),
                    TimeSeriesEntry(LocalDateTime.of(2010, 2, 2, 0, 0), .0),
                )
            )),
        )
    }

    @ParameterizedTest
    @MethodSource("rateProvider")
    fun `Time series should be ordered by date, ascending`(rate: Rate) {

        val tsIterator = rate.timeSeries.iterator()
        while (tsIterator.hasNext()) {
            val a = tsIterator.next()
            if (tsIterator.hasNext()) {
                val b = tsIterator.next()

                assertTrue(a.key < b.key)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("rateProvider")
    fun `Time series should return the closest adjacent date before a target`(rate: Rate) {
        val expected = mapOf(
            Pair("a", LocalDateTime.of(2000, 2, 2, 0, 0)),
            Pair("b", LocalDateTime.of(2000, 3, 2, 0, 0)),
            Pair("c", LocalDateTime.of(1999, 2, 2, 0, 0)),
        )[rate.label]

        assertNotNull(rate.timeSeries.getAdjacentBefore(LocalDateTime.of(2001, 2, 2, 0, 0)))
        assertTrue(rate.timeSeries.getAdjacentBefore(LocalDateTime.of(2001, 2, 2, 0, 0))!!.dateTime.isEqual(expected))
    }

    //TODO Test for adjacent after

    //TODO Test for adjacent null - before & after
}