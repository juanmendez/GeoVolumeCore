package info.juanmendez.geovolumecore.types.awareness

import info.juanmendez.geovolumecore.types.fence.models.VolumeFence

/**
 * Created by juan on 2/23/18.
 */
interface CoreFenceStorage {
    var lastFence:VolumeFence
    var cancellationPending:Boolean
}