package com.kttelematicsolutions.routeplay.realmClasses

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Otp:RealmObject {
	@PrimaryKey
	var id: String = ""
	var code: String = ""
	var expiryTime: Long = 0L
}