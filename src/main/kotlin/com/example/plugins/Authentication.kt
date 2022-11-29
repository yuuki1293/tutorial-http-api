package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Application.configureAuthentication() {
    val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }
    val hashedUserTable = UserHashedTableAuth(
        table = mapOf(
            "jetbrains" to digestFunction("foobar"),
            "admin" to digestFunction("password")
        ),
        digester = digestFunction
    )
    install(Authentication) {
        basic("auth-basic-hashed") {
            realm = "Access to the '/auth-basic' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }
    }

    routing {
        authenticate("auth-basic-hashed") {
            get("/auth-basic") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }
}
