package org.mevaristo.app.domain.inbound

import org.mevaristo.app.domain.model.Rate
import org.mevaristo.app.domain.model.Rate.TimeSeries.TimeSeriesEntry

fun rateDtoToDomainMapper(rateDto: RateDto): Rate {
    val timeSeriesEntries = rateDto.timeSeries.stream().map { it ->
        TimeSeriesEntry(it.localDateTime, it.value)
    }

    return Rate(rateDto.label, Rate.TimeSeries.of(timeSeriesEntries))
}

fun rateDomainToDtoMapper(rate: Rate): RateDto {
    val timeSeriesEntries = rate.timeSeries.stream().map { it ->
        RateDto.TimeSeriesEntryDto(it.dateTime, it.value)
    }

    return RateDto(rate.label, timeSeriesEntries.toList())
}