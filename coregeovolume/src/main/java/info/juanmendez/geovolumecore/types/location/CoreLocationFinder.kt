package info.juanmendez.geovolumecore.types.location

import info.juanmendez.geovolumecore.types.location.models.Location
import io.reactivex.Observable

/**
 * Created by @juanmendezinfo on 2/3/18.
 */
interface CoreLocationFinder {
    fun startTracking():Observable<Location>
    fun stopTracking()
    fun hasPermission(): Boolean
}