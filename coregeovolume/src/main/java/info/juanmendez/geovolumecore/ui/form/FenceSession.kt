package info.juanmendez.geovolumecore.ui.form

import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence

/**
 * Created by juan on 2/10/18.
 */
class FenceSession(val awarenessService: CoreAwarenessService) {
    private var _fence:VolumeFence? = null

    var fence:VolumeFence
        set(value) {
            _fence = value
        }
        get() {
            if( _fence == null )
                _fence = awarenessService.getLastFence() ?: VolumeFence(false)

            return _fence as VolumeFence
        }
}