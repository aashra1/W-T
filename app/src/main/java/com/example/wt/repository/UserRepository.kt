package com.example.wt.repository

import com.example.wt.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    fun signup(email:String,password:String,callback:(Boolean,String,String)->Unit)
    fun getCurrentUser(): FirebaseUser?
    fun addUserToDB(userid:String,model:UserModel,callback: (Boolean, String) -> Unit)
}