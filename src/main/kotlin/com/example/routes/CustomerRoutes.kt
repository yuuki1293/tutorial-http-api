package com.example.routes

import io.ktor.server.routing.*

fun Route.customerRouting() {
    route("/customer") {
        get {

        }
        get("{id?}"){

        }
        post {

        }
        delete("{id?}") {

        }
    }
}