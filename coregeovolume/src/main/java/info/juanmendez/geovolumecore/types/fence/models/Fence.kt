package info.juanmendez.geovolumecore.types.fence.models

/**
 * Created by @juanmendezinfo on 2/6/18.
 * Fences can be of different types but what they all have in common is if they are
 * being active or inactive. By default a compound fenceKey has a reference of
 * each one of them even if they are not active. That is useful for caching for example
 * distance, location, timeExpiration, etc in the event of disabling and enabling again the fenceKey.
 * @see VolumeFence
 */
interface Fence {
    var isActive:Boolean
}