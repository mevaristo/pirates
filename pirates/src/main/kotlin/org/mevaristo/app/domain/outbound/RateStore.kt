package org.mevaristo.app.domain.outbound

import org.mevaristo.app.domain.model.Rate

interface RateStore {
    fun getRate(label: String): Rate

    fun saveRate(rate: Rate)

    fun deleteRate(label: String)
}