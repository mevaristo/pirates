package org.mevaristo.app.domain.port

import org.mevaristo.app.domain.model.Rate

interface RateManagerPort {
    fun getRate(label: String): Rate

    fun saveRate(rate: Rate)

    fun deleteRate(label: String)
}