package com.example.wt.repository

import com.google.firebase.auth.FirebaseAuth

class UserAuthRepoImpl(var auth: FirebaseAuth): AuthUserRepo {
    override fun signup(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
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
}