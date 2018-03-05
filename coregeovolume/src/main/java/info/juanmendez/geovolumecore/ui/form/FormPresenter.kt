package info.juanmendez.geovolumecore.ui.form

import info.juanmendez.geovolumecore.types.awareness.AwarenessConst
import info.juanmendez.geovolumecore.types.fence.FenceUtils
import info.juanmendez.geovolumecore.types.fence.models.FenceEvent
import info.juanmendez.geovolumecore.types.location.LocationTracker
import io.reactivex.disposables.CompositeDisposable
import android.databinding.Observable.OnPropertyChangedCallback as OnCallback

/**
 * Created by @JuanMendezInfo on 2/3/18.
 * Currently presenter takes care of tracking location
 * It is also tracking network being online.
 * In the event the network is offline we need to stopCurrentFence tracking location, if running
 * 0. watch for network status
 * * ! Class ensures to track all subscriptions and release them when view rotates or closes
 */
class FormPresenter(private val view:CoreFormView, private var m:FormModule ):Presenter<CoreFormView,FormViewModel> {

    private lateinit var mVm:FormViewModel
    private var mLocationTracker: LocationTracker? = null
    private var mDisposable:CompositeDisposable = CompositeDisposable()

    private val mVmManager = FormViewModelManager()

    override fun getVM(): FormViewModel= mVm

    override fun start() {

        mVm = FormViewModel( m.session.fence )
        mVmManager.start(mVm, m.session.fence )

        //0
        mDisposable.add( m.network.listening().subscribe({
            if( !it ){
                stopTrackingLocation()
            }
            mVm.isOnline.set( it )
        }))


        mDisposable.add( m.awarenessService.listen().subscribe({
            if( it.status == FenceEvent.FENCE_STARTED ){
                mVm.isVolumeFenceActive.set(true)
            }else if( it.status == FenceEvent.FENCE_CANCELED ){
                //lets ensure we stop, and reset volumeFence
                mVmManager.stop()

                FenceUtils.updateFromCancellation( m.session.fence )
                mVmManager.start(mVm, m.session.fence )
            }
        }))
    }

    fun browseLocation(){
        val geoFence = m.session.fence.geoFence

        if( FenceUtils.isValid( geoFence )){
            view.browseLocation( geoFence.location.lat, geoFence.location.lon )
        }
    }

    /**
     * This is useful functionality in the event of replacing VolumeFence
     */
    fun refresh(){
        stop()
        start()
    }

    fun startTrackingLocation(){
        mLocationTracker = LocationTracker(mVm, m)
        mLocationTracker?.startTracking()
    }

    fun stopTrackingLocation() {
        mLocationTracker?.stopTracking()
        mLocationTracker = null
    }

    fun setFenceAction( action:Int ){
        when( action ){
            AwarenessConst.START_FENCE->{
                m.awarenessService.startFence( m.session.fence )
            }
            AwarenessConst.STOP_FENCE->{
                m.awarenessService.stopCurrentFence()
            }
            AwarenessConst.RESET_FENCE ->{
                m.awarenessService.startFence( m.session.fence )
            }
        }
    }

    override fun stop() {
        mVmManager.stop()
        mDisposable.clear()
        stopTrackingLocation()
        mLocationTracker = null
    }
}