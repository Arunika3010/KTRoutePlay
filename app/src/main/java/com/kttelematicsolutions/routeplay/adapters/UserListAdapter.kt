package com.kttelematicsolutions.routeplay.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kttelematicsolutions.routeplay.R
import com.kttelematicsolutions.routeplay.realmClasses.User

class UserListAdapter(
	private var users : List<User>,
	private val onUserSelected : (User) -> Unit
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
	class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
		val username : TextView = itemView.findViewById(R.id.userName)
		val userEmail : TextView = itemView.findViewById(R.id.userEmail)
	}

	override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : UserViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.popup_user_recycler_item, parent , false)

		return UserViewHolder(view)
	}

	override fun onBindViewHolder(holder : UserViewHolder, position : Int) {
		val user = users[position]
		holder.username.text = user.username
		holder.userEmail.text = user.email
		holder.itemView.setOnClickListener {
			onUserSelected(user)
		}
	}

	override fun getItemCount() : Int = users.size


}