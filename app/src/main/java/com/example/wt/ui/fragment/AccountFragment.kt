package com.example.wt.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.wt.R
import com.example.wt.databinding.FragmentAccountBinding
import com.example.wt.login.activity.LoginActivity
import com.example.wt.login.fragment.SignupFragment
import com.example.wt.ui.activity.ContactUsActivity
import com.example.wt.ui.activity.FaqActivity
import com.example.wt.ui.activity.OrderActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountFragment : Fragment() {

    lateinit var binding: FragmentAccountBinding
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        binding.contactBtn.setOnClickListener{
            val intent = Intent(requireActivity(), ContactUsActivity::class.java)
            startActivity(intent)
        }

        binding.faqBtn.setOnClickListener{
            val intent = Intent(requireActivity(), FaqActivity::class.java)
            startActivity(intent)
        }

        binding.ordersBtn.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val intent = Intent(requireActivity(), OrderActivity::class.java).apply {
                    putExtra("userId", currentUser.uid)
                }
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please log in to view orders", Toast.LENGTH_SHORT).show()
            }
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser.uid)
            // Disabling the button if the user is logged in
            binding.logBtn.isClickable = false
            binding.logBtn.isFocusable = false
            binding.logBtn.visibility = View.GONE

            binding.logoutBtn.visibility = View.VISIBLE
            binding.logoutBar.visibility = View.VISIBLE
            binding.logoutBtn.setOnClickListener {
                signOutUser()
            }

        } else{
            binding.logBtn.visibility = View.VISIBLE
            binding.logBtn.setOnClickListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }

            binding.logoutBtn.visibility = View.GONE
            binding.logoutBar.visibility = View.GONE
        }

    }

    private fun signOutUser() {
        auth.signOut()

        Toast.makeText(requireContext(), "Successfully signed out", Toast.LENGTH_SHORT).show()

        // Hiding log out button
        binding.logoutBtn.visibility = View.GONE
        binding.logoutBar.visibility = View.GONE

        // Showing prev info
        binding.userName.text = "Sign Up/Login"
        binding.userEmail.text = "W&T Member"
        binding.bottomTextView.visibility = View.VISIBLE

        binding.userName.visibility = View.VISIBLE
        binding.userEmail.visibility = View.VISIBLE
        binding.bottomTextView.visibility = View.VISIBLE

        // Enabling the button if the user is logged out
        binding.logBtn.isClickable = true
        binding.logBtn.isFocusable = true
        binding.logBtn.visibility = View.VISIBLE

        // Login button function
        binding.logBtn.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchUserData(uid: String) {
        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value?.toString() ?: "No Name"
                val email = snapshot.child("email").value?.toString() ?: "No Email"

                binding.userName.text = name
                binding.userEmail.text = email

                binding.bottomTextView.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
            }
        })
    }
}