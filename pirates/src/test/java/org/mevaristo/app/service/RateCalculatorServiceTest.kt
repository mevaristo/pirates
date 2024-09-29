package org.mevaristo.app.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mevaristo.app.model.PricePoint
import org.mevaristo.app.model.Rate
import java.time.LocalDate
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RateCalculatorServiceTest {
    lateinit var rateCalculatorService: RateCalculatorService

    companion object {
        @JvmStatic
        fun priceProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Rate("a(same date)", listOf(
                    PricePoint(1.0, LocalDate.of(2000, 1, 1)),
                    PricePoint(2.0, LocalDate.of(2001, 1, 1)),
                    PricePoint(3.0, LocalDate.of(2002, 1, 1)),
                )),
                Rate("b(same date)", listOf(
                    PricePoint(.5, LocalDate.of(2000, 1, 1)),
                    PricePoint(.6, LocalDate.of(2001, 1, 1)),
                    PricePoint(.7, LocalDate.of(2002, 1, 1)),
                )),
                listOf<PricePoint>(
                    PricePoint(1.0/.5, LocalDate.of(2000, 1, 1)),
                    PricePoint(2.0/.6, LocalDate.of(2001, 1, 1)),
                    PricePoint(3.0/.7, LocalDate.of(2002, 1, 1)),
                )),
            Arguments.of(
                Rate("a(between)", listOf(
                    PricePoint(1.0, LocalDate.of(2000, 1, 1)),
                    PricePoint(3.0, LocalDate.of(2002, 1, 1)),
                )),
                Rate("b(surrounding)", listOf(
                    PricePoint(.5, LocalDate.of(1999, 1, 1)),
                    PricePoint(.6, LocalDate.of(2001, 1, 1)),
                    PricePoint(.7, LocalDate.of(2003, 1, 1)),
                    PricePoint(.7, LocalDate.of(2005, 1, 1)),
                )),
                listOf<PricePoint>(
                    PricePoint(1.0/0.5, LocalDate.of(2000, 1, 1)),
                    PricePoint(3.0/0.6, LocalDate.of(2002, 1, 1)),
                )),
            Arguments.of(
                Rate("a(before-after gap)", listOf(
                    PricePoint(1.0, LocalDate.of(2000, 1, 1)),
                    PricePoint(2.0, LocalDate.of(2001, 1, 1)),
                    PricePoint(3.0, LocalDate.of(2002, 1, 1)),
                    PricePoint(4.0, LocalDate.of(2003, 1, 1)),
                )),
                Rate("b(before-after gap)", listOf(
                    PricePoint(0.5, LocalDate.of(2001, 1, 1)),
                    PricePoint(0.6, LocalDate.of(2002, 6, 1)),
                )),
                listOf<PricePoint>(
                    PricePoint(1.0, LocalDate.of(2000, 1, 1)),
                    PricePoint(2.0/0.5, LocalDate.of(2001, 1, 1)),
                    PricePoint(3.0/0.5, LocalDate.of(2002, 1, 1)),
                    PricePoint(4.0/0.6, LocalDate.of(2003, 1, 1)),
                )),
            Arguments.of(
                Rate("a(only)", listOf(
                    PricePoint(1.0, LocalDate.of(2000, 12, 1)),
                    PricePoint(1.0, LocalDate.of(2005, 6, 1)),
                    PricePoint(1.0, LocalDate.of(2010, 12, 1)),
                )),
                Rate("b(only)", listOf(
                    PricePoint(0.5, LocalDate.of(2005, 6, 1)),
                )),
                listOf<PricePoint>(
                    PricePoint(1.0, LocalDate.of(2000, 12, 1)),
                    PricePoint(1.0/.5, LocalDate.of(2005, 6, 1)),
                    PricePoint(1.0/.5, LocalDate.of(2010, 12, 1)),
                )),
        )
    }

    @BeforeEach
    fun setup() {
        rateCalculatorService = RateCalculatorService()
    }

    @Test
    fun `Rated subject needs to have the same amount of price points as its calculated rate`() {
        val orange = Rate("Orange", listOf(
            PricePoint(0.2, LocalDate.of(2000, 12, 1)),
            PricePoint(0.3, LocalDate.of(2001, 6, 1)),
            PricePoint(0.4, LocalDate.of(2001, 12, 1)),
            PricePoint(0.5, LocalDate.of(2002, 6, 1)),
            PricePoint(0.6, LocalDate.of(2002, 12, 1))
        ))

        val minimumWage = Rate("Minimum Wage", listOf(
            PricePoint(1.0, LocalDate.of(2000, 6, 1)),
            PricePoint(1.2, LocalDate.of(2001, 6, 1)),
            PricePoint(1.4, LocalDate.of(2002, 6, 1))
        ))

        val ratedOrangeAgaintMinimumWage = rateCalculatorService
            .calculate(subject = orange, rateAgainst = minimumWage, null)

        assertEquals(orange.pricePoints.size, ratedOrangeAgaintMinimumWage.pricePoints.size)
    }

    @ParameterizedTest
    @MethodSource("priceProvider")
    fun `Prices should be correctly rated`(subject: Rate, rateAgainst: Rate, expectedRates: List<PricePoint>) {
        val ratedSubject = rateCalculatorService.calculate(subject, rateAgainst, null)

        ratedSubject.pricePoints.forEach { pricePoint ->
            assertTrue(pricePoint.date.isEqual(expectedRates[ratedSubject.pricePoints.indexOf(pricePoint)].date))
            assertEquals(expectedRates[ratedSubject.pricePoints.indexOf(pricePoint)].price, pricePoint.price)
        }
    }

}