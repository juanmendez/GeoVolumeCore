package info.juanmendez.geovolumecore.types.awareness

import android.content.Intent
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence

/**
 * Created by juan on 2/23/18.
 */
interface CoreAwarenessBuilder {
    fun startFence( fence:VolumeFence)
    fun stopFence( fence:VolumeFence )
    fun getHeadphoneState( stateHandler:(Boolean)->Unit )
}