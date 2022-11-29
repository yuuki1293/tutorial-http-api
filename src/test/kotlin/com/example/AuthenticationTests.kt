package com.example

import com.example.plugins.configureAuthentication
import io.ktor.client.request.*
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
        println(response.request.url)
        assertEquals(
            "Hello, jetbrains!",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}