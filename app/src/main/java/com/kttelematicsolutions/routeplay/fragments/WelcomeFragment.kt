package com.kttelematicsolutions.routeplay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kttelematicsolutions.routeplay.R
import com.kttelematicsolutions.routeplay.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
	private lateinit var binding : FragmentWelcomeBinding
	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View {
		binding = FragmentWelcomeBinding.inflate(inflater , container , false)
		return binding.root
	}

	override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
		super.onViewCreated(view , savedInstanceState)
		binding.login.setOnClickListener { navigateToLogin() }
		binding.signUpButton.setOnClickListener { navigateToSignUp()  }
	}

	private fun navigateToSignUp(){
		val navController = findNavController()
		navController.navigate(R.id.action_welcomeFragment_to_signupFragment)
	}

	private fun navigateToLogin(){
		val navController = findNavController()
		navController.navigate(R.id.action_welcomeFragment_to_loginFragment)
	}

}