package com.example.wt.login.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.wt.R
import com.example.wt.databinding.FragmentLoginBinding
import com.example.wt.repository.UserRepositoryImpl
import com.example.wt.ui.activity.NavigationActivity
import com.example.wt.viewModel.UserViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailInput1.text.toString()
            val password = binding.passwordInput1.text.toString()

            if (validateInputs(email, password)) {
                // Proceed with login or API call
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            }
        }

        val signupButton: Button = view.findViewById(R.id.signupBtn)
        signupButton.setOnClickListener {
            // Replace the current fragment with SignupFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, SignupFragment()) // Replace with your container ID
                .addToBackStack(null) // Optional: Adds this transaction to the back stack
                .commit()
        }
    }
    var repo = UserRepositoryImpl()


        private fun validateInputs(email: String, password: String): Boolean {
            if (email.isEmpty()) {
                Toast.makeText(context, "Email is required!", Toast.LENGTH_SHORT).show()
                return false
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Enter a valid email address!", Toast.LENGTH_SHORT).show()
                return false
            }

            if (password.isEmpty()) {
                Toast.makeText(context, "Password is required!", Toast.LENGTH_SHORT).show()
                return false
            }

            if (password.length < 6) {
                Toast.makeText(
                    context,
                    "Password must be at least 6 characters long!",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            return true
        }


        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    val email = binding.emailInput1.text.toString()
                    val password = binding.passwordInput1.text.toString()

                    userViewModel.login(email, password) { success, message ->
                        if (success) {
                            val intent = Intent(requireContext(), NavigationActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish() // Close the current activity
                        } else {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                binding.signupBtn.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, SignupFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
    }
    }