package info.juanmendez.geovolumecore.types.location

import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * Created by @juanmendezinfo on 2/3/18.
 */
interface CoreNetworkService {
    fun isOnline(): Boolean
    fun listening(): Observable<Boolean>
}