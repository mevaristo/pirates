package org.mevaristo.app.domain.model

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RateTest {
    companion object {
        @JvmStatic
        fun rateProvider(): Stream<Rate> = Stream.of(
            Rate("a", TimeSeries.of(
                listOf(
                    TimeSeriesEntry(LocalDate.of(2000, 2, 2), .0),
                    TimeSeriesEntry(LocalDate.of(1999, 2, 2), .0),
                    TimeSeriesEntry(LocalDate.of(1999, 2, 2), .0),
                    TimeSeriesEntry(LocalDate.of(2001, 2, 2), .0),
                    TimeSeriesEntry(LocalDate.of(2005, 2, 2), .0),
                    TimeSeriesEntry(LocalDate.of(2003, 2, 2), .0),
                )
            )),
            Rate("b", TimeSeries.of(
                listOf(
                    TimeSeriesEntry(LocalDate.of(2000, 2, 2), .2),
                    TimeSeriesEntry(LocalDate.of(2000, 2, 5), .3),
                    TimeSeriesEntry(LocalDate.of(2000, 3, 2), .4),
                    TimeSeriesEntry(LocalDate.of(2005, 2, 2), .5),
                    TimeSeriesEntry(LocalDate.of(2003, 2, 2), .6),
                )
            )),
            Rate("c", TimeSeries.of(
                listOf(
                    TimeSeriesEntry(LocalDate.of(2011, 2, 2), .1),
                    TimeSeriesEntry(LocalDate.of(1999, 2, 2), .0),
                    TimeSeriesEntry(LocalDate.of(2009, 2, 2), .1),
                    TimeSeriesEntry(LocalDate.of(1992, 12, 25), .0),
                    TimeSeriesEntry(LocalDate.of(2005, 2, 2), .1),
                    TimeSeriesEntry(LocalDate.of(2010, 2, 2), .0),
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
            Pair("a", LocalDate.of(2000, 2, 2)),
            Pair("b", LocalDate.of(2000, 3, 2)),
            Pair("c", LocalDate.of(1999, 2, 2)),
        )[rate.label]

        assertNotNull(rate.timeSeries.getAdjacentBefore(LocalDate.of(2001, 2, 2)))
        assertTrue(rate.timeSeries.getAdjacentBefore(LocalDate.of(2001, 2, 2))!!.date.isEqual(expected))
    }

    //TODO Test for adjacent after

    //TODO Test for adjacent null - before & after
}