package com.example

import com.example.repository.*
import com.example.service.AuthService
import com.example.service.UserSession
import com.example.service.getUserInfo
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

fun Application.configureGoogleOAuth() {
    val httpClient by inject<HttpClient>()

    // 安裝 session，用以存取使用者資訊 (token, state等)
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.maxAgeInSeconds = 60 * 60 * 24 // 1天，依需求調整
        }
    }

    // 建立一個用來儲存 state -> redirectUrl 的暫存 map
    // 若要更安全/持久，可考慮放進 DB 或其他資料結構
    val redirects = mutableMapOf<String, String>()

    // 安裝 Authentication，並註冊 OAuth Provider
//    install(Authentication) {
//        oauth("auth-oauth-google") {
//            // 指定認證流程完成後要回到哪個路由
//            urlProvider = { "http://localhost:8080/callback" }
//
//            // 由我們動態提供 Google OAuth 所需參數
//            providerLookup = {
//                OAuthServerSettings.OAuth2ServerSettings(
//                    name = "google",
//                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
//                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
//                    requestMethod = HttpMethod.Post,
//                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
//                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
//                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
//                    // 假如你想要離線模式 (refresh_token)，可加上 access_type=offline
//                    extraAuthParameters = listOf("access_type" to "offline"),
//                    onStateCreated = { call, state ->
//                        // 從查詢參數擷取 redirectUrl，並暫存起來
//                        call.request.queryParameters["redirectUrl"]?.let {
//                            redirects[state] = it
//                        }
//                    }
//                )
//            }
//
//            // 指定 HttpClient
//            client = httpClient
//        }
//    }

    // 註冊 routing
    routing {
        // Step 3: 提供一個 /login 路由，Ktor 會自動幫你導向 Google 的 authorizeUrl
        authenticate("auth-oauth-google") {
            get("/login") {
                // 只要進入這個路由，便會自動跳轉到 Google OAuth Login
                // 不需要在這裡做任何事
            }

            // Step 4: Google OAuth 完成後的 callback route
            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                principal?.let { oauthToken ->
                    // 取得 state & token
                    oauthToken.state?.let { state ->
                        // 儲存到 session
                        call.sessions.set(UserSession(state, oauthToken.accessToken))

                        val userSession: UserSession? = call.sessions.get<UserSession>()
                        val userInfo = userSession?.let { getUserInfo(httpClient, it.token) }
                        println("userInfo:$userInfo")

                        // 轉向原先想去的地方，或預設 /home
                        redirects[state]?.let { redirectUrl ->
                            call.respondRedirect(redirectUrl)
                            return@get
                        }
                    }
                }
                // 如果沒有對應的 redirect 或 state 就跳回 /home
                call.respondRedirect("/home")
            }
        }

        get("/userinfo") {
            // 從 session 中取出 token
            val userSession: UserSession? = call.sessions.get<UserSession>()
            if (userSession == null) {
                call.respond(HttpStatusCode.Unauthorized, "尚未登入，請先走 /login")
                return@get
            }

            val userInfo = getUserInfo(httpClient, userSession.token)
            println("userInfo:$userInfo")
            if (userInfo.name == null) {
                call.respond(HttpStatusCode.BadRequest, "無法取得使用者資訊")
            } else {
                call.respond(mapOf("message" to "Hello, ${userInfo.name}!", "picture" to userInfo.picture))
            }
        }
    }
}