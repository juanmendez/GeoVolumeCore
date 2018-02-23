package info.juanmendez.geovolumecore.types.fence.models

import info.juanmendez.geovolumecore.types.location.models.Location

/**
 * Created by @juanmendezinfo on 2/6/18.
 * This fenceKey allows Google AwarenessConst to track user's location and limits where the volume is off
 * Lat/Lon are in Location, as that object was developed earlier to track user's current location
 */
data class GeoFence(override var isActive: Boolean) :Fence {
    var location: Location = Location()
    var radius:Long = 0 //size of circumference
}