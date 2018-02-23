package info.juanmendez.geovolumecore.ui.form

import info.juanmendez.geovolumecore.types.fence.models.VolumeFence

/**
 * Implementation is a singleton which keeps the fenceKey being edited
 */
interface CoreFenceSession{
    var fence:VolumeFence
}