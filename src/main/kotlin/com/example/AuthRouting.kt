package com.example

import com.example.repository.*
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureAuthRouting() {
    val authService by inject<AuthService>()

    routing {
        post("/register") {
            val params = call.receive<RegisterRequest>()
            try {
                val newUser = authService.register(params.username, params.password, params.role)
                val response = RegisterResponse(
                    id = newUser.id,
                    username = newUser.username,
                    role = newUser.role
                )
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

//        post("/login") {
//            val loginRequest = call.receive<LoginRequest>()
//            val secret = "Roger-TEST-secret"  // TODO move to .env
//            val token = authService.login(loginRequest.username, loginRequest.password, secret)
//            if (token == null) {
//                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
//            } else {
//                call.respond(LoginResponse(token))
//            }
//        }
    }

    routing {
        authenticate("auth-jwt") {
            get("/role-check") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()

                when (role) {
                    "admin" -> {
                        call.respond(
                            mapOf(
                                "message" to "Hi, Admin! 這裡是只有管理員能看到的資訊。"
                            )
                        )
                    }
                    "user" -> {
                        call.respond(
                            mapOf(
                                "message" to "Hello, User! 這裡是一般使用者能看到的內容。"
                            )
                        )
                    }
                    else -> {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                    }
                }
            }
        }

//        authenticate("auth-oauth-google") {
//            // (1) 進入 OAuth 流程
//            get("/login-oauth-google") {
//                // 只要呼叫此路徑，就會自動轉址到 Google 的授權頁
//                // 若成功，Google 最後會帶著 code 轉址到 /callback
//            }
//
//            // (2) Google OAuth callback
//            get("/callback") {
//                val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
//                if (principal != null) {
//                    // principal.accessToken 就是 Google 回傳的 access_token
//                    // principal.extraParameters 可能包含 refresh_token、id_token 等 (若 scope 與流程允許)
//
//                    val accessToken = principal.accessToken
//                    val tokenType = principal.tokenType
//                    val expiresIn = principal.expiresIn
//                    val refreshToken = principal.refreshToken
//                    val idToken = principal.extraParameters["id_token"] // Google 可能回傳 ID Token
//
//                    // 你可以選擇用 Google API 拿 userinfo
//                    // (舉例) call Google "https://www.googleapis.com/oauth2/v3/userinfo"
//                    // 取得使用者資訊，然後決定要不要在本地 DB 建立使用者。
//                    val userInfo = fetchGoogleUserInfo(accessToken)  // 自行撰寫
//
//                    // 在這邊可以：
//                    // 1. 建立 (或更新) 本地使用者資料
//                    // 2. 產生 JWT Token（或 session）給使用者
//                    val localJWT = authService.issueLocalJWTForOAuthUser(
//                        email = userInfo.email,
//                        name = userInfo.name
//                    )
//
//                    // 最後回傳 JWT 或重導到前端頁面
//                    call.respondText("OAuth Success! Here's your local JWT token = $localJWT")
//                } else {
//                    call.respond(HttpStatusCode.Unauthorized, "No principal")
//                }
//            }
//        }
    }
}