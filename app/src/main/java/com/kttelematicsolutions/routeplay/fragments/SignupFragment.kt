package com.kttelematicsolutions.routeplay.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kttelematicsolutions.routeplay.MainViewModel
import com.kttelematicsolutions.routeplay.R
import com.kttelematicsolutions.routeplay.databinding.FragmentSignupBinding
import kotlin.random.Random


class SignupFragment : Fragment() {
	private lateinit var binding : FragmentSignupBinding
	private val viewModel : MainViewModel by viewModels()
	private var isPasswordVisible = false
	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View {
		binding = FragmentSignupBinding.inflate(inflater , container , false)
		return binding.root
	}

	override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
		super.onViewCreated(view , savedInstanceState)
		setFieldIconClicks()
		binding.signupButton.setOnClickListener { signUp() }
		binding.login.setOnClickListener { navigateToLogin() }
		binding.password.setOnClickListener { togglePasswordVisibility() }
		binding.confirmPassword.setOnClickListener { toggleConfirmPasswordVisibility() }
		binding.backArrow.setOnClickListener { handleBackPress() }
	}

	private fun navigateToLogin() {
		val navController = findNavController()
		navController.navigate(R.id.action_signupFragment_to_loginFragment)
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun setFieldIconClicks() {
		binding.userEmail.setOnTouchListener { _ , event ->
			val drawableRight = 2
			if (event.action == MotionEvent.ACTION_UP) {
				if (event.rawX >= (binding.userEmail.right - binding.userEmail.compoundDrawables[drawableRight].bounds.width())) {
					binding.userEmail.text?.clear()
					binding.userEmail.requestFocus()
					binding.userEmail.performClick()
					return@setOnTouchListener true
				}
			}
			false
		}
		binding.userName.setOnTouchListener { _ , event ->
			val drawableRight = 2
			if (event.action == MotionEvent.ACTION_UP) {
				if (event.rawX >= (binding.userName.right - binding.userName.compoundDrawables[drawableRight].bounds.width())) {
					binding.userName.text?.clear()
					binding.userName.requestFocus()
					binding.userName.performClick()
					return@setOnTouchListener true
				}
			}
			false
		}
	}

	private fun signUp() {
		val email = binding.userEmail.text.toString().trim()
		val username = binding.userName.text.toString().trim()
		val password = binding.password.text.toString().trim()
		val confirmPassword = binding.confirmPassword.text.toString().trim()
		if (validateInput(email , username , password , confirmPassword)) {
			val otp = generateRandomOtp()
			try {
				viewModel.addUser(email , username , password, otp)
				Toast.makeText(requireContext() , "Sign up successful!" , Toast.LENGTH_SHORT)
					.show()
				val bundle = Bundle().apply {
					putString("email", email)
					putString("otp", otp)
				}
				val sharedPreferences = requireContext().getSharedPreferences("user_prefs",
					Context.MODE_PRIVATE
				)
				val editor = sharedPreferences.edit()
				editor.putBoolean("isLoggedIn", true)
				editor.apply()
				findNavController().navigate(R.id.action_signupFragment_to_otpFragment, bundle)
			} catch (e : Exception) {
				Toast.makeText(requireContext() , "Sign up failed!" , Toast.LENGTH_SHORT).show()
				e.printStackTrace()
			}
		} else {
			Toast.makeText(requireContext() , "Sign up failed!" , Toast.LENGTH_SHORT).show()
		}
	}

	private fun validateInput(
		email : String ,
		username : String ,
		password : String ,
		confirmPassword : String
	) : Boolean {
		if (email.isEmpty() || ! Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			binding.error.visibility = View.VISIBLE
			binding.error.text = getString(R.string.enter_a_valid_email_address)
			return false
		}
		if (username.isEmpty()) {
			binding.usernameError.visibility = View.VISIBLE
			binding.usernameError.text = getString(R.string.username_is_mandatory)
			return false
		}
		if (password.isEmpty()) {
			binding.passwordError.visibility = View.VISIBLE
			binding.passwordError.text = getString(R.string.password_is_mandatory)
			return false
		}
		if (password != confirmPassword) {
			binding.confirmPasswordError.visibility = View.VISIBLE
			binding.confirmPasswordError.text = getString(R.string.passwords_do_not_match)
			return false
		}
		if (confirmPassword.isEmpty()) {
			binding.confirmPasswordError.visibility = View.VISIBLE
			binding.confirmPasswordError.text = getString(R.string.enter_password_again_to_continue)
			return false
		}
		if(checkUserAlreadyExists(email, username)){
			Toast.makeText(requireContext() , "Email or username already in use" , Toast.LENGTH_LONG).show()
			return false
		}

		binding.error.visibility = View.GONE
		binding.usernameError.visibility = View.GONE
		binding.passwordError.visibility = View.GONE
		binding.confirmPasswordError.visibility = View.GONE
		return true
	}

	private fun checkUserAlreadyExists(email : String , username : String): Boolean{
		var userExists = false
		viewModel.checkUserAlreadyExists(email, username) { exists ->
			userExists = exists
		}
		return userExists
	}


	private fun togglePasswordVisibility() {
		if (isPasswordVisible) {
			// Password is visible, changing input type to password
			binding.password.inputType = 129 // 129 corresponds to textPassword
			binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(
				0 ,
				0 ,
                R.drawable.ic_visible,
				0
			)
			isPasswordVisible = false
		} else {
			// Password is hidden, changing input type to text to make it visible
			binding.password.inputType = 144 // 144 corresponds to text
			binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(
				0 ,
				0 ,
                R.drawable.ic_not_visible,
				0
			)
			isPasswordVisible = true
		}
		binding.password.text?.let { binding.password.setSelection(it.length) }
	}

	private fun toggleConfirmPasswordVisibility() {
		if (isPasswordVisible) {
			// Password is visible, changing input type to password
			binding.confirmPassword.inputType = 129 // 129 corresponds to textPassword
			binding.confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
				0 ,
				0 ,
                R.drawable.ic_visible,
				0
			)
			isPasswordVisible = false
		} else {
			// Password is hidden, changing input type to text to make it visible
			binding.confirmPassword.inputType = 144 // 144 corresponds to text
			binding.confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
				0 ,
				0 ,
                R.drawable.ic_not_visible,
				0
			)
			isPasswordVisible = true
		}
		binding.confirmPassword.text?.let { binding.confirmPassword.setSelection(it.length) }

	}

	private fun handleBackPress() {
		findNavController().navigateUp()
	}

	private fun generateRandomOtp(length : Int = 4) : String {
		val digits = "0123456789"
		val random = Random.Default
		return (1 .. length).map { digits[random.nextInt(digits.length)] }.joinToString("")
	}

}