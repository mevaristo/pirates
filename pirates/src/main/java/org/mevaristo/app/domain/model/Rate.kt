package org.mevaristo.app.domain.model

import java.time.LocalDate

data class PricePoint(val price: Double, val date: LocalDate)

data class Rate(val name: String, var pricePoints: List<PricePoint>)