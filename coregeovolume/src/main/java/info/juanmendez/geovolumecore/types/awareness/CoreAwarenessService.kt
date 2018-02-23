package info.juanmendez.geovolumecore.types.awareness

import info.juanmendez.geovolumecore.types.fence.models.FenceEvent
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import io.reactivex.Observable

/**
 * Created by juan on 2/10/18.
 * This is the interface for Google AwarenessConst
 * Aside from starting and stopping a fenceKey, it will also keep track of the last fenceKey activated
 */
interface CoreAwarenessService {
    fun getLastFence():VolumeFence?
    fun startFence(volumeFence: VolumeFence )
    fun stopCurrentFence()
    fun onReboot()
    fun listen(): Observable<FenceEvent>
}