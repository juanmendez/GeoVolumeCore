package info.juanmendez.geovolumecore.types.fence.models

/**
 * Created by @juanmendezinfo on 2/6/18.
 */
data class TimeFence(override var isActive: Boolean) :Fence {
    var interval:Long = 0 //this is the interval which can remain constant
    var expiration:Long = 0 //this is the timeExpiration the fenceKey is set to expire
}