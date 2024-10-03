package org.mevaristo.app.domain.inbound

import java.time.LocalDateTime

interface RateUseCase {

}

data class RateDto(val label: String, val timeSeries: List<TimeSeriesEntryDto>) {
    data class TimeSeriesEntryDto(val localDateTime: LocalDateTime, val value: Double)
}
