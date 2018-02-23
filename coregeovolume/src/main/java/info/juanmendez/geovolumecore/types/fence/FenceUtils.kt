package info.juanmendez.geovolumecore.types.fence


import info.juanmendez.geovolumecore.types.fence.models.*

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
    }
}