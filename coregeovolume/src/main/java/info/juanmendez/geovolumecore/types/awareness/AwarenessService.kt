package info.juanmendez.geovolumecore.types.awareness

import info.juanmendez.geovolumecore.types.fence.FenceUtils
import info.juanmendez.geovolumecore.types.fence.models.FenceEvent
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Created by juan on 2/10/18.
 * This is the implementation of Google AwarenessConst. This class will also exchange information with
 * a BroadcastReceiver dedicated to handle Google AwarenessConst. It will also be notified once the device
 * is rebooted.
 */
class AwarenessService(val awarenessBuilder: CoreAwarenessBuilder, val fenceStorage: CoreFenceStorage ) : CoreAwarenessService {

    companion object {
         const val DEFAULT_FENCE_KEY = "geovolume_default_fence"
     }

    private val subject: BehaviorSubject<FenceEvent> = BehaviorSubject.create()

    override fun getLastFence(): VolumeFence? {
        return fenceStorage.lastFence
    }

    override fun startFence(volumeFence: VolumeFence) {
        volumeFence.isActive = true

        if( volumeFence.key.isEmpty() )
            volumeFence.key = DEFAULT_FENCE_KEY

        fenceStorage.lastFence = volumeFence
        fenceStorage.cancellationPending = false

        awarenessBuilder.startFence( volumeFence )
        subject.onNext( FenceEvent( FenceEvent.FENCE_STARTED, volumeFence.key ) )
    }

    /**
     * We don't just cancel unless headphones are unplugged..
     */
    override fun stopCurrentFence() {
        //fenceKey deactivated!

        var volumeFence = getLastFence()
        if( volumeFence?.headphoneFence?.isActive == true ){

            awarenessBuilder.getHeadphoneState {
                if( it ){
                    fenceStorage.cancellationPending = true
                    subject.onNext( FenceEvent( FenceEvent.FENCE_CANCELED, volumeFence.key ))
                }else{
                    forceToStopFence()
                }
            }
        }else{
            forceToStopFence()
        }

    }

    private fun forceToStopFence(){

        var volumeFence = getLastFence()

        if( volumeFence != null ){

            subject.onNext( FenceEvent( FenceEvent.FENCE_CANCELED, volumeFence.key ) )

            FenceUtils.updateFromCancellation( volumeFence)
            fenceStorage.lastFence = volumeFence
            fenceStorage.cancellationPending = false

            awarenessBuilder.stopFence( volumeFence )
        }
    }

    override fun listen(): Observable<FenceEvent> = subject

    override fun onReboot() {
        var lastFence = getLastFence()

        if( fenceStorage.cancellationPending ){
            forceToStopFence()
        }else if( lastFence?.isActive == true ){

            if( FenceUtils.isTimeExpired( lastFence ) ){
                forceToStopFence()
            }else{
                startFence( lastFence )
            }
        }
    }

    fun onFenceEvent(isWithinFence:Boolean ){

        if( fenceStorage.cancellationPending ){
            stopCurrentFence()
        }else{
            var lastFence = getLastFence()
            var key = lastFence?.key ?: ""

            if( key.isNotEmpty() && lastFence != null ){

                //withinFence means we don't want the volume up.
                //so within timeFence, volume down!
                //within radius while headphones off, volume down!
                subject.onNext( FenceEvent(FenceEvent.FENCE_UPDATED,
                        key,
                        isWithinFence))

                if( FenceUtils.isTimeExpired( lastFence )){
                    forceToStopFence()
                }
            }
        }
    }
}