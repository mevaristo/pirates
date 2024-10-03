package org.mevaristo.app.domain.model

import java.time.LocalDate
import java.util.TreeMap
import java.util.stream.Stream

/**
 * Data class representing a generic subject with temporal values, like a currency, a product
 * or other valued references.
 */
data class Rate(val label: String, var timeSeries: TimeSeries) {
    /**
     * Class representing a data structure to hold time series data ordered by date. Aims to offer
     * auxiliary search, transformation and other utility methods.
     */
    class TimeSeries(): TreeMap<LocalDate, Double>(comparator) {
        companion object {
            private val comparator = SortByDateComparator()

            fun of(entries: List<TimeSeriesEntry>): TimeSeries {
                return of(entries.stream())
            }

            fun of(entries: Stream<TimeSeriesEntry>): TimeSeries {
                val timeSeries = TimeSeries()
                entries.forEach() { entry ->
                    timeSeries.put(entry)
                }

                return timeSeries
            }
        }

        fun getAdjacentBefore(localDate: LocalDate): TimeSeriesEntry? {
            return this.getAsEntry(
                this.keys.stream().filter { thisDate ->
                    thisDate < localDate
                }.toList().lastOrNull())
        }

        fun getAdjacentAfter(localDate: LocalDate): TimeSeriesEntry? {
            return this.getAsEntry(
                this.keys.stream().filter { thisDate ->
                    thisDate > localDate
                }.toList().firstOrNull())
        }

        fun getAsEntry(localDate: LocalDate?): TimeSeriesEntry? {
            val value = this[localDate]

            if (value == null || localDate == null) {
                return null
            }

            return TimeSeriesEntry(localDate, value)
        }

        fun put(timeSeriesEntry: TimeSeriesEntry) {
            this.put(timeSeriesEntry.date, timeSeriesEntry.value)
        }

        fun stream(): Stream<TimeSeriesEntry> {
            return this.keys.stream().map { key ->
                getAsEntry(key)
            }
        }

        /**
         * Data class representing a value relative to a point in time
         */
        data class TimeSeriesEntry(val date: LocalDate, val value: Double)
    }

    private class SortByDateComparator: Comparator<LocalDate> {
        override fun compare(p0: LocalDate, p1: LocalDate): Int {
            return p0.compareTo(p1)
        }
    }
}
