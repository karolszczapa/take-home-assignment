package com.marionete.login_service

import io.grpc.ManagedChannelBuilder
import reactor.core.publisher.Mono
import services.LoginRequest
import services.LoginResponse
import services.ReactorLoginServiceGrpc

class LoginClient(target: String) {
    private val channel = ManagedChannelBuilder.forTarget(target)
        .usePlaintext()
        .build()
    private val stub = ReactorLoginServiceGrpc.newReactorStub(channel)

    fun downloadToken(loginRequest: LoginRequest): Mono<LoginResponse> = stub.login(loginRequest)

}