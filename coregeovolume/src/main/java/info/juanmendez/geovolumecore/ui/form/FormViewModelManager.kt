package info.juanmendez.geovolumecore.ui.form

import android.databinding.Observable
import com.android.databinding.library.baseAdapters.BR
import info.juanmendez.geovolumecore.types.fence.FenceUtils
import info.juanmendez.geovolumecore.types.fence.GeoSliderUtil
import info.juanmendez.geovolumecore.types.fence.TimeSliderUtil
import info.juanmendez.geovolumecore.types.fence.models.GeoFence
import info.juanmendez.geovolumecore.types.fence.models.HeadphoneFence
import info.juanmendez.geovolumecore.types.fence.models.TimeFence
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import org.joda.time.DateTime
import android.databinding.Observable.OnPropertyChangedCallback as Callback

/**
 * Created by juan on 2/7/18.
 * For reducing code in presenter, we move all data management to this helper class
 */
class FormViewModelManager{

    private lateinit var mVm:FormViewModel
    private lateinit var mVolumeFence:VolumeFence
    private lateinit var mGeoFence: GeoFence
    private lateinit var mHeadphoneFence: HeadphoneFence
    private lateinit var mTimeFence: TimeFence
    private var mTimeToTheFifteenth:Long = 0L


    private val callbacks:MutableList<Observable> = arrayListOf()

    fun start(vm:FormViewModel, volumeFence:VolumeFence ){
        mVm = vm

        //0
        mVolumeFence = volumeFence
        mGeoFence = volumeFence.geoFence
        mHeadphoneFence = volumeFence.headphoneFence
        mTimeFence = volumeFence.timeFence

        /**
         * If the fenceKey is already active, then initial time is what it is when initially set
         */
        if( mVolumeFence.isActive && FenceUtils.isValid(mTimeFence)){
           mTimeToTheFifteenth = mTimeFence.expiration - mTimeFence.interval
        }else{
            mTimeToTheFifteenth = TimeSliderUtil.toNextFifteenMod( DateTime.now().millis )
        }

        mVm.getObservablesWatched().forEach { addCallback(it) }

        //feed viewModel
        mVm.isGeoFenceActive.set( mGeoFence.isActive )
        mVm.isHeadphoneFenceActive.set( mHeadphoneFence.isActive )
        mVm.isTimeFenceActive.set( mTimeFence.isActive )
        mVm.isVolumeFenceValid.set( FenceUtils.isValid(mVolumeFence))

        //so we immediately tell if fenceKey is valid..
        mVm.isVolumeFenceValid.set( FenceUtils.isValid(mVolumeFence))
        mVm.isVolumeFenceActive.set( mVolumeFence.isActive )
        updateTime()
        updateRadius()
    }

    private val callback = object: Observable.OnPropertyChangedCallback() {
        //1
        override fun onPropertyChanged(observable: Observable?, br: Int) {
            when( br ){
                BR.timeInterval ->{
                    updateTime()
                }

                BR.radius->{
                    updateRadius()
                }
            }

            when( observable ){
                mVm.isGeoFenceActive ->{
                    mGeoFence.isActive = mVm.isGeoFenceActive.get()
                }

                mVm.isTimeFenceActive ->{
                    mTimeFence.isActive = mVm.isTimeFenceActive.get()
                }

                mVm.isHeadphoneFenceActive->{
                    mHeadphoneFence.isActive = mVm.isHeadphoneFenceActive.get()
                }

                mVm.isVolumeFenceActive->{
                    mVolumeFence.isActive = mVm.isVolumeFenceActive.get()
                }

            }

            mVm.isVolumeFenceValid.set( FenceUtils.isValid(mVolumeFence) )
        }
    }


    fun stop(){
        removeCallbacks()
    }

    private fun addCallback(listener:Observable ){
        callbacks.add( listener )
        listener.addOnPropertyChangedCallback( callback )
    }

    private fun removeCallbacks(){
        callbacks.forEach( { it.removeOnPropertyChangedCallback(callback) })
    }

    private fun updateTime(){
        mTimeFence.interval = TimeSliderUtil.toRuler( mVm.timeInterval )
        mTimeFence.expiration = mTimeToTheFifteenth + mTimeFence.interval
        //mVm.timeExpiration.set( "${TimeSliderUtil.displayTime(mTimeFence.interval)} -> ${mTimeFence.expiration._toLocalDateTime()}" )
        mVm.timeExpiration.set( mTimeFence.expiration )
    }

    private fun updateRadius() {
        mGeoFence.radius = GeoSliderUtil.toRuler( mVm.radius ).toLong()
    }
}