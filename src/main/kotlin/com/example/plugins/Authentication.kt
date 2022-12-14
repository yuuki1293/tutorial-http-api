package com.example.plugins

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.text.Charsets.UTF_8

fun Application.configureAuthentication() {
    // region basic-hashed
    val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }
    val hashedUserTable = UserHashedTableAuth(
        table = mapOf(
            "jetbrains" to digestFunction("foobar"),
            "admin" to digestFunction("password")
        ),
        digester = digestFunction
    )
    // endregion

    // region digest
    fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

    val myRealm = "Access to the '/auth-digest' path"
    val userTable: Map<String, ByteArray> = mapOf(
        "jetbrains" to getMd5Digest("jetbrains:$myRealm:foobar"),
        "admin" to getMd5Digest("admin:$myRealm:password")
    )
    // endregion

    install(Authentication) {
        basic("auth-basic-hashed") {
            realm = "Access to the '/auth-basic' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }

        digest("auth-digest") {
            realm = myRealm
            digestProvider { userName, realm ->
                userTable[userName]
            }
        }

        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
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
        authenticate("auth-basic-hashed") {
            get("/auth-basic") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
        authenticate("auth-digest") {
            get("/auth-digest") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
        authenticate("auth-form") {
            post("/auth-form") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }
}
