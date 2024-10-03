package org.mevaristo.app.domain.model

import java.time.LocalDateTime
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
    class TimeSeries(): TreeMap<LocalDateTime, Double>(comparator) {
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

        fun getAdjacentBefore(dateTime: LocalDateTime): TimeSeriesEntry? {
            return this.getAsEntry(
                this.keys.stream().filter { it ->
                    it < dateTime
                }.toList().lastOrNull())
        }

        fun getAdjacentAfter(dateTime: LocalDateTime): TimeSeriesEntry? {
            return this.getAsEntry(
                this.keys.stream().filter { it ->
                    it > dateTime
                }.toList().firstOrNull())
        }

        fun getAsEntry(dateTime: LocalDateTime?): TimeSeriesEntry? {
            val value = this[dateTime]

            if (value == null || dateTime == null) {
                return null
            }

            return TimeSeriesEntry(dateTime, value)
        }

        fun put(timeSeriesEntry: TimeSeriesEntry) {
            this.put(timeSeriesEntry.dateTime, timeSeriesEntry.value)
        }

        fun stream(): Stream<TimeSeriesEntry> {
            return this.keys.stream().map { key ->
                getAsEntry(key)
            }
        }

        /**
         * Data class representing a value relative to a point in time
         */
        data class TimeSeriesEntry(val dateTime: LocalDateTime, val value: Double)
    }

    private class SortByDateComparator: Comparator<LocalDateTime> {
        override fun compare(p0: LocalDateTime, p1: LocalDateTime): Int {
            return p0.compareTo(p1)
        }
    }
}
