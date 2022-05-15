package com.marionete.services

data class UserAccountInfoRequest(val username: String, val password: String)
data class UserAccountInfoDto(val userInfoDto: UserInfoDto, val accountInfo: AccountInfoDto)
data class UserInfoDto(val name:String, val surname: String, val sex: String, val age: Int)
data class AccountInfoDto(val accountNumber: String)
