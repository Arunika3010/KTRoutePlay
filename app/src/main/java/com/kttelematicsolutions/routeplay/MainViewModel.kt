package com.kttelematicsolutions.routeplay

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kttelematicsolutions.routeplay.realmClasses.LocationData
import com.kttelematicsolutions.routeplay.realmClasses.Otp
import com.kttelematicsolutions.routeplay.realmClasses.RealmApplication
import com.kttelematicsolutions.routeplay.realmClasses.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.delete
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
	private val realm = RealmApplication.realm
	private val _usersLiveData = MutableLiveData<List<User>>()
	val usersLiveData: LiveData<List<User>> = _usersLiveData
	private val _currentUser = MutableLiveData<User>()
	val currentUser : LiveData<User> = _currentUser
	private val _usersLocationLiveData = MutableLiveData<List<LocationData>>()
	val usersLocationLiveData: LiveData<List<LocationData>> = _usersLocationLiveData
	fun addUser(
		email : String ,
		username : String ,
		password : String ,
		otp : String
	) {
		viewModelScope.launch {
			realm.write {
				val user = User().apply {
					this.username = username
					this.email = email
					this.password = password
				}
				copyToRealm(user , updatePolicy = UpdatePolicy.ALL)
				val generateOtp = Otp().apply {
					this.code = otp
					this.id = user.email.toString()
				}
				copyToRealm(generateOtp , updatePolicy = UpdatePolicy.ALL)
			}
		}
	}

	fun verifyOtp(
		enteredOtp : String ,
		email : String ,
		callback : (success : Boolean) -> Unit
	) {
		viewModelScope.launch {
			try {
				val otpObject = realm.query<Otp>("id == $0" , email).find().first()
				if (otpObject.code == enteredOtp) {
					realm.write {
						this.delete<Otp>() // Delete the used OTP
					}
					callback(true)
					realm.query<User>("email == $0", email).find().first().also {
						realm.writeBlocking {
							findLatest(it)?.isLoggedIn = true
						}
					}

				} else {
					callback(false)
				}
			} catch (exception : Exception) {
				Log.e("OtpViewModel" , "Error verifying user: ${exception.message}")
			}

		}
	}

	fun loginUser(email : String, password : String, otp : String, callback : (success : Boolean) -> Unit){
		viewModelScope.launch {
			try {
				val userObject = realm.query<User>("email == $0 AND password == $1", email, password).find().first()
				if(userObject.isLoggedIn){
					val generateOtp = Otp().apply {
						this.code = otp
						this.id = email
					}
					realm.write {
						copyToRealm(generateOtp , updatePolicy = UpdatePolicy.ALL)
					}
					realm.query<User>("email == $0", email).find().first().also {
						realm.writeBlocking {
							findLatest(it)?.isLoggedIn = true
							findLatest(it)?.currentUser = true
						}

					}
					callback(true)
				}else{
					callback(false)
				}
			}catch (exception : Exception){
				callback(false)
				Log.e("LoginViewModel" , "User Login Failed: ${exception.message}")
			}
		}
	}
	fun checkUserAlreadyExists(email: String, username: String, callback : (success : Boolean) -> Unit){
		viewModelScope.launch {
			try {
				realm.query<User>("email == $0 OR username == $1", email, username).find().first()
				callback(true)
			}catch (exception : Exception){
				callback(false)
				Log.e("SignupViewModel" , "User not exists: ${exception.message}")
			}
		}
	}

	fun setCurrentUser(selectedUser: String) {
		viewModelScope.launch {
			try {
				realm.query<User>("email == $0", selectedUser).find().first().also {
					realm.writeBlocking {
						findLatest(it)?.currentUser = true
					}
					Log.d("SignupViewModel" , "User updated: Done")
				}
				val otherUsers = realm.query<User>("email != $0", selectedUser)
				for (user in otherUsers.find()) {
					realm.write{
						findLatest(user)?.currentUser = false
					}
				}
				Log.d("SignupViewModel" , "Other User update: Done")
			}catch (exception : Exception){
				Log.e("SignupViewModel" , "User not exists: ${exception.message}")
			}

		}
	}
	fun getAllUsers() {
		viewModelScope.launch {
			val userResults: RealmResults<User> = realm.query<User>().find()
			val users = userResults.toList()
			_usersLiveData.postValue(users)
		}
	}
	fun currentUser(){
		viewModelScope.launch {
			try {
				val currentUserResult = realm.query<User>("currentUser==$0", true).find().first()
				_currentUser.postValue(currentUserResult)
			}catch (exception : Exception){
				Log.e("HomeViewModel" , "User not found: ${exception.message}")
			}

		}

	}
	fun saveLocationDataToRealm(userId: String, locationResult: Location, timestamp: String) {
		viewModelScope.launch {
			realm.write {
				val locationData = LocationData().apply {
					this.userId = userId
					this.latitude = locationResult.latitude
					this.longitude = locationResult.longitude
					this.timestamp = timestamp
				}
				copyToRealm(locationData, UpdatePolicy.ALL)
				Log.e("HomeViewModel" , "User location: $locationData")
			}
		}
	}
	fun getLocationOfUser(userId: String){
		viewModelScope.launch{
			try {
				val currentUserLocationResult = realm.query<LocationData>("userId==$0", userId).find().toList()
				_usersLocationLiveData.postValue(currentUserLocationResult)
			}catch (exception: Exception){
				Log.e("HomeViewModel" , "User location not found: ${exception.message}")
			}
		}
	}

	fun getPreviousLocations(userId: String, currentLocationId: String): LiveData<List<LocationData>> {
		val previousLocationsLiveData = MutableLiveData<List<LocationData>>()
		viewModelScope.launch {
			try {
				val previousLocationsResult = realm.query<LocationData>("userId == $0 AND id <= $1", userId, currentLocationId).find()

				previousLocationsLiveData.postValue(previousLocationsResult)
			} catch (exception: Exception) {
				Log.e("MainViewModel", "Error retrieving previous locations: ${exception.message}")
			}
		}
		return previousLocationsLiveData
	}

}