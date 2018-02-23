package info.juanmendez.geovolumecore.types.awareness

import info.juanmendez.geovolumecore.types.fence.models.VolumeFence

/**
 * Created by juan on 2/10/18.
 */
interface CoreFenceTable {
    fun addOrUpdate( volumeFence: VolumeFence )
    fun remove( volumeFence: VolumeFence )
    fun getFences():List<VolumeFence>
}