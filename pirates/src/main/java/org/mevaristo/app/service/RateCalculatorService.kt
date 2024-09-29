package org.mevaristo.app.service

import org.mevaristo.app.model.PricePoint
import org.mevaristo.app.model.Rate
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

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