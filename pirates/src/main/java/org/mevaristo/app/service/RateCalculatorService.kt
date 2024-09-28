package org.mevaristo.app.service

import org.mevaristo.app.model.PricePoint
import org.mevaristo.app.model.Rate

class RateCalculatorService {
    fun calculate(subject: Rate, rateAgainst: Rate): Rate {
        val ratedPrices = subject.pricePoints.map {
            pricePoint ->
                calculatePricePoint(pricePoint, rateAgainst)
        }

        return Rate(
            subject.name + "-" + rateAgainst.name + " rating",
            ratedPrices,
        )
    }

    private fun calculatePricePoint(pricePoint: PricePoint, rateAgainst: Rate): PricePoint {
        rateAgainst.pricePoints.map { againstPricePoint ->
            if (againstPricePoint.date.isAfter(pricePoint.date)) {
                return PricePoint(pricePoint.price / againstPricePoint.price, pricePoint.date)
            }
        }

        return PricePoint(pricePoint.price / rateAgainst.pricePoints.last().price, pricePoint.date)
    }
}