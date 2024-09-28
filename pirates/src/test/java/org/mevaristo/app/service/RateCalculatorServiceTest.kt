package org.mevaristo.app.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mevaristo.app.model.PricePoint
import org.mevaristo.app.model.Rate
import java.time.LocalDate
import kotlin.test.assertEquals

class RateCalculatorServiceTest {
    lateinit var rateCalculatorService: RateCalculatorService

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

        val ratedOrangeAgaintMinimumWage = rateCalculatorService.calculate(subject = orange, rateAgainst = minimumWage)

        assertEquals(orange.pricePoints.size, ratedOrangeAgaintMinimumWage.pricePoints.size)
    }

    @Test
    fun `Rated price should be in the correct ratio when there is only one price point to rate against`() {
        val orange = Rate("Orange", listOf(
            PricePoint(1.0, LocalDate.of(2000, 12, 1)),
            PricePoint(1.0, LocalDate.of(2005, 6, 1)),
            PricePoint(1.0, LocalDate.of(2010, 12, 1)),
        ))

        val minimumWage = Rate("Minimum Wage", listOf(
            PricePoint(0.5, LocalDate.of(2005, 6, 1))
        ))

        val ratedOrangeAgaintMinimumWage = rateCalculatorService.calculate(subject = orange, rateAgainst = minimumWage)

        ratedOrangeAgaintMinimumWage.pricePoints.forEach { pricePoint -> assertEquals(2.0, pricePoint.price) }
    }
}