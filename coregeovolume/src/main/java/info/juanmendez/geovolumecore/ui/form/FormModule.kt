package info.juanmendez.geovolumecore.ui.form

import android.content.Context
import info.juanmendez.geovolumecore.types.awareness.CoreAwarenessService
import info.juanmendez.geovolumecore.types.location.CoreLocationFinder
import info.juanmendez.geovolumecore.types.location.CoreNetworkService
import io.reactivex.Observable

/**
 * Created by @juanmendezinfo on 2/3/18.
 */
data class FormModule(var network: CoreNetworkService,
                      var finder: CoreLocationFinder,
                      var session: FenceSession,
                      var awarenessService: CoreAwarenessService){

    var locationPermit:Observable<Boolean>?=null
}