package info.juanmendez.geovolumecore.types.fence.models

/**
 * Created by @juanmendezinfo on 2/6/18.
 * So if the user is in a fenceKey where the volume is off,
 * this can change the fenceKey rule by allowing volume to go up if headphones are on
 */
data class HeadphoneFence(override var isActive: Boolean) :Fence {
    var enableVolumeWhenOn:Boolean = true
}