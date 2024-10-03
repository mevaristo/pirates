package org.mevaristo.app.domain.inbound

import java.time.LocalDate

interface RateUseCase {

}

data class RateDto(val label: String, val timeSeries: List<TimeSeriesEntryDto>)

data class TimeSeriesEntryDto(val localDate: LocalDate, val value: Double)