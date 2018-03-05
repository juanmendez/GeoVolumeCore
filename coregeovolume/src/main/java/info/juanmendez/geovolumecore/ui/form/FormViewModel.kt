package info.juanmendez.geovolumecore.ui.form

import android.databinding.*
import info.juanmendez.geovolumecore.BR
import info.juanmendez.geovolumecore.types.fence.GeoSliderUtil
import info.juanmendez.geovolumecore.types.fence.TimeSliderUtil
import info.juanmendez.geovolumecore.types.fence.models.GeoFence
import info.juanmendez.geovolumecore.types.fence.models.HeadphoneFence
import info.juanmendez.geovolumecore.types.fence.models.TimeFence
import info.juanmendez.geovolumecore.types.fence.models.VolumeFence
import info.juanmendez.geovolumecore.types.location.models.Location

/**
 * Created by @juanmendezinfo on 2/3/18.
 */
class FormViewModel( private  val volumeFence:VolumeFence):BaseObservable() {
    private var mGeoFence: GeoFence = volumeFence.geoFence
    private var mHeadphoneFence: HeadphoneFence = volumeFence.headphoneFence
    private var mTimeFence: TimeFence = volumeFence.timeFence

    var isOnline:ObservableBoolean= ObservableBoolean(true)
    var hasLocationPermission:ObservableBoolean = ObservableBoolean(false)
    val isVolumeFenceValid:ObservableBoolean = ObservableBoolean(false)
    val isVolumeFenceActive = ObservableBoolean()

    //<editor-fold desc="~TimeFence">

    private var _timeInterval:Int = TimeSliderUtil.toSlider(mTimeFence.interval)
    var timeInterval:Int
    @Bindable get() = _timeInterval
    set(value) {
        _timeInterval = value
        notifyPropertyChanged( BR.timeInterval )
    }

    var timeExpiration = ObservableField<Long>()
    val isTimeFenceActive = ObservableBoolean(false)

    //</editor-fold>

    //<editor-fold desc="~GeoFence">
    var location: Location
    @Bindable get() = mGeoFence.location
    set(value) {
        mGeoFence.location = value
        notifyPropertyChanged( BR.location)
    }

    private var _radius:Int = GeoSliderUtil.toSlider( mGeoFence.radius.toInt() )
    var radius:Int
        @Bindable get() = _radius
        set(value) {
            _radius = value
            notifyPropertyChanged( BR.radius )
        }
    val isGeoFenceActive = ObservableBoolean(false)
    //</editor-fold>

    //<editor-fold desc="~HeadphoneFence">
    val isHeadphoneFenceActive = ObservableBoolean( false )
    //</editor-fold>

    fun getObservablesWatched():MutableList<Observable> =
            mutableListOf(this, isOnline, timeExpiration, isTimeFenceActive, isGeoFenceActive, isHeadphoneFenceActive, isVolumeFenceValid, isVolumeFenceActive )
}