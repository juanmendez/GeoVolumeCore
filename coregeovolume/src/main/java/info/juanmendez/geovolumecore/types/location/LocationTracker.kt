package info.juanmendez.geovolumecore.types.location

import info.juanmendez.geovolumecore.ui.form.FormModule
import info.juanmendez.geovolumecore.ui.form.FormViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import timber.log.Timber

/**
 *
 * Created by @juanmendezinfo on 2/4/18.
 * LocationTracker takes care of tracking location
 *
 * 0. startFence tracking, works only if there is online, and location access is permitted
 *      User might see dialog permission once and press again to track
 * 1. locationFinder is constantly firing the closest location
 * 2. User has been pressing tracking button, upon release locationFinder stops
 */
class LocationTracker(val vm:FormViewModel, val m:FormModule ) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    init {
        //initiate also checking on locationPermission
        vm.hasLocationPermission.set( m.finder.hasPermission() )
    }

    fun startTracking(){
        //0
        if( m.network.isOnline() ){
            m.locationPermit?.let {
                disposable.add(it.subscribe( {
                    vm.hasLocationPermission.set( it ) //don't forget the Jocker!!
                    if( it ) trackLocation()
                }))
            }
        }
    }

    //1
    private fun trackLocation(){
        disposable.add( m.finder.startTracking().subscribe(Consumer{

            with( vm.location){
                this.name = it.name
                this.addressLine1 = it.addressLine1
                this.addressLine2 = it.addressLine2
                this.lat = it.lat
                this.lon = it.lon
            }

            Timber.i( "location ${it}")
            vm.notifyChange()
        }) )
    }

    //2
    fun stopTracking(){
        m.finder.stopTracking()
        disposable.clear()
    }
}