package info.juanmendez.geovolumecore.types.fence.models

/**
 * Created by @juanmendezinfo on 2/6/18.
 * VolumeFence has a hold of each which combined makes a whole awareness fenceKey
 * This is helpful for caching data in each fenceKey when its active property is toggled
 */
data class VolumeFence(override var isActive: Boolean, var key:String = ""):Fence {
    val geoFence: GeoFence = GeoFence(false)
    val headphoneFence:HeadphoneFence = HeadphoneFence(false)
    val timeFence:TimeFence = TimeFence(false)

    fun asList():List<Fence>{
        return listOf(geoFence,headphoneFence, timeFence)
    }
}