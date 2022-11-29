package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthentication() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/auth-basic' path"
            validate { credentials ->
                if (credentials.name == "jetbrains" && credentials.password == "foobar") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
	}

    routing {
        authenticate("auth-basic") {
            get("/auth-basic") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }
}
