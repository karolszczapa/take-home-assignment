package com.marionete.login_service

import org.junit.jupiter.api.Test
import services.LoginRequest

class LoginServiceTest {
    @Test
    fun login() {
        //given
        LoginService.start()
        val loginRequest = LoginRequest.newBuilder().setUsername("username").setPassword("password").build();
        //when
        val response = LoginClient("localhost:8897").downloadToken(loginRequest).block()
        assert(response.token != null)
    }
}