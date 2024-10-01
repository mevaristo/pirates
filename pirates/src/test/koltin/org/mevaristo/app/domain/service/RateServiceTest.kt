package org.mevaristo.app.domain.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mevaristo.app.domain.model.Rate
import org.mevaristo.app.domain.model.TimeSeries
import org.mevaristo.app.domain.model.TimeSeriesEntry
import java.time.LocalDate
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RateServiceTest {
    lateinit var rateService: RateService

    companion object {
        @JvmStatic
        fun valuePointProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Rate("a(same date)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(2000, 1, 1), 1.0),
                        TimeSeriesEntry(LocalDate.of(2001, 1, 1), 2.0),
                        TimeSeriesEntry(LocalDate.of(2002, 1, 1), 3.0),
                    )
                )),
                Rate("b(same date)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(2000, 1, 1), .5),
                        TimeSeriesEntry(LocalDate.of(2001, 1, 1), .6),
                        TimeSeriesEntry(LocalDate.of(2002, 1, 1), .7),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDate.of(2000, 1, 1), 1.0/.5),
                    TimeSeriesEntry(LocalDate.of(2001, 1, 1), 2.0/.6),
                    TimeSeriesEntry(LocalDate.of(2002, 1, 1), 3.0/.7),
                )),
            Arguments.of(
                Rate("a(between)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(2000, 1, 1), 1.0),
                        TimeSeriesEntry(LocalDate.of(2002, 1, 1), 3.0),
                    )
                )),
                Rate("b(surrounding)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(1999, 1, 1), .5),
                        TimeSeriesEntry(LocalDate.of(2001, 1, 1), .6),
                        TimeSeriesEntry(LocalDate.of(2003, 1, 1), .7),
                        TimeSeriesEntry(LocalDate.of(2005, 1, 1), .7),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDate.of(2000, 1, 1), 1.0/0.5),
                    TimeSeriesEntry(LocalDate.of(2002, 1, 1), 3.0/0.6),
                )),
            Arguments.of(
                Rate("a(before-after gap)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(2000, 1, 1), 1.0),
                        TimeSeriesEntry(LocalDate.of(2001, 1, 1), 2.0),
                        TimeSeriesEntry(LocalDate.of(2002, 1, 1), 3.0),
                        TimeSeriesEntry(LocalDate.of(2003, 1, 1), 4.0),
                    )
                )),
                Rate("b(before-after gap)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(2001, 1, 1), 0.5),
                        TimeSeriesEntry(LocalDate.of(2002, 6, 1), 0.6),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDate.of(2000, 1, 1), 1.0),
                    TimeSeriesEntry(LocalDate.of(2001, 1, 1), 2.0/0.5),
                    TimeSeriesEntry(LocalDate.of(2002, 1, 1), 3.0/0.5),
                    TimeSeriesEntry(LocalDate.of(2003, 1, 1), 4.0/0.6),
                )),
            Arguments.of(
                Rate("a(only)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(2000, 12, 1), 1.0),
                        TimeSeriesEntry(LocalDate.of(2005, 6, 1), 1.0),
                        TimeSeriesEntry(LocalDate.of(2010, 12, 1), 1.0),
                    )
                )),
                Rate("b(only)", TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDate.of(2005, 6, 1), 0.5),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDate.of(2000, 12, 1), 1.0),
                    TimeSeriesEntry(LocalDate.of(2005, 6, 1), 1.0/.5),
                    TimeSeriesEntry(LocalDate.of(2010, 12, 1), 1.0/.5),
                )),
        )
    }

    @BeforeEach
    fun setup() {
        rateService = RateService()
    }

    @ParameterizedTest
    @MethodSource("valuePointProvider")
    fun `Resulting rate needs to have the same points in time as its rated subject`(
        subject: Rate, rateAgainst: Rate) {

        val ratedResult = rateService
            .rate(subject = subject, rateAgainst = rateAgainst, null)

        assertEquals(subject.timeSeries.size, ratedResult.timeSeries.size)
        ratedResult.timeSeries.forEach { timedValue ->
            assertTrue(subject.timeSeries.containsKey(timedValue.key))
        }
    }

    @ParameterizedTest
    @MethodSource("valuePointProvider")
    fun `Values should be correctly rated`(subject: Rate, rateAgainst: Rate, expectedRates: List<TimeSeriesEntry>) {
        val ratedSubject = rateService.rate(subject, rateAgainst, null)

        expectedRates.stream().forEach { entry ->
            assertEquals(entry.value, ratedSubject.timeSeries.get(entry.date))
        }
    }

}