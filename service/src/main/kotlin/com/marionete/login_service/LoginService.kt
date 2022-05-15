package com.marionete.login_service

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import services.LoginRequest
import services.LoginResponse
import services.LoginServiceGrpc
import java.util.*

object LoginService : LoginServiceGrpc.LoginServiceImplBase() {
    override fun login(request: LoginRequest, responseObserver: StreamObserver<LoginResponse>) {
        responseObserver.onNext(alwaysAuthorize())
        responseObserver.onCompleted()
    }
    private fun alwaysAuthorize(): LoginResponse = LoginResponse.newBuilder().setToken(getRandomToken()).build()
    private fun getRandomToken(): String = UUID.randomUUID().toString()

    fun start() {
        val port = 8897
        val server = ServerBuilder.forPort(port)
            .addService(this)
            .build()
        println("Starting LoginService on port $port")
        server.start()
    }

}