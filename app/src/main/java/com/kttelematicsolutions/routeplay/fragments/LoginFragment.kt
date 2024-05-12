package com.kttelematicsolutions.routeplay.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
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
import com.kttelematicsolutions.routeplay.databinding.FragmentLoginBinding
import kotlin.random.Random

class LoginFragment : Fragment() {
	private lateinit var binding : FragmentLoginBinding
	private val viewModel : MainViewModel by viewModels()
	private var isPasswordVisible = false
	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View {
		binding = FragmentLoginBinding.inflate(inflater , container , false)
		return binding.root
	}

	override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
		super.onViewCreated(view , savedInstanceState)
		setFieldIconClicks()
		binding.backArrow.setOnClickListener { handleBackPress() }
		binding.signup.setOnClickListener { navigateToSignup() }
		binding.verifyOtpButton.setOnClickListener { login() }
		binding.RegisteredPassword.setOnClickListener { togglePasswordVisibility() }
	}

	private fun navigateToSignup() {
		val navController = findNavController()
		navController.navigate(R.id.action_loginFragment_to_signupFragment)
	}

	private fun login() {
		val email = binding.RegisteredUserEmail.text.toString().trim()
		val password = binding.RegisteredPassword.text.toString().trim()
		if (validateInput(email , password)) {
			val otp = generateRandomOtp()
			try {
				viewModel.loginUser(email , password, otp) { success ->
					if (success) {
						Toast.makeText(requireContext() , "Login successful!" , Toast.LENGTH_LONG)
							.show()
						val bundle = Bundle().apply {
							putString("email" , email)
							putString("otp" , otp)
						}
						val sharedPreferences = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
						val editor = sharedPreferences.edit()
						editor.putBoolean("isLoggedIn", true)
						editor.apply()
						findNavController().navigate(
                            R.id.action_loginFragment_to_otpFragment,
							bundle
						)
					} else {
						Toast.makeText(
							requireContext() ,
							"Login failed. Check entered details" ,
							Toast.LENGTH_LONG
						).show()
					}
				}

			} catch (e : Exception) {
				Toast.makeText(
					requireContext() ,
					"Login failed. Check entered details" ,
					Toast.LENGTH_LONG
				).show()
				e.printStackTrace()
			}
		} else {
			Toast.makeText(
				requireContext() ,
				"Login failed. Check entered details" ,
				Toast.LENGTH_LONG
			).show()
		}
	}

	private fun validateInput(
		email : String ,
		password : String ,
	) : Boolean {
		if (email.isEmpty() || ! Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			binding.error.visibility = View.VISIBLE
			binding.error.text = getString(R.string.enter_a_valid_email_address)
			return false
		}
		if (password.isEmpty()) {
			binding.passwordError.visibility = View.VISIBLE
			binding.passwordError.text = getString(R.string.password_is_mandatory)
			return false
		}
		binding.error.visibility = View.GONE
		binding.passwordError.visibility = View.GONE
		return true
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun setFieldIconClicks() {
		binding.RegisteredUserEmail.setOnTouchListener { _ , event ->
			val drawableRight = 2
			if (event.action == MotionEvent.ACTION_UP) {
				if (event.rawX >= (binding.RegisteredUserEmail.right - binding.RegisteredUserEmail.compoundDrawables[drawableRight].bounds.width())) {
					binding.RegisteredUserEmail.text?.clear()
					binding.RegisteredUserEmail.requestFocus()
					binding.RegisteredUserEmail.performClick()
					return@setOnTouchListener true
				}
			}
			false
		}
	}

	private fun togglePasswordVisibility() {
		if (isPasswordVisible) {
			// Password is visible, changing input type to password
			binding.RegisteredPassword.inputType = 129 // 129 corresponds to textPassword
			binding.RegisteredPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
				0 ,
				0 ,
                R.drawable.ic_visible,
				0
			)
			isPasswordVisible = false
		} else {
			// Password is hidden, changing input type to text to make it visible
			binding.RegisteredPassword.inputType = 144 // 144 corresponds to text
			binding.RegisteredPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
				0 ,
				0 ,
                R.drawable.ic_not_visible,
				0
			)
			isPasswordVisible = true
		}
		binding.RegisteredPassword.text?.let { binding.RegisteredPassword.setSelection(it.length) }
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