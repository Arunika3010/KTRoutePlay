package com.kttelematicsolutions.routeplay.fragments


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kttelematicsolutions.routeplay.LocationService
import com.kttelematicsolutions.routeplay.MainViewModel
import com.kttelematicsolutions.routeplay.R
import com.kttelematicsolutions.routeplay.adapters.LocationAdapter
import com.kttelematicsolutions.routeplay.databinding.FragmentHomeBinding
import com.kttelematicsolutions.routeplay.realmClasses.LocationData
import com.kttelematicsolutions.routeplay.realmClasses.User


class HomeFragment : Fragment(), LocationAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var userLocationListAdapter: LocationAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val navController by lazy { findNavController() }
    private var userId = ""


    override fun onStart() {
        super.onStart()
        viewModel.currentUser.observe(viewLifecycleOwner) { currentUser ->
            if (currentUser != null) {
                val serviceIntent = Intent(requireContext(), LocationService::class.java)
                serviceIntent.putExtra("userId", currentUser.username)
                requireContext().startService(serviceIntent)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        requireContext().stopService(Intent(requireContext(), LocationService::class.java))

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val locationRecycler = view.findViewById<RecyclerView>(R.id.location_recycler)
        val layoutManager = LinearLayoutManager(requireContext())
        locationRecycler.layoutManager = layoutManager
        viewModel.currentUser()
        binding.usernameLayout.setOnClickListener {
            showUserListPopup()
        }
        viewModel.currentUser.observe(viewLifecycleOwner) {
            viewModel.currentUser()
            if (it != null) {
                bindViewData(it)
                it.username?.let { it1 ->
                    userId = it1
                    viewModel.getLocationOfUser(it1)
                }
            }
        }
        userLocationListAdapter = LocationAdapter(emptyList() , this)
        locationRecycler.adapter = userLocationListAdapter

        viewModel.usersLocationLiveData.observe(viewLifecycleOwner) { locationData ->
            userLocationListAdapter.updateData(locationData)
        }

    }

    private fun bindViewData(currentUser: User) {
        binding.usernameText.text = currentUser.username
        binding.title2.text = getString(R.string.location_log, currentUser.username)
    }

    private fun showUserListPopup() {
        val userListDialog = UserListDialogFragment()
        userListDialog.show(childFragmentManager, "UserListDialog")
    }

    override fun onItemClick(location : LocationData) {
        binding.progress.visibility = View.VISIBLE
        val bundle = Bundle()
        bundle.putString("id", location.id)
        bundle.putString("userId", location.userId)
        bundle.putDouble("latitude", location.latitude)
        bundle.putDouble("longitude", location.longitude)
        bundle.putString("timestamp", location.timestamp)
        Log.d("TAG", "onItemClick: works")
        navController.navigate(R.id.action_homeFragment_to_mapsFragment, bundle)
        Log.d("TAG", "onItemClick: works2")
    }



}