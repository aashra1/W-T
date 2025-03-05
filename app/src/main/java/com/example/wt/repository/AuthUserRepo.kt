package com.example.wt.repository

interface AuthUserRepo {
    fun signup(email:String, password:String, callback: (Boolean, String?) -> Unit)
}