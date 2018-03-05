package info.juanmendez.geovolumecore.types.fence


import info.juanmendez.geovolumecore.types.fence.models.*
import java.util.concurrent.TimeUnit

/**
 * Created by juan on 2/6/18.
 */
class FenceUtils {

    companion object {
        /**
         * Find out if fenceKey is valid based on subtype including VolumeFence
         */
        fun isValid( fence:Fence ):Boolean{

            return when( fence ){

                is VolumeFence ->{

                    val fences = fence.asList()
                    fences.any { it !is HeadphoneFence && FenceUtils.isValid(it) }
                }

                is GeoFence ->{

                    fence.isActive &&
                    fence.radius != 0L &&
                            fence.location.lat != 0.0 &&
                            fence.location.lon != 0.0
                }

                is TimeFence->{
                    fence.isActive && fence.expiration != 0L
                }

                is HeadphoneFence->{
                    fence.isActive
                }
                else -> {
                    false
                }
            }
        }

        /**
         * get all fences which are valid from volumeFence
         */
        fun getActiveFences( volumeFence: VolumeFence )=volumeFence.asList().filter { FenceUtils.isValid(it) }


        /**
         * Any fence must expire once its timeFence is up.
         * This method checks for that comparing current time in milliseconds.
         * If there is a difference of a minute or less, then it returns true
         */
        fun isTimeExpired( volumeFence: VolumeFence ):Boolean{
            var timeFence = volumeFence.timeFence
            var isTimeExpired = timeFence.isActive && System.currentTimeMillis() >= timeFence.expiration

            return isTimeExpired
        }

        fun updateFromCancellation(fence: VolumeFence) {
            fence.isActive = false
            fence.timeFence.expiration = 0
            fence.timeFence.interval = 0
        }
    }
}