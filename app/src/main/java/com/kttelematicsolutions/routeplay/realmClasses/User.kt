package com.kttelematicsolutions.routeplay.realmClasses


import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class User : RealmObject {
	@PrimaryKey
	var id : ObjectId = ObjectId()
	var email : String? = ""
	var username : String? = ""
	var password : String? = ""
	var isLoggedIn: Boolean = false
	var currentUser: Boolean = false
}

