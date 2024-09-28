package org.mevaristo.app.model

import java.time.LocalDate

data class PricePoint(val price: Double, val date: LocalDate)

data class Rate(val name: String, var pricePoints: List<PricePoint>)