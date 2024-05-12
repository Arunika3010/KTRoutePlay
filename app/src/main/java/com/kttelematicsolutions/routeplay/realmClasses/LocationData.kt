package com.kttelematicsolutions.routeplay.realmClasses

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class LocationData : RealmObject {
	@PrimaryKey
	var id: String = UUID.randomUUID().toString()
	var userId: String = ""
	var latitude: Double = 0.0
	var longitude: Double = 0.0
	var timestamp: String = ""
}