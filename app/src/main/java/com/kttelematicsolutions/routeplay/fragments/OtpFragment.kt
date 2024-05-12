package com.kttelematicsolutions.routeplay.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kttelematicsolutions.routeplay.MainViewModel
import com.kttelematicsolutions.routeplay.R
import com.kttelematicsolutions.routeplay.databinding.FragmentOtpBinding

class OtpFragment : Fragment() {
	private lateinit var binding : FragmentOtpBinding
	private val viewModel : MainViewModel by viewModels()
	private lateinit var sharedPreferences: SharedPreferences
	private var permissionGranted: Boolean = false
	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View {
		binding = FragmentOtpBinding.inflate(inflater , container , false)
		return binding.root
	}

	override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
		super.onViewCreated(view , savedInstanceState)
		sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
		permissionGranted = sharedPreferences.getBoolean("locationPermissionGranted", false)
		val passedEmail = arguments?.getString("email" , "") ?: ""
		val passedOtp = arguments?.getString("otp" , "") ?: ""
		val box1 = binding.otpBox1
		val box2 = binding.otpBox2
		val box3 = binding.otpBox3
		val box4 = binding.otpBox4

		Log.d("otpFrag", passedEmail)

		//setting up Text watchers
		setOtpTextWatcher(box1 , box2)
		setOtpTextWatcher(box2 , box3)
		setOtpTextWatcher(box3 , box4)
		setOtpTextWatcher(box4 , null)

		//setting up BackspaceWatcher
		setBackspaceWatcher(box1 , null)
		setBackspaceWatcher(box2 , box1)
		setBackspaceWatcher(box3 , box2)
		setBackspaceWatcher(box4 , box3)

		binding.otpIs.text = passedOtp
		binding.verifyOtpButton.setOnClickListener {
			//retrieving otp
			val no1 = box1.text.toString()
			val no2 = box2.text.toString()
			val no3 = box3.text.toString()
			val no4 = box4.text.toString()
			val otp : String = no1 + no2 + no3 + no4
			if (validateInput(otp)) {
				validateOtp(otp , passedEmail)
			}
		}

	}

	private fun validateOtp(enteredOTP : String , userEmailId : String) {
		viewModel.verifyOtp(enteredOTP , userEmailId) { success ->
			if (success && permissionGranted) {
				viewModel.setCurrentUser(userEmailId)
				Toast.makeText(requireContext() , "OTP Verified Successfully" , Toast.LENGTH_SHORT)
					.show()
				findNavController().navigate(R.id.action_otpFragment_to_homeFragment)
			}else if(!permissionGranted) {
				Toast.makeText(requireContext() , "Location Permission Required!" , Toast.LENGTH_SHORT).show()
			}
			else {
				Toast.makeText(requireContext() , "Invalid OTP!" , Toast.LENGTH_SHORT).show()
			}
		}

	}

	private fun validateInput(otp : String) : Boolean {
		if (otp.isEmpty()) {
			Toast.makeText(
				requireContext() ,
				"Enter the 4 digit OTP code properly" ,
				Toast.LENGTH_SHORT
			).show()
			return false
		}
		return true
	}

	private fun setOtpTextWatcher(currentBox : EditText , nextBox : EditText?) {
		currentBox.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(
				s : CharSequence? ,
				start : Int ,
				count : Int ,
				after : Int
			) {
			}

			override fun onTextChanged(
				s : CharSequence? ,
				start : Int ,
				before : Int ,
				count : Int
			) {
				if (s?.length == 1) {
					nextBox?.requestFocus()
				}
			}

			override fun afterTextChanged(s : Editable?) {}
		})
	}

	private fun setBackspaceWatcher(currentBox : EditText , prevBox : EditText?) {
		currentBox.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(
				s : CharSequence? ,
				start : Int ,
				count : Int ,
				after : Int
			) {
			}

			override fun onTextChanged(
				s : CharSequence? ,
				start : Int ,
				before : Int ,
				count : Int
			) {
			}

			override fun afterTextChanged(s : Editable?) {
				if (s.isNullOrEmpty() && prevBox != null) {
					prevBox.requestFocus()
				}
			}
		})
	}


}