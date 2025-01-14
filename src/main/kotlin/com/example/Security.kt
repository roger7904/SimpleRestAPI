package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val httpClient by inject<HttpClient>()
    val redirects = mutableMapOf<String, String>()

    install(Authentication) {
        jwt("auth-jwt") {
            val secret = "Roger-TEST-secret"
            val issuer = "ktor.io"
            val audience = "ktorAudience"
            val myRealm = "Access to 'protected' routes"

            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }

        oauth("auth-oauth-google") {
            // 指定認證流程完成後要回到哪個路由
            urlProvider = { "http://localhost:8080/callback" }

            // 由我們動態提供 Google OAuth 所需參數
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                    // 假如你想要離線模式 (refresh_token)，可加上 access_type=offline
                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        // 從查詢參數擷取 redirectUrl，並暫存起來
                        call.request.queryParameters["redirectUrl"]?.let {
                            redirects[state] = it
                        }
                    }
                )
            }

            // 指定 HttpClient
            client = httpClient
        }

//        oauth("auth-oauth-google") {
//            urlProvider = { "http://localhost:8080/callback" }
//            providerLookup = {
//                OAuthServerSettings.OAuth2ServerSettings(
//                    name = "google",
//                    authorizeUrl = "https://accounts.google.com/o/oauth2/v2/auth",
//                    accessTokenUrl = "https://oauth2.googleapis.com/token",
//                    clientId = System.getenv("GOOGLE_CLIENT_ID") ?: "your-google-client-id",
//                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET") ?: "your-google-client-secret",
//                    defaultScopes = listOf("profile", "email")
//                )
//            }
//            client = HttpClient(Apache)
//        }
    }
}