package info.juanmendez.geovolumecore.types.fence.models

/**
 * Created by juan on 2/10/18.
 */
data class FenceEvent(var status:Int, var fenceKey:String="", var fenceValue:Boolean = false ){

    companion object {
        val FENCE_STARTED = 1
        val FENCE_CANCELED = 2
        val FENCE_UPDATED = 3
    }
}