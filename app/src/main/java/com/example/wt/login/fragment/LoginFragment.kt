package com.example.wt.login.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.wt.R
import com.example.wt.databinding.FragmentLoginBinding
import com.example.wt.repository.UserRepositoryImpl
import com.example.wt.ui.activity.NavigationActivity
import com.example.wt.viewModel.UserViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        binding.loginBtn.setOnClickListener {

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