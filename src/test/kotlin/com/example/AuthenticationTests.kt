package com.example

import com.example.plugins.configureAuthentication
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class AuthenticationTests {
    @Test
    fun testAuthBasic() = testApplication {
        application {
            configureAuthentication()
        }
        val response = client.get("/auth-basic") {
            url {
                basicAuth("jetbrains", "foobar")
            }
        }
        assertEquals(
            "Hello, jetbrains!",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testAuthForm() = testApplication {
        application {
            configureAuthentication()
        }
        val response = client.submitForm(
            url = "/auth-form",
            formParameters = Parameters.build {
                append("username", "jetbrains")
                append("password", "foobar")
            }
        )
        println(response.status)
        response.headers.forEach { a, b -> b.forEach { println(it + a) } }
        assertEquals(
            "Hello, jetbrains!",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}