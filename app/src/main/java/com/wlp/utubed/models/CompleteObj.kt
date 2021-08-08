package com.wlp.utubed.model

object CompleteObj {
    var esitoRegisterUser :Boolean = false
    var esitoLoginUser :Boolean = false
    var esitoAddUser :Boolean = false

    fun reset()
    {
        esitoRegisterUser  = false
        esitoLoginUser = false
        esitoAddUser = false
    }
}