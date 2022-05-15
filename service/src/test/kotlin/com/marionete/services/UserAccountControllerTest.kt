package com.marionete.services

import com.beust.klaxon.Klaxon
import org.junit.jupiter.api.BeforeAll
import com.marionete.backends.AccountInfoMock
import com.marionete.backends.UserInfoMock
import com.marionete.infrastructure.controllers.UserAccountController
import com.marionete.login_service.LoginService
import com.twitter.finagle.Http
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Response
import com.twitter.util.Await
import org.junit.jupiter.api.Test
import scala.runtime.BoxedUnit
import kotlin.test.assertEquals

class UserAccountControllerTest {
    companion object {
        @BeforeAll
        @JvmStatic
        internal fun setup() {
            AccountInfoMock.start()
            UserInfoMock.start()
            LoginService.start()
            UserAccountController.start()
        }
    }

    @Test
    fun UserAccountServiceTest (){
        //given
        val service = Http.client().newService("localhost:8900")
        val request = Request.apply(Method.Post(), "/marionete/useraccount")
        request.contentString = Klaxon().toJsonString(UserAccountInfoRequest("bla", "foo"))
        //when
        val responseFuture = service.apply(request)

        //then
        val response = responseFuture.toCompletableFuture<Response>().get()
        val userAccountInfoDto = Klaxon().parse<UserAccountInfoDto>(response.contentString)!!

        assertEquals("12345-3346-3335-4456", userAccountInfoDto.accountInfo.accountNumber)
        assertEquals("John", userAccountInfoDto.userInfoDto.name)
        assertEquals("Doe", userAccountInfoDto.userInfoDto.surname)
        assertEquals("male", userAccountInfoDto.userInfoDto.sex)
        assertEquals(32, userAccountInfoDto.userInfoDto.age)

    }
}