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
        binding=FragmentSignupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo=UserRepositoryImpl()
        userViewModel= UserViewModel(repo)

        var currentUser = userViewModel.getCurrentUser().toString()
        binding.signInbutton.setOnClickListener{
            userViewModel.signup(binding.emailInput2.text.toString(),binding.passwordInput2.text.toString()){
                success,messege,id->
                if(success){
                    val model=UserModel(
                        id.toString(),
                        binding.nameInput.text.toString()
                    )
                    userViewModel.addUserToDB(id,model){
                        success,message->
                        if(success){
                            Toast.makeText(requireContext(),messege,Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(),messege,Toast.LENGTH_SHORT).show()
                        }
                    }
                }else
                {
                    Toast.makeText(requireContext(),messege,Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}