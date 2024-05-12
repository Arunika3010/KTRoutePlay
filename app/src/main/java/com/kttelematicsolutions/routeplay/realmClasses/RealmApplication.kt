package com.kttelematicsolutions.routeplay.realmClasses

import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration


class RealmApplication : Application() {
	companion object {
		lateinit var realm : Realm
	}

	override fun onCreate() {
		super.onCreate()
		realm = Realm.open(
			configuration = RealmConfiguration.Builder(
				schema = setOf(
					User::class ,
					Otp::class ,
					LocationData::class
				)
			)
				.schemaVersion(5L)
				.build()
		)

	}
}