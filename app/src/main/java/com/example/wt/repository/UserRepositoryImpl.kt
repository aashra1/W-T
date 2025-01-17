package com.example.wt.repository

import com.example.wt.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserRepositoryImpl:UserRepository {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()

        var ref: DatabaseReference = database.reference
            .child("users")

        var auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun signup(email:String, password:String, callback:(Boolean, String,String)->Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(
                        true, "Registration success",
                        auth.currentUser?.uid.toString()
                    )
                } else {
                    callback(false, it.exception?.message.toString(), "")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun addUserToDB(
        userid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(model.userId).setValue(UserModel).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Datas Added to Database")
            }
            else{
                callback(false,it.exception?.message.toString())
            }

        }
    }
}