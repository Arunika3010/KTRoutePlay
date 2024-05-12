package com.kttelematicsolutions.routeplay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kttelematicsolutions.routeplay.MainViewModel
import com.kttelematicsolutions.routeplay.R
import com.kttelematicsolutions.routeplay.adapters.UserListAdapter
import com.kttelematicsolutions.routeplay.realmClasses.User


class UserListDialogFragment : DialogFragment() {
	private val viewModel : MainViewModel by viewModels()
	private lateinit var userListAdapter : UserListAdapter
	override fun onCreateView(
		inflater : LayoutInflater , container : ViewGroup? ,
		savedInstanceState : Bundle?
	) : View? {
		return inflater.inflate(R.layout.fragment_user_list_dialog, container , false)
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view , savedInstanceState)
		val userList = view.findViewById<RecyclerView>(R.id.user_recycler_view)
		val addAccount : TextView = view.findViewById(R.id.addAccount)
		addAccount.setOnClickListener {
			val navController = findNavController()
			navController.navigate(R.id.action_homeFragment_to_signupFragment)
			dialog?.dismiss()
		}
		viewModel.getAllUsers()
		viewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
			val layoutManager = LinearLayoutManager(requireContext())
			userList.layoutManager = layoutManager
			userListAdapter = UserListAdapter(users , ::onUserSelected)
			userList.adapter = userListAdapter
			//userListAdapter.notifyDataSetChanged()
		}
		dialog?.window?.setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT ,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)
	}

	private fun onUserSelected(user : User) {
		user.email?.let { viewModel.setCurrentUser(it) }
		viewModel.currentUser()
		dialog?.dismiss()
	}


}