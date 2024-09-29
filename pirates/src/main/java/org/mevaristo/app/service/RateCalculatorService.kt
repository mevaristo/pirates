package org.mevaristo.app.service

import org.mevaristo.app.model.PricePoint
import org.mevaristo.app.model.Rate
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Class representing a service responsible for rating functionality
 */
class RateCalculatorService {
    /**
     * Method responsible for rating a Rate subject (representing a product, a currency, etc.) against another Rate
     * subject
     *
     * @param subject The rating target, which will be rated against a base rate value list
     * @param rateAgainst The base value to rate another subject against
     * @param name A symbolic name for the rating
     * @return A new Rate object with a list of price points of the same size and dates as the subject price points
     * list, with each of its prices changed according to the base rate prices
     */
    fun calculate(subject: Rate, rateAgainst: Rate, name: String?): Rate {
        val ratedPrices = subject.pricePoints.map {
            pricePoint ->
                calculatePricePoint(pricePoint, rateAgainst)
        }

        val rateName = name ?: (subject.name + "-" + rateAgainst.name + " rating")

        return Rate(
            rateName,
            ratedPrices,
        )
    }

    private fun calculatePricePoint(pricePoint: PricePoint, rateAgainst: Rate): PricePoint {
        var closestMatchBelow: PricePoint = PricePoint(1.0, LocalDate.MIN)
        rateAgainst.pricePoints.map { againstPricePoint ->
            if (pricePoint.date.isEqual(againstPricePoint.date)) {
                return PricePoint(pricePoint.price / againstPricePoint.price, pricePoint.date)
            }

            if (pricePoint.date.isAfter(againstPricePoint.date)) {
                if (abs(ChronoUnit.DAYS.between(pricePoint.date, againstPricePoint.date)) <
                    abs(ChronoUnit.DAYS.between(pricePoint.date, closestMatchBelow.date)))
                closestMatchBelow = againstPricePoint
            }
        }

        return PricePoint(pricePoint.price / closestMatchBelow.price, pricePoint.date)
    }
}