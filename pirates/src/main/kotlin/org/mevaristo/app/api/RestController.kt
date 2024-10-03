package org.mevaristo.app.api

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {
    companion object {
        const val SWAGGER_HOME = "/swagger-ui.html"
    }

    @RequestMapping("/", method = [RequestMethod.GET])
    fun rootRedirect(httpServletResponse: HttpServletResponse) {
        httpServletResponse.setHeader("Location", SWAGGER_HOME)
        httpServletResponse.status = 302
    }
}