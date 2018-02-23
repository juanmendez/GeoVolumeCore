package info.juanmendez.geovolumecore.ui.form

import info.juanmendez.geovolumecore.types.awareness.AwarenessConst
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
        mLocationTracker = LocationTracker(mVm, m)

        //0
        mDisposable.add( m.network.listening().subscribe({
            if( !it ){
                stopTrackingLocation()
            }
            mVm.isOnline.set( it )
        }))
    }

    /**
     * This is useful functionality in the event of replacing VolumeFence
     */
    fun refresh(){
        stop()
        start()
    }

    fun startTrackingLocation(){
        mLocationTracker?.startTracking()
    }

    fun stopTrackingLocation() {
        mLocationTracker?.stopTracking()
    }

    fun setFenceAction( action:Int ){
        when( action ){
            AwarenessConst.START_FENCE->{
                mVm.isVolumeFenceActive.set(true)
                m.awarenessService.startFence( m.session.fence )
            }
            AwarenessConst.STOP_FENCE->{
                mVm.isVolumeFenceActive.set(false)
                m.awarenessService.stopCurrentFence()
            }
            AwarenessConst.RESET_FENCE ->{
                mVm.isVolumeFenceActive.set(true)
                m.awarenessService.stopCurrentFence()
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