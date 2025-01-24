package com.example.wt.viewModel

import com.example.wt.model.UserModel
import com.example.wt.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo : UserRepository) {

    fun login(email : String, password : String, callback:(Boolean,String) -> Unit){
        repo.login(email,password,callback)
    }

    fun signup(email:String,password:String,
               callback: (Boolean, String,String) -> Unit){
        repo.signup(email, password, callback)
    }

    fun getCurrentUser(): FirebaseUser?{
        return repo.getCurrentUser()
    }

    fun addUserToDB(userId:String, userModel: UserModel, callback: (Boolean, String) -> Unit){
        return repo.addUserToDB(userId,userModel,callback)
    }
}