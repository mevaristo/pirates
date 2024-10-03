package org.mevaristo.app.api

import org.springframework.stereotype.Controller

@Controller
class RateRestController {
    fun home(): String {
        return "Welcome to pirates API"
    }
}