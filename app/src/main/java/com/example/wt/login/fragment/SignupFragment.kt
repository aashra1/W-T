package com.example.wt.login.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wt.viewModel.UserViewModel
import com.example.wt.databinding.FragmentSignupBinding
import com.example.wt.login.activity.LoginActivity
import com.example.wt.model.UserModel
import com.example.wt.repository.UserRepositoryImpl


class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        binding.signInbutton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput2.text.toString()
            val password = binding.passwordInput2.text.toString()
            val confirmPassword = binding.confirmpwInput.text.toString()

            if (validateInputs(name, email, password, confirmPassword)) {
                userViewModel.signup(email, password) { success, message, id ->
                    if (success) {
                        val userModel = UserModel(id.toString(), name, email)
                        userViewModel.addUserToDB(id, userModel) { success, message ->
                            if (success) {
                                Toast.makeText(requireContext(), "Signup successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), LoginActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(context, "Name is required!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(context, "Email is required!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Enter a valid email address!", Toast.LENGTH_SHORT).show()
            return false
        }



        return true
    }
}