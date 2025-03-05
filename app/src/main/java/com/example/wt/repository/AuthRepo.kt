package com.example.wt.repository

interface AuthRepo {
    fun login(email : String, password : String, callback:(Boolean,String) -> Unit)
}