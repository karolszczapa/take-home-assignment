package com.marionete.infrastructure.controllers

import com.beust.klaxon.Klaxon
import com.marionete.services.UserAccountInfoRequest
import com.marionete.services.UserAccountService
import com.twitter.finagle.Http
import com.twitter.finagle.ListeningServer
import com.twitter.finagle.Service
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Response
import com.twitter.finagle.http.Status
import com.twitter.util.Future

object UserAccountController : Service<Request, Response>() {
    private val userAccountService = UserAccountService()

    override fun apply(req: Request): Future<Response> {
        println("request received $req")
        return when (req.path()) {
            "/marionete/useraccount" -> {
                when (req.method()) {
                    Method.Post() -> {
                        val reqObject = Klaxon().parse<UserAccountInfoRequest>(req.contentString)!!
                        userAccountService.downloadUserAccountInfo(reqObject)
                            .map {
                                val response = Response.apply(req.version(), Status.Ok())
                                response.contentString = Klaxon().toJsonString(it)
                                response
                            }
                    }
                    else -> Future.value(Response.apply(Status.NotFound()))
                }
            }
            else -> Future.value(Response.apply(Status.NotFound()))
        }
    }

    fun start(): ListeningServer {
        val port = "8900"
        println("Starting UserAccountController service in port $port")
        return Http.serve(":$port", UserAccountController)
    }
}