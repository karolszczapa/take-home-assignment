package com.marionete.services

import com.beust.klaxon.Klaxon
import com.marionete.login_service.LoginClient
import com.twitter.finagle.Http
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.Request
import com.twitter.util.Future
import com.twitter.util.Promise
import services.LoginRequest
import java.util.concurrent.CompletableFuture

class UserAccountService() {
    private val accountInfoDest = Http.client().newService("localhost:8899")
    private val userInfoDest = Http.client().newService("localhost:8898")
    private val loginClient = LoginClient("localhost:8897")

    fun downloadUserAccountInfo(userAccountInfoRequest: UserAccountInfoRequest): Future<UserAccountInfoDto> {
        val tokenCompletableFuture =
            loginClient.downloadToken(map(userAccountInfoRequest))
                .toFuture()
                .toTwitterFunction()

        return tokenCompletableFuture
            .flatMap{ response ->
                val token = response.token
                val userRequest = Request.apply(Method.Get(), "/marionete/user/")
                userRequest.`authorization_$eq`("Bearer: $token")
                val userInfoFuture = userInfoDest.apply(userRequest)
                    .map { Klaxon().parse<UserInfoDto>(it.contentString) }

                val accountRequest = Request.apply(Method.Get(), "/marionete/account/")
                accountRequest.`authorization_$eq`("Bearer: $token")
                val accountInfoFuture = accountInfoDest.apply(accountRequest)
                    .map { Klaxon().parse<AccountInfoDto>(it.contentString) }

                com.twitter.util.Futures.join(userInfoFuture, accountInfoFuture)
                    .map { it -> UserAccountInfoDto(it._1!!, it._2!!) }
            }
    }

    private fun <T> CompletableFuture<T>.toTwitterFunction(): Future<T> {
        val promise = Promise<T>()
        this.whenComplete { value, ex ->
            if (ex == null) {
                promise.setValue(value)
            } else {
                promise.setException(ex)
            }
        }
        return promise
    }
    private fun map(request: UserAccountInfoRequest): LoginRequest {
        return LoginRequest.newBuilder().setUsername(request.username).setPassword(request.password).build()
    }
}