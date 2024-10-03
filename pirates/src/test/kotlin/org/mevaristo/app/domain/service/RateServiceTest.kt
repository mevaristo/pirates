package org.mevaristo.app.domain.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mevaristo.app.domain.model.Rate
import org.mevaristo.app.domain.model.Rate.TimeSeries.TimeSeriesEntry
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RateServiceTest {
    lateinit var rateService: RateService

    companion object {
        @JvmStatic
        fun valuePointProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Rate("a(same date)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(2000, 1, 1, 0 , 0), 1.0),
                        TimeSeriesEntry(LocalDateTime.of(2001, 1, 1, 0 , 0), 2.0),
                        TimeSeriesEntry(LocalDateTime.of(2002, 1, 1, 0 , 0), 3.0),
                    )
                )),
                Rate("b(same date)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(2000, 1, 1, 0 , 0), .5),
                        TimeSeriesEntry(LocalDateTime.of(2001, 1, 1, 0 , 0), .6),
                        TimeSeriesEntry(LocalDateTime.of(2002, 1, 1, 0 , 0), .7),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDateTime.of(2000, 1, 1, 0 , 0), 1.0/.5),
                    TimeSeriesEntry(LocalDateTime.of(2001, 1, 1, 0 , 0), 2.0/.6),
                    TimeSeriesEntry(LocalDateTime.of(2002, 1, 1, 0 , 0), 3.0/.7),
                )),
            Arguments.of(
                Rate("a(between)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(2000, 1, 1, 0 , 0), 1.0),
                        TimeSeriesEntry(LocalDateTime.of(2002, 1, 1, 0 , 0), 3.0),
                    )
                )),
                Rate("b(surrounding)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(1999, 1, 1, 0 , 0), .5),
                        TimeSeriesEntry(LocalDateTime.of(2001, 1, 1, 0 , 0), .6),
                        TimeSeriesEntry(LocalDateTime.of(2003, 1, 1, 0 , 0), .7),
                        TimeSeriesEntry(LocalDateTime.of(2005, 1, 1, 0 , 0), .7),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDateTime.of(2000, 1, 1, 0 , 0), 1.0/0.5),
                    TimeSeriesEntry(LocalDateTime.of(2002, 1, 1, 0 , 0), 3.0/0.6),
                )),
            Arguments.of(
                Rate("a(before-after gap)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(2000, 1, 1, 0 , 0), 1.0),
                        TimeSeriesEntry(LocalDateTime.of(2001, 1, 1, 0 , 0), 2.0),
                        TimeSeriesEntry(LocalDateTime.of(2002, 1, 1, 0 , 0), 3.0),
                        TimeSeriesEntry(LocalDateTime.of(2003, 1, 1, 0 , 0), 4.0),
                    )
                )),
                Rate("b(before-after gap)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(2001, 1, 1, 0 , 0), 0.5),
                        TimeSeriesEntry(LocalDateTime.of(2002, 6, 1, 0 , 0), 0.6),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDateTime.of(2000, 1, 1, 0 , 0), 1.0),
                    TimeSeriesEntry(LocalDateTime.of(2001, 1, 1, 0 , 0), 2.0/0.5),
                    TimeSeriesEntry(LocalDateTime.of(2002, 1, 1, 0 , 0), 3.0/0.5),
                    TimeSeriesEntry(LocalDateTime.of(2003, 1, 1, 0 , 0), 4.0/0.6),
                )),
            Arguments.of(
                Rate("a(only)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(2000, 12, 1, 0 , 0), 1.0),
                        TimeSeriesEntry(LocalDateTime.of(2005, 6, 1, 0 , 0), 1.0),
                        TimeSeriesEntry(LocalDateTime.of(2010, 12, 1, 0 , 0), 1.0),
                    )
                )),
                Rate("b(only)", Rate.TimeSeries.of(
                    listOf(
                        TimeSeriesEntry(LocalDateTime.of(2005, 6, 1, 0 , 0), 0.5),
                    )
                )),
                listOf<TimeSeriesEntry>(
                    TimeSeriesEntry(LocalDateTime.of(2000, 12, 1, 0 , 0), 1.0),
                    TimeSeriesEntry(LocalDateTime.of(2005, 6, 1, 0 , 0), 1.0/.5),
                    TimeSeriesEntry(LocalDateTime.of(2010, 12, 1, 0 , 0), 1.0/.5),
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
            assertEquals(entry.value, ratedSubject.timeSeries.get(entry.dateTime))
        }
    }

}