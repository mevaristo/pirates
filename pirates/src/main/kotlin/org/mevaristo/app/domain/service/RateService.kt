package org.mevaristo.app.domain.service

import org.mevaristo.app.domain.model.TimeSeriesEntry
import org.mevaristo.app.domain.model.Rate
import org.mevaristo.app.domain.model.TimeSeries
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Class representing a service responsible for rating functionality
 */
class RateService {

    /**
     * Method responsible for rating a Rate subject (representing a product, a currency, etc.) against another Rate
     * subject
     *
     * @param subject The rating target, which will be rated against a base rate value list
     * @param rateAgainst The base value to rate another subject against
     * @param label A symbolic name for the rating
     * @return A new Rate object with a list of price points of the same size and dates as the subject price points
     * list, with each of its prices changed according to the base rate prices
     */
    fun rate(subject: Rate, rateAgainst: Rate, label: String?): Rate {
        val ratedSubjectTimeSeries = subject.timeSeries.stream().map { entry ->
            rateTimeSeriesEntry(entry, rateAgainst.timeSeries)
        }

        val rateName = label ?: (subject.label + "-" + rateAgainst.label + " rating")

        return Rate(
            rateName,
            TimeSeries.of(ratedSubjectTimeSeries),
        )
    }

    private fun rateTimeSeriesEntry(timeSeriesEntry: TimeSeriesEntry, rateAgainstTimeSeries: TimeSeries):
            TimeSeriesEntry {
        val entry =
            if (rateAgainstTimeSeries.getAsEntry(timeSeriesEntry.date) != null)
                rateAgainstTimeSeries.getAsEntry(timeSeriesEntry.date)
            else
                rateAgainstTimeSeries.getAdjacentBefore(timeSeriesEntry.date)

        return TimeSeriesEntry(timeSeriesEntry.date, timeSeriesEntry.value /
                (entry?.value ?: 1.0))
    }
}