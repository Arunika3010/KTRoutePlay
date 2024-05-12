package com.kttelematicsolutions.routeplay.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kttelematicsolutions.routeplay.realmClasses.LocationData
import com.kttelematicsolutions.routeplay.R

class LocationAdapter(
    private var locations: List<LocationData>,
    private var onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(location: LocationData)
    }
    fun updateData(updatedLocationData: List<LocationData>) {
        locations = updatedLocationData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_log_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)

    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationNameTextView: TextView = itemView.findViewById(R.id.location_name)
        private val cityNameTextView: TextView = itemView.findViewById(R.id.city_name)
        private val timeStampTextView: TextView = itemView.findViewById(R.id.time_stamp)
        private val viewButton: Button = itemView.findViewById(R.id.viewButton)
        private val locationLayout: RelativeLayout = itemView.findViewById(R.id.overlay)

        fun bind(location: LocationData) {
            locationNameTextView.text = location.latitude.toString()
            cityNameTextView.text = location.longitude.toString()
            timeStampTextView.text = location.timestamp
            viewButton.setOnClickListener {
                onItemClickListener.onItemClick(location)
            }
            locationLayout.setOnClickListener {
                onItemClickListener.onItemClick(location)
            }
        }
    }
}